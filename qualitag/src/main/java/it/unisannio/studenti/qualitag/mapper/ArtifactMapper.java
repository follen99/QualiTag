package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.dto.artifact.WholeArtifactDto;
import it.unisannio.studenti.qualitag.model.Artifact;

/**
 * Mapper for the Artifact entity. Provides methods to convert between Artifact entities and
 * ArtifactDTO.
 */
public class ArtifactMapper {

  /**
   * Converts an ArtifactCreateDto to an Artifact entity.
   *
   * @param dto the DTO to convert
   * @return the converted entity
   */
  public static Artifact toEntity(ArtifactCreateDto dto) {
    Artifact entity = new Artifact();
    entity.setArtifactName(dto.artifactName());
    entity.setDescription(dto.description());
    entity.setProjectId(dto.projectId());
    entity.setTeamId(dto.teamId());
    return entity;
  }

  /**
   * Converts an Artifact entity to a WholeArtifactDto.
   *
   * @param entity the entity to convert
   * @return the converted DTO
   */
  public static WholeArtifactDto toWholeArtifactDto(Artifact entity) {
    return new WholeArtifactDto(
        entity.getArtifactId(),
        entity.getArtifactName(),
        entity.getDescription(),
        entity.getProjectId(),
        entity.getTeamId(),
        entity.getFilePath(),
        entity.getTags(),
        entity.isTaggingOpen()
    );
  }
}
