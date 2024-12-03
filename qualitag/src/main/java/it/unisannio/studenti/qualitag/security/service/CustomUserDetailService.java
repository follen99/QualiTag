package it.unisannio.studenti.qualitag.security.service;

import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service class for CustomUserDetails.
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Creates a new CustomUserDetailService.
   *
   * @param userRepository the repository for the User
   */
  public CustomUserDetailService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    User user = userRepository.findByUsernameOrEmail(identifier, identifier);
    if (user == null) {
      throw new UsernameNotFoundException("User not found with username or email: " + identifier);
    }
    return new CustomUserDetails(user);
  }
}
