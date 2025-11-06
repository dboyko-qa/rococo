package qa.dboyko.rococo.controller;

import qa.dboyko.rococo.service.OauthSessionValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class LoginController {

  private static final String LOGIN_VIEW_NAME = "login";

  private final String rococoFrontUri;
  private final OauthSessionValidator sessionValidator;

  public LoginController(@Value("${rococo-front.base-uri}") String rococoFrontUri, OauthSessionValidator sessionValidator) {
    this.rococoFrontUri = rococoFrontUri;
    this.sessionValidator = sessionValidator;
  }

  @GetMapping("/login")
  public String login(HttpSession session) {
    if (sessionValidator.isWebOauthSession(session) || sessionValidator.isAndroidOauthSession(session)) {
      return LOGIN_VIEW_NAME;
    }
    return "redirect:" + rococoFrontUri;
  }

  @GetMapping("/")
  @ResponseStatus(HttpStatus.FOUND)
  public String root(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
      return "redirect:" + rococoFrontUri;
    }
    return "redirect:/" + LOGIN_VIEW_NAME;
  }
}
