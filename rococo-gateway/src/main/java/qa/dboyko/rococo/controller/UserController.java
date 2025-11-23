package qa.dboyko.rococo.controller;

import com.dboyko.rococo.grpc.GetUserResponse;
import com.dboyko.rococo.grpc.UpdateUserResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import qa.dboyko.rococo.client.UserDataClient;
import qa.dboyko.rococo.model.UserdataJson;

import static qa.dboyko.rococo.model.UserdataJson.fromGrpcMessage;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserDataClient userDataClient;

    @PostMapping("/{id}")
    public Boolean updateUser(@PathVariable String id,
                                         @RequestParam String name) {
        return userDataClient.updateUser(id, name).getSuccess();
    }

    @GetMapping
    public UserdataJson currentUser(@AuthenticationPrincipal Jwt principal) {
        final String principalUsername = principal.getClaim("sub");
        return fromGrpcMessage(userDataClient.getUser(principalUsername));
    }
}

