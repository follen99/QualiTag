package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
import it.unisannio.studenti.qualitag.service.ProjectService;
import it.unisannio.studenti.qualitag.model.Project;


/**
 * Mapper for the Project entity.
 * Provides methods to convert between Project entities and ProjectDTO.
 */
public class ProjectMapper {

  private final ProjectService projectService;

  /**
   * Constructs a new ProjectMapper
   *
   * @param projectService The project service to use
   */
  public ProjectMapper(ProjectService projectService) {
    this.projectService = projectService;
  }

  /**
   * Converts a ProjectCreationDto to a Project entity
   *
   * @param dto The ProjectCreationDto to convert
   * @return The converted Project entity
   */
  public Project toEntity(ProjectCreateDto dto) {
    if (dto == null) {
      return null;
    }
    return new Project(
        dto.projectName(),
        dto.projectDescription(),
        dto.deadlineDate(),
        dto.userIds(),
        dto.teamIds(),
        dto.artifactIds(),
        dto.ownerId()
    );
  }

  public ProjectCreateDto toDto(Project entity) {
    if (entity == null) {
      return null;
    }
    return new ProjectCreateDto(
        entity.getProjectName(),
        entity.getProjectDescription(),
        entity.getProjectCreationDate(),
        entity.getProjectDeadline(),
        entity.getUserIds(),
        entity.getTeamIds(),
        entity.getArtifactIds(),
        entity.getOwnerId()
    );
  }


}
