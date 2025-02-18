package it.unisannio.studenti.qualitag.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the user views.
 */
@Controller
@RequestMapping("/artifact")
@RequiredArgsConstructor
public class ArtifactViewController {

  /**
   * Returns the create team view.
   *
   * @param projectId the project id where the artifact will be linked
   * @return the create team view
   */
  @GetMapping("/{projectId}/create")
  public String createArtifact(@PathVariable("projectId") String projectId, Model model) {
    model.addAttribute("projectId", projectId);
    return "artifact/upload_artifact";
  }

  /**
   * Returns the view for the artifact.
   *
   * @param artifactId the artifact id
   * @return the view for tagging the artifact
   */
  @GetMapping("/{artifactId}/tag/{projectOwnerUsername}")
  public String viewArtifact(@PathVariable("artifactId") String artifactId,
      @PathVariable String projectOwnerUsername, Model model) {
    /*WholeArtifactDto artifactDto = ArtifactMapper.toWholeArtifactDto(
        artifactRepository.findById(artifactId).orElseThrow(NoSuchElementException::new));
    model.addAttribute("artifact", artifactDto);*/
    model.addAttribute("ownerUsername", projectOwnerUsername);
    return "artifact/artifact_details";
  }

}
