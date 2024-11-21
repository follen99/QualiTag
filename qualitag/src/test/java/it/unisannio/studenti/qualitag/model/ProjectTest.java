package it.unisannio.studenti.qualitag.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectTest {

    @Test
    public void testProjectDefaultConstructor() {
        Project project = new Project();

        project.setProjectId("defaultId");
        project.setProjectName("defaultName");
        Date creationDate = new Date();
        project.setProjectCreationDate(creationDate);
        Date deadlineDate = new Date();
        project.setProjectDeadline(deadlineDate);
        project.setProjectDescription("defaultDescription");

        assertEquals("defaultId", project.getProjectId());
        assertEquals("defaultName", project.getProjectName());
        assertEquals(creationDate, project.getProjectCreationDate());
        assertEquals(deadlineDate, project.getProjectDeadline());
        assertEquals("defaultDescription", project.getProjectDescription());
    }

    @Test
    public void testProjectConstructor() {
        Date date = new Date();
        Project project = new Project("1", "Project", date, "Description");

        assertEquals("1", project.getProjectId());
        assertEquals("Project", project.getProjectName());
        assertEquals(date, project.getProjectDeadline());
        assertEquals("Description", project.getProjectDescription());
    }

    @Test
    public void testSettersAndGetters() {
        Date date = new Date();
        Project project = new Project("1", "Project", date, "Description");
        project.setProjectId("2");
        project.setProjectName("Project2");
        Date date2 = new Date();
        project.setProjectDeadline(date2);
        project.setProjectDescription("Description2");

        assertEquals("2", project.getProjectId());
        assertEquals("Project2", project.getProjectName());
        assertEquals(date2, project.getProjectDeadline());
        assertEquals("Description2", project.getProjectDescription());
    }

    @Test
    public void testEqualsAndHashCode(){
        Date date = new Date();

        Project project = new Project("1", "Project", date, "Description");
        Project sameProject = new Project("1", "Project", date, "Description");
        Project differentProject = new Project("2", "Different Project", date, "Different Description");

        assertEquals(project, sameProject);
        assertEquals(project.hashCode(), sameProject.hashCode());

        assertNotEquals(project, differentProject);
        assertNotEquals(project.hashCode(), differentProject.hashCode());
    }

    @Test
    public void testToString(){
        Date date = new Date();
        Project project = new Project("1", "Project", date, "Description");
        assertEquals("Project{projectId='1', projectName='Project', users=[], teams=[], projectDeadline=" +
                date + ", projectCreationDate=" + project.getProjectCreationDate() +
                ", projectDescription='Description'}", project.toString());
    }

}
