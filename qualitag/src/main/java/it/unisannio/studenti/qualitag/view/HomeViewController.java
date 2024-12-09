package it.unisannio.studenti.qualitag.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeViewController {

  @GetMapping("/")
  public String home() {
    return "home";
  }

  @GetMapping("/404")
  public String error404() {
    return "404";
  }
}
