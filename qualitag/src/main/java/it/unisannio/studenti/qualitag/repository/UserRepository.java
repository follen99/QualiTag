package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for the User entity.
 */
public interface UserRepository extends MongoRepository<User, String> {

  /**
   * Checks if a user with the given username exists.
   *
   * @param username The username to check.
   * @return True if a user with the given username exists, false otherwise.
   */
  Boolean existsByUsername(String username);

  /**
   * Checks if a user with the given email exists.
   *
   * @param email The email to check.
   * @return True if a user with the given email exists, false otherwise.
   */
  Boolean existsByEmail(String email);

  /**
   * Finds a user by its email or username.
   *
   * @param username The username of the user to find.
   * @param email    The email of the user to find.
   * @return User with the given email or username.
   */
  User findByUsernameOrEmail(String username, String email);

  /**
   * Finds a user by its username.
   *
   * @param username The username of the user to find.
   * @return User with the given username.
   */
  User findByUsername(String username);

  /**
   * Finds a user by its email.
   *
   * @param email The email of the user to find.
   * @return User with the given email.
   */
  User findByEmail(String email);

  /**
   * Deletes a user by its username.
   *
   * @param username The username of the user to delete.
   */
  void deleteByUsername(String username);


}
