package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.service.ArtifactService;

/**
 * Mapper for the Artifact entity. Provides methods to convert between Artifact entities and
 * ArtifactDTO.
 */
public class ArtifactMapper {

  private final ArtifactService artifactService;

  /**
   * Constructs a new ArtifactMapper.
   *
   * @param artifactService the artifact service
   */
  public ArtifactMapper(ArtifactService artifactService) {
    this.artifactService = artifactService;
  }

  /**
   * Converts a ArtifactCreationDto to an Artifact entity.
   *
   * @param dto The ArtifactCreationDto to convert
   * @return The converted Artifact entity
   */
  public Artifact toEntity(ArtifactCreateDto dto) {
    if (dto == null) {
      return null;
    }
    return new Artifact(
        dto.artifactName(),
        dto.content(),
        dto.tags()
    );
  }

  /**
   * Converts an Artifact entity to a ArtifactCreationDto.
   *
   * @param entity The Artifact entity to convert
   * @return The converted ArtifactCreationDto
   */
  public ArtifactCreateDto toDto(Artifact entity) {
    if (entity == null) {
      return null;
    }
    return new ArtifactCreateDto(
        entity.getArtifactName(),
        entity.getContent(),
        entity.getTags()
    );
  }
}
