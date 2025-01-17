package it.unisannio.studenti.qualitag.dto.artifact;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO used for artifact creation in the system.
 */
public record WholeArtifactDto(
        @NotBlank String artifactId,
        @NotBlank String artifactName,
        String description,
        @NotBlank String projectId,
        @NotBlank String teamId,
        MultipartFile file,
        List<String> tags) {

}


