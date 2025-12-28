package qa.dboyko.rococo.integrationtests;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import qa.dboyko.rococo.service.*;

import static org.mockito.Mockito.mock;

@TestConfiguration
class TestClientConfig {

    @Bean
    public ArtistClient artistClient() {
        return mock(ArtistClient.class);
    }

    @Bean
    public MuseumClient museumClient() {
        return mock(MuseumClient.class);
    }

    @Bean
    public PaintingClient paintingClient() {
        return mock(PaintingClient.class);
    }

    @Bean
    public GeoClient geoClient() {
        return mock(GeoClient.class);
    }

    @Bean
    public UserClient userClient() {
        return mock(UserClient.class);
    }
}

