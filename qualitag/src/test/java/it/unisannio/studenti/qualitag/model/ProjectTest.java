package it.unisannio.studenti.qualitag.model;

import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

public class ProjectTest {

  private Project project;

  /**
   * Set up a project object for the tests
   */
  @BeforeEach
  public void setUp() {
    LocalDate localDate = LocalDate.of(2025, 12, 31);
    Long projectDeadline = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    project = new Project("ProjectName", "ProjectDescription", projectDeadline);
  }

  /**
   * Test the getProjectId method
   */
  @Test
  public void testGetProjectId() {
    assertNull(project.getProjectId()); //Initially, projectId should be null
  }

  /**
   * Test the getProjectName method
   */
  @Test
  public void testGetProjectName() {
    assertEquals("ProjectName", project.getProjectName());
  }

  /**
   * Test the setProjectName method
   */
  @Test
  public void testSetProjectName() {
    project.setProjectName("NewProjectName");
    assertEquals("NewProjectName", project.getProjectName());
  }

  /**
   * Test the getProjectDescription method
   */
  @Test
  public void testGetProjectDescription() {
    assertEquals("ProjectDescription", project.getProjectDescription());
  }

  /**
   * Test the setProjectDescription method
   */
  @Test
  public void testSetProjectDescription() {
    project.setProjectDescription("NewProjectDescription");
    assertEquals("NewProjectDescription", project.getProjectDescription());
  }

  /**
   * Test the getProjectCreationDate method
   */
  @Test
  public void testGetProjectCreationDate() {
    assertNotNull(project.getProjectCreationDate());
  }

  /**
   * Test the getProjectDeadline method
   */
  @Test
  public void testGetProjectDeadline() {
    LocalDate localDate = LocalDate.of(2025, 12, 31);
    Long projectDeadline = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    this.project.setProjectDeadline(projectDeadline);

    assertEquals(projectDeadline, project.getProjectDeadline());
  }

  /**
   * Test the setProjectDeadline method
   */
  @Test
  public void testSetProjectDeadline() {
    LocalDate localDate = LocalDate.of(2026, 12, 31);
    Long projectDeadline = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    project.setProjectDeadline(projectDeadline);
    assertEquals(projectDeadline, project.getProjectDeadline());
  }

  /**
   * Test the add and remove methods for the user, team and artifact ids
   */
  @Test
  public void testAddAndRemoveUserId() {
    project.addUserId("userId");
    assertTrue(project.getUsers().contains("userId"));
    project.removeUserId("userId");
    assertFalse(project.getUsers().contains("userId"));
  }

  /**
   * Test the add and remove methods for the team ids
   */
  @Test
  public void testAddAndRemoveTeamId() {
    project.addTeamId("teamId");
    assertTrue(project.getTeams().contains("teamId"));
    project.removeTeamId("teamId");
    assertFalse(project.getTeams().contains("teamId"));
  }

  /**
   * Test the add and remove methods for the artifact ids
   */
  @Test
  public void testAddAndRemoveArtifactId() {
    project.addArtifactId("artifactId");
    assertTrue(project.getArtifacts().contains("artifactId"));
    project.removeArtifactId("artifactId");
    assertFalse(project.getArtifacts().contains("artifactId"));
  }

  /**
   * Test the isUserIdInProject method
   */
  @Test
  public void testGetOwnerId() {
    assertNull(project.getOwnerId());
  }

  /**
   * Test the setOwnerId method
   */
  @Test
  public void testSetOwnerId() {
    project.setOwnerId("ownerId");
    assertEquals("ownerId", project.getOwnerId());
  }

  /**
   * Test the isUserIdInProject method
   */
  @Test
  public void testEqualsAndHashCode() {
    LocalDate localDate = LocalDate.of(2025, 12, 31);
    Long projectDeadline = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    Project anotherProject = new Project("ProjectName", "ProjectDescription",
        projectDeadline);
    assertEquals(project, anotherProject);
    assertEquals(project.hashCode(), anotherProject.hashCode());
  }

//  /**
//   * Test the toString method
//   */
//  @Test
//  public void testToString() {
//    String expected =
//        "Project{projectId='null', projectName='ProjectName', projectDescription='ProjectDescription', projectCreationDate="
//            + project.getProjectCreationDate()
//            + ", projectDeadline=Wed Dec 31 00:00:00 CET 2025, "
//            + "usersIds=[], teamsIds=[], artifactsIds=[], ownerId=null}";
//    assertEquals(expected, project.toString());
//  }
}
