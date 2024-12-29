package it.unisannio.studenti.qualitag;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The QualitagApplication class is the entry point of the Qualitag application.
 */
@SpringBootApplication
public class QualitagApplication {

  /**
   * The main method is the entry point of the Qualitag application.
   *
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure().load();
    System.setProperty("KEYSTORE_PASSWORD", dotenv.get("KEYSTORE_PASSWORD"));

    SpringApplication.run(QualitagApplication.class, args);
  }
}
