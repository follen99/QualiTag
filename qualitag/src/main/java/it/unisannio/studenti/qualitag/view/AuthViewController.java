package it.unisannio.studenti.qualitag.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for the authentication views.
 */
@Controller
public class AuthViewController {

  /**
   * Returns the sign-in view.
   *
   * @return the sign-in view
   */
  @GetMapping("/signin")
  public String signin() {
    return "auth/sign_in";
  }

  /**
   * Returns the sign-up view.
   *
   * @return the sign-up view
   */
  @GetMapping("/signup")
  public String signup() {
    return "auth/sign_up";
  }
}
