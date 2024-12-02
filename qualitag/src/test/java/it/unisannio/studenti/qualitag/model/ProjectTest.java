package it.unisannio.studenti.qualitag.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectTest {

  @Test
  public void testProjectDefaultConstructor() {
    Project project = new Project();

    project.setProject_id("default_id");
    project.setProject_name("default_name");
    Date creation_date = new Date();
    project.setProject_creation_date(creation_date);
    Date deadline_date = new Date();
    project.setProject_deadline(deadline_date);
    project.setProject_description("default_description");

    assertEquals("default_id", project.getProject_id());
    assertEquals("default_name", project.getProject_name());
    assertEquals(creation_date, project.getProject_creation_date());
    assertEquals(deadline_date, project.getProject_deadline());
    assertEquals("default_description", project.getProject_description());
  }

  @Test
  public void testProjectConstructor() {
    Date date = new Date();
    Project project = new Project("1", "Project", date, "Description");

    assertEquals("1", project.getProject_id());
    assertEquals("Project", project.getProject_name());
    assertEquals(date, project.getProject_deadline());
    assertEquals("Description", project.getProject_description());
  }

  @Test
  public void testSettersAndGetters() {
    Date date = new Date();
    Project project = new Project("1", "Project", date, "Description");
    project.setProject_id("2");
    project.setProject_name("Project2");
    Date date2 = new Date();
    project.setProject_deadline(date2);
    project.setProject_description("Description2");

    assertEquals("2", project.getProject_id());
    assertEquals("Project2", project.getProject_name());
    assertEquals(date2, project.getProject_deadline());
    assertEquals("Description2", project.getProject_description());
  }

  @Test
  public void testEqualsAndHashCode() {
    Date date = new Date();

    Project project = new Project("1", "Project", date, "Description");
    Project same_project = new Project("1", "Project", date, "Description");
    Project different_project = new Project("2", "Different Project", date,
        "Different Description");

    assertEquals(project, same_project);
    assertEquals(project.hashCode(), same_project.hashCode());

    assertNotEquals(project, different_project);
    assertNotEquals(project.hashCode(), different_project.hashCode());
  }

  @Test
  public void testToString() {
    Date date = new Date();
    Project project = new Project("1", "Project", date, "Description");
    assertEquals(
        "Project{project_id='1', project_name='Project', users=[], teams=[], project_deadline=" +
            date + ", project_creation_date=" + project.getProject_creation_date() +
            ", project_description='Description'}", project.toString());
  }

}
