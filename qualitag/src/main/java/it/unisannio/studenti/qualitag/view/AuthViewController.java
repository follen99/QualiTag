package it.unisannio.studenti.qualitag.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

  @GetMapping("/signin")
  public String signin() {
    return "auth/sign_in";
  }

  @GetMapping("/signup")
  public String signup() {
    return "auth/sign_up";
  }
}
