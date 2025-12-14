package qa.dboyko.rococo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import qa.dboyko.rococo.model.ArtistJson;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ArtistClient {
    ArtistJson getArtist(String id);

    Page<ArtistJson> allArtists(Pageable pageable, String nameFilter);

    ArtistJson createArtist(ArtistJson artistJson);

    ArtistJson updateArtist(ArtistJson artistJson);
}
