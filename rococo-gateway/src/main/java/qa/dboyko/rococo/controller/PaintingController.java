package qa.dboyko.rococo.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import qa.dboyko.rococo.model.*;
import qa.dboyko.rococo.service.ArtistClient;
import qa.dboyko.rococo.util.GrpcImpl;

import java.util.List;

@RestController
@RequestMapping("/api/painting")
public class PaintingController {
    private static final Logger LOG = LoggerFactory.getLogger(PaintingController.class);

    @Autowired
    @GrpcImpl
    private ArtistClient artistClient;

//    @PostMapping("/{id}")
//    public Boolean updateUser(@PathVariable String id) {
//        return artistClient.getArtist(id, name).getSuccess();
//    }

    @GetMapping("/{id}")
    public PaintingJson getPainting(@PathVariable String id) {
        LOG.info("!!! call to get painting {}", id);
        return new PaintingJson("","","","",
                new MuseumJson("", "", "", "",
                        new GeoJson("",
                                new CountryJson("", ""))));
//        return artistClient.getArtist(id);
    }

    @GetMapping("/author/{id}")
    public Page<PaintingJson> getPaintingForArtist(@PathVariable String id,
                                                   Pageable pageable) {
        LOG.info("!!! call to get paintings for artist {}", id);
        List<PaintingJson> listPainting = List.of(new PaintingJson("","","","",
                new MuseumJson("", "", "", "",
                        new GeoJson("",
                                new CountryJson("", "")))));
        Page<PaintingJson> pagePainting = Page.empty();
        return new PageImpl<>(
                pagePainting.stream().toList(),
                pageable,
                pagePainting.getTotalElements()
        );
//        return artistClient.getArtist(id);
    }

    @PostMapping
    public ArtistJson createArtist(@Valid @RequestBody ArtistJson artist,
                                   @AuthenticationPrincipal Jwt principal){
        return artistClient.createArtist(artist);
    }

    @GetMapping
    public Page<ArtistJson> allArtists(Pageable pageable) {
        Page<ArtistJson> allArtistsPage = artistClient.allArtists(pageable);

        return new PageImpl<>(
                allArtistsPage.stream().toList(),
                pageable,
                allArtistsPage.getTotalElements()
        );
    }
}

