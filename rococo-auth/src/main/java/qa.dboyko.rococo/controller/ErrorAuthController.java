package qa.dboyko.rococo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static qa.dboyko.rococo.service.GlobalExceptionHandler.ERROR_MESSAGE_PARAM;
import static qa.dboyko.rococo.service.GlobalExceptionHandler.ORIGINAL_CODE_PARAM;

@Controller
public class ErrorAuthController implements ErrorController {

  private static final String ERROR_VIEW_NAME = "error";
  private static final String REQUEST_EXCEPTION_ATTR = "javax.servlet.error.exception";

  private final String rococoFrontUri;

  public ErrorAuthController(@Value("${rococo-front.base-uri}") String rococoFrontUri) {
    this.rococoFrontUri = rococoFrontUri;
  }

  @GetMapping("/error")
  @ResponseStatus(HttpStatus.OK)
  public String error(HttpServletRequest request,
                      HttpServletResponse response,
                      Model model) {
    final Object originalCode = model.getAttribute(ORIGINAL_CODE_PARAM);
    final Object originalError = model.getAttribute(ERROR_MESSAGE_PARAM);

    final String message;
    int status;

    if (originalCode != null) {
      try {
        status = Integer.parseInt(originalCode.toString());
      } catch (NumberFormatException e) {
        status = response.getStatus();
      }
    } else {
      status = response.getStatus();
    }

    final Throwable throwable = (Throwable) request.getAttribute(REQUEST_EXCEPTION_ATTR);
    if (originalError != null) {
      message = originalError.toString();
    } else if (throwable != null) {
      message = throwable.getMessage();
    } else {
      message = "Unknown error";
    }

    model.addAttribute("status", status);
    model.addAttribute("frontUri", rococoFrontUri + "/main");
    model.addAttribute("error", message);
    return ERROR_VIEW_NAME;
  }
}
