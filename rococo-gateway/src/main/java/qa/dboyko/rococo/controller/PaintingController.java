package qa.dboyko.rococo.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import qa.dboyko.rococo.model.*;
import qa.dboyko.rococo.service.PaintingClient;

@RestController
@RequestMapping("/api/painting")
public class PaintingController {
    private static final Logger LOG = LoggerFactory.getLogger(PaintingController.class);

    @Autowired
    private PaintingClient paintingClient;

    @PatchMapping()
    public PaintingJson updatePainting(@Valid @RequestBody PaintingJson painting,
                                       @AuthenticationPrincipal Jwt principal) {
        return paintingClient.updatePainting(painting);
    }

    @GetMapping("/{id}")
    public PaintingJson getPainting(@PathVariable String id) {
        LOG.info("!!! call to get painting {}", id);
        return paintingClient.getPainting(id);
    }

    @GetMapping("/author/{id}")
    public Page<PaintingJson> getPaintingsForArtist(@PathVariable String id,
                                                   Pageable pageable) {
        LOG.info("!!! call to get paintings for artist {}", id);
        Page<PaintingJson> allPaintingsPage = paintingClient.getPaintingsForArtist(pageable, id);

        return new PageImpl<>(
                allPaintingsPage.stream().toList(),
                pageable,
                allPaintingsPage.getTotalElements()
        );
    }

    @PostMapping
    public PaintingJson createPainting(@Valid @RequestBody PaintingJson painting,
                                   @AuthenticationPrincipal Jwt principal){
        return paintingClient.createPainting(painting);
    }

    @GetMapping
    public Page<PaintingJson> allPaintings(@RequestParam(required = false) String title,
                                           @PageableDefault Pageable pageable) {
        Page<PaintingJson> allPaintingsPage = paintingClient.allPaintings(pageable, title);

        return new PageImpl<>(
                allPaintingsPage.stream().toList(),
                pageable,
                allPaintingsPage.getTotalElements()
        );
    }
}

