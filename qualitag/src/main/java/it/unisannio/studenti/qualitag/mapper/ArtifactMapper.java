package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.artifact.ArtifactCreateDto;
import it.unisannio.studenti.qualitag.model.Artifact;

/**
 * Mapper for the Artifact entity. Provides methods to convert between Artifact entities and
 * ArtifactDTO.
 */
public class ArtifactMapper {

  // /**
  //  * Converts a ArtifactCreationDto to an Artifact entity.
  //  *
  //  * @param dto The ArtifactCreationDto to convert
  //  * @return The converted Artifact entity
  //  */
  // public static Artifact toEntity(ArtifactCreateDto dto) {
  //   if (dto == null) {
  //     return null;
  //   }
  //   return new Artifact(
  //       dto.artifactName(),
  //       dto.content(),
  //       dto.tags()
  //   );
  // }

  // /**
  //  * Converts an Artifact entity to a ArtifactCreationDto.
  //  *
  //  * @param entity The Artifact entity to convert
  //  * @return The converted ArtifactCreationDto
  //  */
  // public ArtifactCreateDto toDto(Artifact entity) {
  //   if (entity == null) {
  //     return null;
  //   }
  //   return new ArtifactCreateDto(
  //       entity.getArtifactName(),
  //       entity.getContent(),
  //       entity.getTags()
  //   );
  // }
}
