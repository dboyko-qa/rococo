package qa.dboyko.rococo.controller;

import com.dboyko.rococo.grpc.Artist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import qa.dboyko.rococo.model.ArtistJson;
import qa.dboyko.rococo.service.ArtistClient;

import java.util.List;

import static qa.dboyko.rococo.model.UserdataJson.fromGrpcMessage;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    @Autowired
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

