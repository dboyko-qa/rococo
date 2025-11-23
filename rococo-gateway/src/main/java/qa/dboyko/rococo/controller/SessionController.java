package qa.dboyko.rococo.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qa.dboyko.rococo.model.SessionJson;

import java.util.Date;


@RestController
@RequestMapping("/api/session")
public class SessionController {

  @GetMapping("/current")
  public SessionJson session(@AuthenticationPrincipal Jwt principal) {
    if (principal != null) {
      return new SessionJson(
          principal.getClaim("sub"),
          Date.from(principal.getIssuedAt()),
          Date.from(principal.getExpiresAt())
      );
    } else {
      return SessionJson.empty();
    }
  }
}
