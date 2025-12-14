package qa.dboyko.rococo.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import qa.dboyko.rococo.model.UserdataJson;
import qa.dboyko.rococo.service.UserClient;

import static qa.dboyko.rococo.model.UserdataJson.fromGrpcMessage;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserClient userClient;

    @PatchMapping()
    public UserdataJson updateUser(@AuthenticationPrincipal Jwt principal,
                              @Valid @RequestBody UserdataJson user) {
        final String principalUsername = principal.getClaim("sub");
        return fromGrpcMessage(userClient.updateUser(user));
    }

    @GetMapping
    public UserdataJson currentUser(@AuthenticationPrincipal Jwt principal) {
        final String principalUsername = principal.getClaim("sub");
        return fromGrpcMessage(userClient.getUser(principalUsername));
    }
}

