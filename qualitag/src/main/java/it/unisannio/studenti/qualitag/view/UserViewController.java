package it.unisannio.studenti.qualitag.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserViewController {

  @GetMapping("/{username}")
  public String profile(@PathVariable("username") String username, Model model) {
    model.addAttribute("username", username);
    return "user/profile";
  }

  @GetMapping("/{username}/update")
  public String update(@PathVariable("username") String username, Model model) {
    model.addAttribute("username", username);
    return "user/update_user";
  }
}
