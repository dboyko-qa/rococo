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
import qa.dboyko.rococo.model.CountryJson;
import qa.dboyko.rococo.model.GeoJson;
import qa.dboyko.rococo.model.MuseumJson;
import qa.dboyko.rococo.service.MuseumClient;

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
class MuseumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MuseumClient museumClient;

    @Test
    @DisplayName("Should return a paged list of museums when requesting all museums without authentication")
    void shouldReturnPagedMuseumsWhenRequestingAllMuseums() throws Exception {
        // Arrange
        MuseumJson museum = sampleMuseum();
        Page<MuseumJson> page = new PageImpl<>(List.of(museum));
        given(museumClient.allMuseums(any(Pageable.class), any())).willReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/museum")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(museum.id()))
                .andExpect(jsonPath("$.content[0].title").value(museum.title()));
    }

    @Test
    @DisplayName("Should return museum details when requesting an existing museum by ID")
    void shouldReturnMuseumWhenMuseumExists() throws Exception {
        // Arrange
        MuseumJson museum = sampleMuseum();
        given(museumClient.getMuseum("1")).willReturn(museum);

        // Act & Assert
        mockMvc.perform(get("/api/museum/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Louvre"));
    }

    @Test
    @DisplayName("Should create a new museum when a valid JWT is provided")
    void shouldCreateMuseumWhenJwtIsProvided() throws Exception {
        // Arrange
        MuseumJson museum = sampleMuseum();
        given(museumClient.createMuseum(any())).willReturn(museum);

        String requestBody = """
                {
                  "title": "Louvre",
                  "description": "Famous museum in Paris",
                  "photo": "",
                  "geo": {
                    "city": "Paris",
                    "country": {
                      "id": "FR",
                      "name": "France"
                    }
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/museum")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Louvre"));
    }

    @Test
    @DisplayName("Should update an existing museum when a valid JWT is provided")
    void shouldUpdateMuseumWhenJwtIsProvided() throws Exception {
        // Arrange
        MuseumJson updatedMuseum = new MuseumJson(
                "1",
                "Updated Museum",
                "Updated description",
                "",
                new GeoJson("Paris", new CountryJson("FR", "France"))
        );
        given(museumClient.updateMuseum(any())).willReturn(updatedMuseum);

        String requestBody = """
                {
                  "id": "1",
                  "title": "Updated Museum",
                  "description": "Updated description",
                  "photo": "",
                  "geo": {
                    "city": "Paris",
                    "country": {
                      "id": "FR",
                      "name": "France"
                    }
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(patch("/api/museum")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Updated Museum"));
    }

    private MuseumJson sampleMuseum() {
        return new MuseumJson(
                "1",
                "Louvre",
                "Famous museum in Paris",
                "",
                new GeoJson(
                        "Paris",
                        new CountryJson("FR", "France")
                )
        );
    }
}
