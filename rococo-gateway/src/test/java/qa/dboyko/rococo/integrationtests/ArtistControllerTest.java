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
import qa.dboyko.rococo.model.ArtistJson;
import qa.dboyko.rococo.service.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestClientConfig.class)
class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistClient artistClient;

    @Test
    @DisplayName("Should return a paged list of artists when requesting all artists without authentication")
    void shouldReturnPagedArtistsWhenRequestingAllArtists() throws Exception {
        // Arrange
        Page<ArtistJson> samplePage = new PageImpl<>(List.of(
                new ArtistJson("1", "ArtistName", "Biography", null)
        ));
        given(artistClient.allArtists(any(Pageable.class), any())).willReturn(samplePage);

        // Act & Assert
        mockMvc.perform(get("/api/artist")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[0].name").value("ArtistName"));
    }

    @Test
    @DisplayName("Should return artist details when requesting an existing artist by ID without authentication")
    void shouldReturnArtistWhenArtistExists() throws Exception {
        // Arrange
        ArtistJson artist = new ArtistJson("1", "ArtistName", "Biography", null);
        given(artistClient.getArtist("1")).willReturn(artist);

        // Act & Assert
        mockMvc.perform(get("/api/artist/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("ArtistName"));
    }

    @Test
    @DisplayName("Should create a new artist when a valid JWT is provided")
    void shouldCreateArtistWhenJwtIsProvided() throws Exception {
        // Arrange
        ArtistJson artist = new ArtistJson("1", "NewArtist", "Bio", null);
        given(artistClient.createArtist(any())).willReturn(artist);

        String requestBody = """
            {
              "name": "NewArtist",
              "biography": "Bio"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/artist")
                        .with(jwt()) // подставляем mock JWT
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("NewArtist"));
    }

    @Test
    @DisplayName("Should update an existing artist when a valid JWT is provided")
    void shouldUpdateArtistWhenJwtIsProvided() throws Exception {
        // Arrange
        ArtistJson updatedArtist = new ArtistJson("1", "UpdatedArtist", "UpdatedBio", null);
        given(artistClient.updateArtist(any())).willReturn(updatedArtist);

        String requestBody = """
            {
              "id": "1",
              "name": "UpdatedArtist",
              "biography": "UpdatedBio"
            }
            """;

        // Act & Assert
        mockMvc.perform(patch("/api/artist")
                        .with(jwt()) // подставляем mock JWT
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("UpdatedArtist"));
    }
}
