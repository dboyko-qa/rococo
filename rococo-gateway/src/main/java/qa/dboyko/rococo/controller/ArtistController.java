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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import qa.dboyko.rococo.model.ArtistJson;
import qa.dboyko.rococo.service.ArtistClient;
import qa.dboyko.rococo.util.GrpcImpl;

@RestController
@RequestMapping("/api/artist")
@Validated
public class ArtistController {
    private static final Logger LOG = LoggerFactory.getLogger(ArtistController.class);

    @Autowired
    @GrpcImpl
    private ArtistClient artistClient;

    @PatchMapping
    public ArtistJson updateArtist(@Valid @RequestBody ArtistJson artist,
                                   @AuthenticationPrincipal Jwt principal) {
        LOG.info("!!! call to update artist {}", artist.id());
        return artistClient.updateArtist(artist);
    }

    @GetMapping("/{id}")
    public ArtistJson getArtist(@PathVariable String id) {
        LOG.info("!!! call to get artist {}", id);
        return artistClient.getArtist(id);
    }

    @PostMapping
    public ArtistJson createArtist(@Valid @RequestBody ArtistJson artist,
                                   @AuthenticationPrincipal Jwt principal){
        LOG.info("!!! call to create artist {}", artist.name());
        return artistClient.createArtist(artist);
    }

    @GetMapping
    public Page<ArtistJson> allArtists(Pageable pageable) {
        LOG.info("!!! call to get artists list {}", pageable.getPageNumber());
        Page<ArtistJson> allArtistsPage = artistClient.allArtists(pageable);

        return new PageImpl<>(
                allArtistsPage.stream().toList(),
                pageable,
                allArtistsPage.getTotalElements()
        );
    }
}

