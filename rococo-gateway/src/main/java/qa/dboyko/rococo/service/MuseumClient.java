package qa.dboyko.rococo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import qa.dboyko.rococo.model.MuseumJson;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MuseumClient {
    MuseumJson getMuseum(String id);

    Page<MuseumJson> allMuseums(Pageable pageable, String nameFilter);

    MuseumJson createMuseum(MuseumJson museumJson);

    MuseumJson updateMuseum(MuseumJson museumJson);
}
