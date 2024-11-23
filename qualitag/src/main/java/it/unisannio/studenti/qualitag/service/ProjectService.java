package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.dto.user.ProjectCreationDto;
import it.unisannio.studenti.qualitag.mapper.ProjectMapper;
import it.unisannio.studenti.qualitag.model.Project;
import it.unisannio.studenti.qualitag.repository.ProjectRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * the ProjectService class is a service class that provides methods to manage the project entity
 */
@Service
public class ProjectService {
  private static final int LOG_ROUNDS = 12;

  private final ProjectRepository projectRepository;
  private final ProjectMapper projectMapper;

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  /**
   * Constructs a new ProjectService
   * @param projectRepository the project repository
   */
  public ProjectService(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
    this.projectMapper = new ProjectMapper(this);
  }

  //DTO validation
  public boolean isValidProjectCreation(ProjectCreationDto projectCreationDto) {
    Set<ConstraintViolation<ProjectCreationDto>> violations = validator.validate(
        projectCreationDto);

    return violations.isEmpty();
  }

  //Checks if the deadline date is valid
  public boolean isValidDeadlineDate(ProjectCreationDto projectCreationDto) {
    LocalDate localDate = LocalDate.of(2030, 12, 31);
    Date maxDeadline= Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    return projectCreationDto.deadlineDate().after(projectCreationDto.creationDate())
        && projectCreationDto.deadlineDate().before(maxDeadline);
  }

  /**
   * Creates a new project
   *
   * @param projectCreationDto the project creation data
   * @return the response entity with the result of the project creation
   */
  public ResponseEntity<?> createProject(ProjectCreationDto projectCreationDto) {
    if (!isValidProjectCreation(projectCreationDto)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields must be filled");
    }

    if (!isValidDeadlineDate(projectCreationDto)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid deadline date");
    }

    //Create project
    assert projectMapper != null;
    Project project = projectMapper.toEntity(projectCreationDto);

    projectRepository.save(project);

    return ResponseEntity.status(HttpStatus.CREATED).body("Project created successfully");
  }

  public ResponseEntity<?> getAllProjects() {
    return ResponseEntity.status(HttpStatus.OK).body(projectRepository.findAll());
  }
}
