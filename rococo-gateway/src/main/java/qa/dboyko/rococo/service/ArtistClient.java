package qa.dboyko.rococo.service;

import com.dboyko.rococo.grpc.GetArtistResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import qa.dboyko.rococo.model.ArtistJson;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ArtistClient {
    public ArtistJson getArtist(String name);
    public Page<ArtistJson> allArtists(Pageable pageable);
}
