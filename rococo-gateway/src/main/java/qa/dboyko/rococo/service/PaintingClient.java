package qa.dboyko.rococo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import qa.dboyko.rococo.model.PaintingJson;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface PaintingClient {
    PaintingJson getPainting(String id);

    Page<PaintingJson> allPaintings(Pageable pageable, String titleFilter);
    Page<PaintingJson> getPaintingsForArtist(Pageable pageable, String artistId);

    PaintingJson createPainting(PaintingJson paintingJson);

    PaintingJson updatePainting(PaintingJson paintingJson);
}
