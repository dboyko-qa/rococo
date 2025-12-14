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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import qa.dboyko.rococo.model.MuseumJson;
import qa.dboyko.rococo.service.MuseumClient;

@RestController
@RequestMapping("/api/museum")
@Validated
public class MuseumController {
    private static final Logger LOG = LoggerFactory.getLogger(MuseumController.class);

    @Autowired
    private MuseumClient museumClient;

    @PatchMapping
    public MuseumJson updateMuseum(@Valid @RequestBody MuseumJson museum,
                                   @AuthenticationPrincipal Jwt principal) {
        LOG.info("!!! call to update museum {}", museum.id());
        return museumClient.updateMuseum(museum);
    }

    @GetMapping("/{id}")
    public MuseumJson getMuseum(@PathVariable String id) {
        LOG.info("!!! call to get museum {}", id);
        return museumClient.getMuseum(id);
    }

    @PostMapping
    public MuseumJson createMuseum(@Valid @RequestBody MuseumJson museum,
                                   @AuthenticationPrincipal Jwt principal){
        LOG.info("!!! call to create museum {}", museum.title());
        return museumClient.createMuseum(museum);
    }

    @GetMapping
    public Page<MuseumJson> allMuseums(@RequestParam(required = false) String title,
                                       @PageableDefault Pageable pageable) {
        LOG.info("!!! call to get museums list {}", pageable.getPageNumber());
        Page<MuseumJson> allMuseumsPage = museumClient.allMuseums(pageable, title);

        return new PageImpl<>(
                allMuseumsPage.stream().toList(),
                pageable,
                allMuseumsPage.getTotalElements()
        );
    }
}

