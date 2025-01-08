package it.unisannio.studenti.qualitag;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * The DotenvApplicationRunListener class is a custom listener that loads environment variables from
 * a .env file.
 */
public class DotenvApplicationRunListener implements SpringApplicationRunListener {

  /**
   * The DotenvApplicationRunListener constructor, required by SpringApplicationRunListener.
   *
   * @param application The Spring application.
   * @param args The command line arguments.
   */
  public DotenvApplicationRunListener(SpringApplication application, String[] args) {
    // No implementation needed
  }

  @Override
  public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
      ConfigurableEnvironment environment) {
    Dotenv dotenv = Dotenv.configure()
        .directory("../")
        .load();

    for (DotenvEntry entry : dotenv.entries()) {
      System.setProperty(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void contextPrepared(ConfigurableApplicationContext context) {
    // No implementation needed
  }

  @Override
  public void contextLoaded(ConfigurableApplicationContext context) {
    // No implementation needed
  }

  @Override
  public void failed(ConfigurableApplicationContext context, Throwable exception) {
    // No implementation needed
  }
}