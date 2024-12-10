package it.unisannio.studenti.qualitag.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for the home view.
 */
@Controller
public class HomeViewController {

  /**
   * Returns the home view.
   *
   * @return the home view
   */
  @GetMapping("/")
  public String home() {
    return "home";
  }

  /**
   * Returns the 404 view.
   *
   * @return the 404 view
   */
  @GetMapping("/404")
  public String error404() {
    return "404";
  }
}
