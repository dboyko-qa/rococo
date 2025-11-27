package qa.dboyko.rococo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import qa.dboyko.rococo.model.ArtistJson;
import qa.dboyko.rococo.service.ArtistClient;
import qa.dboyko.rococo.util.GrpcImpl;

import java.util.List;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    @Autowired
    @GrpcImpl
    private ArtistClient artistClient;

//    @PostMapping("/{id}")
//    public Boolean updateUser(@PathVariable String id) {
//        return artistClient.getArtist(id, name).getSuccess();
//    }

    @GetMapping("/{id}")
    public ArtistJson getArtist(@PathVariable String id) {
        return artistClient.getArtist(id);
    }

    @GetMapping
    public List<ArtistJson> allArtists(@AuthenticationPrincipal Jwt principal,
                                       Pageable pageable) {
        final String principalUsername = principal.getClaim("sub");
        return artistClient.allArtists(pageable).stream().toList();
    }
}

