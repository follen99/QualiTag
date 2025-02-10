package it.unisannio.studenti.qualitag.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service to interact with the Python service.
 */
@Service
public class PythonClientService {

  private final WebClient webClient;

  /**
   * Constructor. It initializes the webClient with the base URL of the Python service.
   *
   * @param baseUrl the base URL of the Python service
   */
  public PythonClientService(@Value("${python.service.base-url}") String baseUrl) {
    this.webClient = WebClient.create(baseUrl);
  }

  /**
   * Calls the Python service to get the Krippendorff's alpha value.
   *
   * @param input the input data
   * @return the Krippendorff's alpha value
   */
  public String getKrippendorffAlpha(List<List<List<String>>> input) {
    return webClient.post()
        .uri("/api/krippendorff")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(input)
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  /**
   * Calls the Python service to process the tags.
   *
   * @param tags the tags to process
   * @return the processed tags
   */
  public String processTags(List<String> tags) {
    return webClient.post()
        .uri("/api/process-tags")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(tags)
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }
}
