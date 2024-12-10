package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.project.ProjectCreateDto;
import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.service.ProjectService;


/**
 * Mapper for the Project entity.
 * Provides methods to convert between Project entities and ProjectDTO.
 */
public class ProjectMapper {

  private final ProjectService projectService;

  /**
   * Constructs a new ProjectMapper.
   *
   * @param projectService The project service to use
   */
  public ProjectMapper(ProjectService projectService) {
    this.projectService = projectService;
  }

  /**
   * Converts a ProjectCreationDto to a Project entity.
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
        dto.users(),
        dto.teams(),
        dto.artifacts(),
        dto.ownerId()
    );
  }

  /**
   * Converts a Project entity to a ProjectCreationDto.
   *
   * @param entity The Project entity to convert
   * @return The converted ProjectCreationDto
   */
  public ProjectCreateDto toDto(Project entity) {
    if (entity == null) {
      return null;
    }
    return new ProjectCreateDto(
        entity.getProjectName(),
        entity.getProjectDescription(),
        entity.getProjectCreationDate(),
        entity.getProjectDeadline(),
        entity.getUsers(),
        entity.getTeams(),
        entity.getArtifacts(),
        entity.getOwnerId()
    );
  }


}
