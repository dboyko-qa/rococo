package qa.dboyko.rococo.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import qa.dboyko.rococo.model.CountryJson;
import qa.dboyko.rococo.service.GeoClient;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestClientConfig.class)
class GeoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GeoClient geoClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return country by id when country exists")
    void shouldReturnCountryWhenCountryExists() throws Exception {
        // arrange
        CountryJson country = new CountryJson(
                "cf412371-47d7-41ad-9f33-36d8ece49a35",
                "France"
        );

        when(geoClient.getCountry(country.id())).thenReturn(country);

        // act & assert
        mockMvc.perform(get("/api/country/{id}", country.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(country.id()))
                .andExpect(jsonPath("$.name").value(country.name()));
    }

    @Test
    @DisplayName("Should return paged list of countries")
    void shouldReturnPagedCountries() throws Exception {
        // arrange
        PageRequest pageable = PageRequest.of(0, 10);

        List<CountryJson> countries = List.of(
                new CountryJson("1", "France"),
                new CountryJson("2", "Germany")
        );

        when(geoClient.allCountries(pageable))
                .thenReturn(new PageImpl<>(countries, pageable, countries.size()));

        // act & assert
        mockMvc.perform(get("/api/country")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("France"))
                .andExpect(jsonPath("$.content[1].name").value("Germany"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("Should allow access to get country endpoint without authentication")
    void shouldAllowAnonymousAccessToGetCountry() throws Exception {
        // arrange
        CountryJson country = new CountryJson("1", "Spain");
        when(geoClient.getCountry("1")).thenReturn(country);

        // act & assert
        mockMvc.perform(get("/api/country/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should allow access to get all countries endpoint without authentication")
    void shouldAllowAnonymousAccessToGetAllCountries() throws Exception {
        // arrange
        PageRequest pageable = PageRequest.of(0, 5);

        when(geoClient.allCountries(pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        // act & assert
        mockMvc.perform(get("/api/country")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }
}
