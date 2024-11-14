package it.unisannio.studenti.qualitag.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest {
    @Test
    public void testTeamConstructor() {
        Team team = new Team("1", "1", "Test", null, "Test");
        assertEquals("1", team.getTeam_id());
        assertEquals("1", team.getProject_id());
        assertEquals("Test", team.getTeam_name());
        assertNull(team.getCreation_date());
        assertEquals("Test", team.getTeam_description());
    }

    @Test
    public void testSettersAndGetters() {
        Team team = new Team("1", "1", "Test", null, "Test");
        team.setTeam_id("2");
        team.setProject_id("2");
        team.setTeam_name("Updated");
        team.setCreation_date(null);
        team.setTeam_description("Updated");

        assertEquals("2", team.getTeam_id());
        assertEquals("2", team.getProject_id());
        assertEquals("Updated", team.getTeam_name());
        assertNull(team.getCreation_date());
        assertEquals("Updated", team.getTeam_description());
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
        team.setTeam_id("default_id");
        team.setProject_id("default_project");
        team.setTeam_name("Default Team");
        team.setCreation_date(new Date());
        team.setTeam_description("Default Description");

        assertEquals("default_id", team.getTeam_id());
        assertEquals("default_project", team.getProject_id());
        assertEquals("Default Team", team.getTeam_name());
        assertNotNull(team.getCreation_date());
        assertEquals("Default Description", team.getTeam_description());
    }
}