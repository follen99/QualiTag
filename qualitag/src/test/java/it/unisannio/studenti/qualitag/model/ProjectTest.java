package it.unisannio.studenti.qualitag.model;

import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectTest {

    private Project project;

    @BeforeEach
    public void setUp() {
        LocalDate localDate = LocalDate.of(2025, 12, 31);
        Date projectDeadline = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        project = new Project("ProjectName", "ProjectDescription", projectDeadline);
    }

    @Test
    public void testGetProjectId() {
        assertNull(project.getProjectId()); //Initially, projectId should be null
    }

    @Test
    public void testGetProjectName() {
        assertEquals("ProjectName", project.getProjectName());
    }

    @Test
    public void testSetProjectName() {
        project.setProjectName("NewProjectName");
        assertEquals("NewProjectName", project.getProjectName());
    }

    @Test
    public void testGetProjectDescription() {
        assertEquals("ProjectDescription", project.getProjectDescription());
    }

    @Test
    public void testSetProjectDescription() {
        project.setProjectDescription("NewProjectDescription");
        assertEquals("NewProjectDescription", project.getProjectDescription());
    }

    @Test
    public void testGetProjectCreationDate() {
        assertNotNull(project.getProjectCreationDate());
    }

    @Test
    public void testGetProjectDeadline() {
        LocalDate localDate = LocalDate.of(2025, 12, 31);
        Date projectDeadline= Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertEquals(projectDeadline, project.getProjectDeadline());
    }

    @Test
    public void testSetProjectDeadline() {
        LocalDate localDate = LocalDate.of(2026, 12, 31);
        Date projectDeadline= Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        project.setProjectDeadline(projectDeadline);
        assertEquals(projectDeadline, project.getProjectDeadline());
    }

    @Test
    public void testAddAndRemoveUserId() {
        project.addUserId("userId");
        assertTrue(project.getUserIds().contains("userId"));
        project.removeUserId("userId");
        assertFalse(project.getUserIds().contains("userId"));
    }

    @Test
    public void testAddAndRemoveTeamId() {
        project.addTeamId("teamId");
        assertTrue(project.getTeamIds().contains("teamId"));
        project.removeTeamId("teamId");
        assertFalse(project.getTeamIds().contains("teamId"));
    }

    @Test
    public void testEqualsAndHashCode() {
        LocalDate localDate = LocalDate.of(2025, 12, 31);
        Date projectDeadline = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Project anotherProject = new Project("ProjectName", "ProjectDescription",
            projectDeadline);
        assertEquals(project, anotherProject);
        assertEquals(project.hashCode(), anotherProject.hashCode());
    }

    @Test
    public void testToString() {
        String expected =
            "Project{projectId='null', projectName='ProjectName', projectDescription='ProjectDescription', projectCreationDate="
                + project.getProjectCreationDate()
                + ", projectDeadline=Wed Dec 31 00:00:00 CET 2025, usersIds=[], teamsIds=[]}";
        assertEquals(expected, project.toString());
    }
}
