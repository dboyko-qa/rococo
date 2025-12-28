package qa.dboyko.rococo.integrationtests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import qa.dboyko.rococo.model.*;
import qa.dboyko.rococo.service.PaintingClient;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestClientConfig.class)
class PaintingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaintingClient paintingClient;

    private PaintingJson samplePainting() {
        return new PaintingJson(
                "1",
                "Starry Night",
                "Famous painting",
                "base64content",
                new MuseumJson(
                        "10",
                        "Louvre",
                        "Museum description",
                        null,
                        new GeoJson(
                                "Paris",
                                new CountryJson("FR", "France")
                        )
                ),
                new ArtistJson(
                        "20",
                        "Van Gogh",
                        "Biography",
                        null
                )
        );
    }

    @Test
    @DisplayName("Should return a painting by id when painting exists")
    void shouldReturnPaintingById() throws Exception {
        // Arrange
        PaintingJson painting = samplePainting();
        given(paintingClient.getPainting("1")).willReturn(painting);

        // Act & Assert
        mockMvc.perform(get("/api/painting/{id}", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Starry Night"))
                .andExpect(jsonPath("$.museum.id").value("10"))
                .andExpect(jsonPath("$.artist.id").value("20"));
    }

    @Test
    @DisplayName("Should return paged list of paintings without authentication")
    void shouldReturnAllPaintings() throws Exception {
        // Arrange
        Page<PaintingJson> page = new PageImpl<>(List.of(samplePainting()));
        given(paintingClient.allPaintings(any(Pageable.class), eq(null))).willReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/painting")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[0].title").value("Starry Night"));
    }

    @Test
    @DisplayName("Should return paged list of paintings for artist")
    void shouldReturnPaintingsForArtist() throws Exception {
        // Arrange
        Page<PaintingJson> page = new PageImpl<>(List.of(samplePainting()));
        given(paintingClient.getPaintingsForArtist(any(Pageable.class), eq("20")))
                .willReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/painting/author/{id}", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].artist.id").value("20"))
                .andExpect(jsonPath("$.content[0].title").value("Starry Night"));
    }

    @Test
    @DisplayName("Should create a new painting when JWT is provided")
    void shouldCreatePaintingWhenJwtIsProvided() throws Exception {
        // Arrange
        PaintingJson createdPainting = samplePainting();
        given(paintingClient.createPainting(any())).willReturn(createdPainting);

        String requestBody = """
            {
              "title": "Starry Night",
              "description": "Famous painting",
              "content": null,
              "museum": { "id": "10" },
              "artist": { "id": "20" }
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/painting")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Starry Night"));
    }

    @Test
    @DisplayName("Should update an existing painting when JWT is provided")
    void shouldUpdatePaintingWhenJwtIsProvided() throws Exception {
        // Arrange
        PaintingJson updatedPainting = samplePainting();
        given(paintingClient.updatePainting(any())).willReturn(updatedPainting);

        String requestBody = """
            {
              "id": "1",
              "title": "Starry Night",
              "description": "Updated description",
              "content": null,
              "museum": { "id": "10" },
              "artist": { "id": "20" }
            }
            """;

        // Act & Assert
        mockMvc.perform(patch("/api/painting")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Starry Night"));
    }
}

