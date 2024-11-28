package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.ArtifactCreationDto;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.service.ArtifactService;

/**
 * Mapper for the Artifact entity.
 * Provides methods to convert between Artifact entites and ArtifactDTO.
 */
public class ArtifactMapper {
   private final ArtifactService artifactService;


   /**
    * Constructs a new ArtifactMapper
    *
    * @param artifactService the artifact service
    */
  public ArtifactMapper(ArtifactService artifactService) {
    this.artifactService = artifactService;
  }

  /**
   * Converts a ArtifactCreationDto to a Artifact entity
   *
   * @param dto The ArtifactCreationDto to convert
   * @return The converted Artifact entity
   */
  public Artifact toEntity(ArtifactCreationDto dto) {
    if (dto == null) {
      return null;
    }
    return new Artifact(
        dto.artifactName(),
        dto.content()
    );
  }

  /**
   * Converts a Artifact entity to a ArtifactCreationDto
   *
   * @param entity The Artifact entity to convert
   * @return The converted ArtifactCreationDto
   */
  public ArtifactCreationDto toDto(Artifact entity) {
    if (entity == null) {
      return null;
    }
    return new ArtifactCreationDto(
        entity.getArtifactName(),
        entity.getContent()
    );
  }


}
