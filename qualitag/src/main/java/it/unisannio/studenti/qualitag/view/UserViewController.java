package it.unisannio.studenti.qualitag.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the user views.
 */
@Controller
@RequestMapping("/user")
public class UserViewController {

  /**
   * Returns the profile view.
   *
   * @param username The username of the user.
   * @param model    The model.
   * @return the profile view
   */
  @GetMapping("/{username}")
  public String profile(@PathVariable("username") String username, Model model) {
    model.addAttribute("username", username);
    return "user/profile";
  }

  /**
   * Returns the update user view.
   *
   * @param username The username of the user.
   * @param model    The model.
   * @return the update user view
   */
  @GetMapping("/{username}/update")
  public String update(@PathVariable("username") String username, Model model) {
    model.addAttribute("username", username);
    return "user/update_user";
  }

  /**
   * Returns the update password view.
   *
   * @param username The username of the user.
   * @param model    The model.
   * @return the update password view
   */
  @GetMapping("/{username}/password")
  public String updatePassword(@PathVariable("username") String username, Model model) {
    model.addAttribute("username", username);
    return "user/update_password";
  }
}
