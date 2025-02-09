package it.unisannio.studenti.qualitag.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unisannio.studenti.qualitag.dto.project.ProjectInfoDto;
import it.unisannio.studenti.qualitag.dto.project.WholeProjectDto;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for the Project model.
 */
public class ProjectTest {

  private Project project;
  private Project equalProject;
  private Project differentProject;

  /**
   * Sets up a project for the tests.
   */
  @BeforeEach
  public void setUp() {
    project = new Project("projectName",
        "projectDescription", 0L, 0L,
        "ownerId", new ArrayList<>());
    equalProject = new Project("projectName", "projectDescription", 0L, 0L, "ownerId",
        new ArrayList<>());
    differentProject = new Project("differentProjectName", "differentProjectDescription", 1L, 1L,
        "differentOwnerId",
        new ArrayList<>());
    differentProject.setProjectId("differentProjectId");
  }

  /**
   * Test the constructor with no users, teams or artifacts.
   */
  @Test
  public void testConstructor() {
    assertEquals("projectName", project.getProjectName());
    assertEquals("projectDescription", project.getProjectDescription());
    assertEquals(0L, project.getProjectCreationDate());
    assertEquals(0L, project.getProjectDeadline());
    assertEquals("ownerId", project.getOwnerId());
    assertTrue(project.getUserIds().isEmpty());
    assertTrue(project.getTeamIds().isEmpty());
    assertTrue(project.getArtifactIds().isEmpty());
  }

  /**
   * Test the getProjectId method.
   */
  @Test
  public void testGetProjectId() {
    assertNull(project.getProjectId());
  }

  /**
   * Test the getProjectName method.
   */
  @Test
  public void testGetProjectName() {
    assertEquals("projectName", project.getProjectName());
  }

  /**
   * Test the setProjectName method.
   */
  @Test
  public void testSetProjectName() {
    project.setProjectName("newProjectName");
    assertEquals("newProjectName", project.getProjectName());
  }

  /**
   * Test the getProjectDescription method.
   */
  @Test
  public void testGetProjectDescription() {
    assertEquals("projectDescription", project.getProjectDescription());
  }

  /**
   * Test the setProjectDescription method.
   */
  @Test
  public void testSetProjectDescription() {
    project.setProjectDescription("newProjectDescription");
    assertEquals("newProjectDescription", project.getProjectDescription());
  }

  /**
   * Test the getProjectCreationDate method.
   */
  @Test
  public void testGetProjectCreationDate() {
    assertEquals(0L, project.getProjectCreationDate());
  }

  /**
   * Test the setProjectCreationDate method.
   */
  @Test
  public void testSetProjectCreationDate() {
    project.setProjectCreationDate(1L);
    assertEquals(1L, project.getProjectCreationDate());
  }

  /**
   * Test the getProjectDeadline method.
   */
  @Test
  public void testGetProjectDeadline() {
    assertEquals(0L, project.getProjectDeadline());
  }

  /**
   * Test the setProjectDeadline method.
   */
  @Test
  public void testSetProjectDeadline() {
    project.setProjectDeadline(1L);
    assertEquals(1L, project.getProjectDeadline());
  }

  /**
   * Test the getOwnerId method.
   */
  @Test
  public void testGetOwnerId() {
    assertEquals("ownerId", project.getOwnerId());
  }

  /**
   * Test the setOwnerId method.
   */
  @Test
  public void testSetOwnerId() {
    project.setOwnerId("newOwnerId");
    assertEquals("newOwnerId", project.getOwnerId());
  }

  /**
   * Test the getUserIds method.
   */
  @Test
  public void testGetUserIds() {
    assertTrue(project.getUserIds().isEmpty());
  }

  /**
   * Test the addUser method.
   */
  @Test
  public void testAddUser() {
    project.getUserIds().add("userId1");
    assertTrue(project.getUserIds().contains("userId1"));
  }

  /**
   * Test the removeUser method.
   */
  @Test
  public void testRemoveUser() {
    project.getUserIds().add("userId1");
    project.getUserIds().remove("userId1");
    assertFalse(project.getUserIds().contains("userId1"));
  }

  /**
   * Test the getTeamIds method.
   */
  @Test
  public void testGetTeamIds() {
    assertTrue(project.getTeamIds().isEmpty());
  }

  /**
   * Test the addTeam method.
   */
  @Test
  public void testAddTeam() {
    project.getTeamIds().add("teamId1");
    assertTrue(project.getTeamIds().contains("teamId1"));
  }

  /**
   * Test the removeTeam method.
   */
  @Test
  public void testRemoveTeam() {
    project.getTeamIds().add("teamId1");
    project.getTeamIds().remove("teamId1");
    assertFalse(project.getTeamIds().contains("teamId1"));
  }

  /**
   * Test the getArtifactIds method.
   */
  @Test
  public void testGetArtifactIds() {
    assertTrue(project.getArtifactIds().isEmpty());
  }

  /**
   * Test the addArtifact method.
   */
  @Test
  public void testAddArtifact() {
    project.getArtifactIds().add("artifactId1");
    assertTrue(project.getArtifactIds().contains("artifactId1"));
  }

  /**
   * Test the removeArtifact method.
   */
  @Test
  public void testRemoveArtifact() {
    project.getArtifactIds().add("artifactId1");
    project.getArtifactIds().remove("artifactId1");
    assertFalse(project.getArtifactIds().contains("artifactId1"));
  }

  /**
   * Test the equals' method.
   */
  @Test
  public void testEquals() {
    assertTrue(project.equals(equalProject));
  }

  /**
   * Test the equals method with the same object.
   */
  @Test
  public void testEqualsSameObject() {
    assertTrue(project.equals(project));
  }

  /**
   * Test the equals method with null.
   */
  @Test
  public void testEqualsWithNull() {
    assertFalse(project.equals(null));
  }


  /**
   * Test the equals method with a different class.
   */
  @Test
  public void testNotEquals() {
    assertFalse(project.equals("differentProject"));
  }


  /**
   * Test the hashCode method.
   */
  @Test
  public void testHashCode() {
    assertEquals(project.hashCode(), equalProject.hashCode());
  }

  /**
   * Test the hashCode method with different objects.
   */
  @Test
  public void testNotHashCode() {
    assertFalse(project.hashCode() == differentProject.hashCode());
  }

  /**
   * Test the toString method.
   */
  @Test
  public void testToString() {
    String expected = "Project{projectId='null', projectName='projectName', "
        + "projectDescription='projectDescription', projectCreationDate=0, "
        + "projectDeadline=0, ownerId='ownerId', usersIds=[], teamsIds=[], artifactsIds=[]}";
    assertEquals(expected, project.toString());
  }

  /**
   * Tests the toProjectInfoDto method
   */
  @Test
  public void testToProjectInfoDto() {
    ProjectInfoDto projectInfoDto = project.toProjectInfoDto();
    assertEquals(project.getProjectName(), projectInfoDto.projectName());
    assertEquals(project.getProjectDescription(), projectInfoDto.projectDescription());
    assertEquals(project.getProjectStatus().toString(), projectInfoDto.projectStatus());
    assertEquals(project.getProjectId(), projectInfoDto.projectId());
  }

  /**
   * Tests the toProjectInfoDto method with a null projectStatus
   */
  @Test
  public void testToProjectInfoDtoNullProjectStatus() {
    project.setProjectStatus(null);
    ProjectInfoDto projectInfoDto = project.toProjectInfoDto();
    assertEquals(project.getProjectName(), projectInfoDto.projectName());
    assertEquals(project.getProjectDescription(), projectInfoDto.projectDescription());
    assertEquals("NO_INFO", projectInfoDto.projectStatus());
    assertEquals(project.getProjectId(), projectInfoDto.projectId());
  }

  /**
   * Tests the toProjectInfoDto method with a null project name
   */
  @Test
  public void testToProjectInfoDtoNullProjectName() {
    project.setProjectName(null);
    ProjectInfoDto projectInfoDto = project.toProjectInfoDto();
    assertEquals("", projectInfoDto.projectName());
    assertEquals(project.getProjectDescription(), projectInfoDto.projectDescription());
    assertEquals(project.getProjectStatus().toString(), projectInfoDto.projectStatus());
    assertEquals(project.getProjectId(), projectInfoDto.projectId());
  }

  /**
   * Tests the toProjectInfoDto method with a null project description
   */
  @Test
  public void testToProjectInfoDtoNullProjectDescription() {
    project.setProjectDescription(null);
    ProjectInfoDto projectInfoDto = project.toProjectInfoDto();
    assertEquals(project.getProjectName(), projectInfoDto.projectName());
    assertEquals("", projectInfoDto.projectDescription());
    assertEquals(project.getProjectStatus().toString(), projectInfoDto.projectStatus());
    assertEquals(project.getProjectId(), projectInfoDto.projectId());
  }

  /**
   * Tests the toResponseProjectDto method
   */
  @Test
  public void testToResponseProjectDto() {
    WholeProjectDto responseProjectDto = project.toResponseProjectDto();
    assertEquals(project.getProjectName(), responseProjectDto.projectName());
    assertEquals(project.getProjectDescription(), responseProjectDto.projectDescription());
    assertEquals(project.getProjectCreationDate(), responseProjectDto.projectCreationDate());
    assertEquals(project.getProjectDeadline(), responseProjectDto.projectDeadline());
    assertEquals(project.getOwnerId(), responseProjectDto.ownerId());
    assertEquals(project.getUserIds(), responseProjectDto.users());
    assertEquals(project.getTeamIds(), responseProjectDto.teams());
    assertEquals(project.getArtifactIds(), responseProjectDto.artifacts());
  }

  /**
   * Tests the toResponseProjectDto method with a null project name
   */
  @Test
  public void testToResponseProjectDtoNullProjectName() {
    project.setProjectName(null);
    WholeProjectDto responseProjectDto = project.toResponseProjectDto();
    assertEquals("", responseProjectDto.projectName());
    assertEquals(project.getProjectDescription(), responseProjectDto.projectDescription());
    assertEquals(project.getProjectCreationDate(), responseProjectDto.projectCreationDate());
    assertEquals(project.getProjectDeadline(), responseProjectDto.projectDeadline());
    assertEquals(project.getOwnerId(), responseProjectDto.ownerId());
    assertEquals(project.getUserIds(), responseProjectDto.users());
    assertEquals(project.getTeamIds(), responseProjectDto.teams());
    assertEquals(project.getArtifactIds(), responseProjectDto.artifacts());
  }

  /**
   * Tests the toResponseProjectDto method with a null project description
   */
  @Test
  public void testToResponseProjectDtoNullProjectDescription() {
    project.setProjectDescription(null);
    WholeProjectDto responseProjectDto = project.toResponseProjectDto();
    assertEquals(project.getProjectName(), responseProjectDto.projectName());
    assertEquals("", responseProjectDto.projectDescription());
    assertEquals(project.getProjectCreationDate(), responseProjectDto.projectCreationDate());
    assertEquals(project.getProjectDeadline(), responseProjectDto.projectDeadline());
    assertEquals(project.getOwnerId(), responseProjectDto.ownerId());
    assertEquals(project.getUserIds(), responseProjectDto.users());
    assertEquals(project.getTeamIds(), responseProjectDto.teams());
    assertEquals(project.getArtifactIds(), responseProjectDto.artifacts());
  }

  /**
   * Tests the toResponseProjectDto method with a null project status
   */
  @Test
  public void testToResponseProjectDtoNullProjectStatus() {
    project.setProjectStatus(null);
    WholeProjectDto responseProjectDto = project.toResponseProjectDto();
    assertEquals(project.getProjectName(), responseProjectDto.projectName());
    assertEquals(project.getProjectDescription(), responseProjectDto.projectDescription());
    assertEquals(project.getProjectCreationDate(), responseProjectDto.projectCreationDate());
    assertEquals(project.getProjectDeadline(), responseProjectDto.projectDeadline());
    assertEquals(project.getOwnerId(), responseProjectDto.ownerId());
    assertEquals(project.getUserIds(), responseProjectDto.users());
    assertEquals(project.getTeamIds(), responseProjectDto.teams());
    assertEquals(project.getArtifactIds(), responseProjectDto.artifacts());
  }

}



