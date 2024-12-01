package it.unisannio.studenti.qualitag.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest {
    @Test
    public void testTeamConstructor() {
        Team team = new Team("1", "1", "Test", null, "Test");
        assertEquals("1", team.getTeamId());
        assertEquals("1", team.getProjectId());
        assertEquals("Test", team.getTeamName());
        assertNull(team.getCreationTimeStamp());
        assertEquals("Test", team.getTeamDescription());
    }

    @Test
    public void testSettersAndGetters() {
        Team team = new Team("1", "1", "Test", null, "Test");
        team.setTeamId("2");
        team.setProjectId("2");
        team.setTeamName("Updated");
        team.setCreationTimeStamp(null);
        team.setTeamDescription("Updated");

        assertEquals("2", team.getTeamId());
        assertEquals("2", team.getProjectId());
        assertEquals("Updated", team.getTeamName());
        assertNull(team.getCreationTimeStamp());
        assertEquals("Updated", team.getTeamDescription());
    }

    @Test
    public void testEqualsAndHashCode() {
        Team team1 = new Team("1", "1", "Test", null, "Test");
        Team team2 = new Team("1", "1", "Test", null, "Test");
        Team different_team = new Team("1", "1", "Test", null, "Different");

        assertEquals(team1, team2);
        assertEquals(team1.hashCode(), team2.hashCode());

        assertNotEquals(team1, different_team);
        assertNotEquals(team1.hashCode(), different_team.hashCode());
    }

    @Test
    public void testToString() {
        Team team = new Team("1", "1", "Test", null, "Test");
        assertEquals("Team{team_id='1', project_id='1', users=[], team_name='Test', creation_date=null, team_description='Test'}", team.toString());
    }

    @Test
    public void testDefaultConstructor() {
        Team team = new Team();
        team.setTeamId("default_id");
        team.setProjectId("default_project");
        team.setTeamName("Default Team");
        team.setCreationTimeStamp(new Date());
        team.setTeamDescription("Default Description");

        assertEquals("default_id", team.getTeamId());
        assertEquals("default_project", team.getProjectId());
        assertEquals("Default Team", team.getTeamName());
        assertNotNull(team.getCreationTimeStamp());
        assertEquals("Default Description", team.getTeamDescription());
    }
}