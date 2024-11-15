package it.unisannio.studenti.qualitag.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Represents a team in the system.
 */
public class Team {
    private String team_id;
    private String project_id;
    private List<String> users;
    private String team_name;
    private Date creation_date;
    private String team_description;

    /**
     * Default constructor for Team.
     */
    public Team() {
        this.users = new ArrayList<String>();
    }

    /**
     * Constructs a Team with the specified details.
     *
     * @param team_id          the ID of the team
     * @param project_id       the ID of the project
     * @param team_name        the name of the team
     * @param creation_date    the creation date of the team
     * @param team_description the description of the team
     */
    public Team(String team_id, String project_id, String team_name, Date creation_date, String team_description) {
        this.team_id = team_id;
        this.project_id = project_id;
        this.team_name = team_name;
        this.creation_date = creation_date;
        this.team_description = team_description;
        this.users = new ArrayList<String>();
    }


    // GETTERS AND SETTERS

    /**
     * Gets the team ID.
     *
     * @return the team ID
     */
    public String getTeam_id() {
        return team_id;
    }

    /**
     * Sets the team ID.
     *
     * @param team_id the team ID to set
     */
    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    /**
     * Gets the project ID.
     *
     * @return the project ID
     */
    public String getProject_id() {
        return project_id;
    }

    /**
     * Sets the project ID.
     *
     * @param project_id the project ID to set
     */
    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    /**
     * Gets the list of users in the team.
     *
     * @return the list of users
     */
    public List<String> getUsers() {
        return users;
    }

    /**
     * Sets the list of users in the team.
     *
     * @param users the list of users to set
     */
    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    /**
     * Sets the list of users in the team.
     *
     * @param users the list of users to set
     */
    public void setUsers(List<String> users) {
        this.users = users;
    }

    /**
     * Adds a user to the team.
     *
     * @param user the user to add
     */
    public void addUser(User user) {
        this.users.add(user.getUserID());
    }

    /**
     * Gets the team name.
     *
     * @return the team name
     */
    public String getTeam_name() {
        return team_name;
    }

    /**
     * Sets the team name.
     *
     * @param team_name the team name to set
     */
    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    /**
     * Gets the creation date of the team.
     *
     * @return the creation date
     */
    public Date getCreation_date() {
        return creation_date;
    }

    /**
     * Sets the creation date of the team.
     *
     * @param creation_date the creation date to set
     */
    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }

    /**
     * Gets the team description.
     *
     * @return the team description
     */
    public String getTeam_description() {
        return team_description;
    }

    /**
     * Sets the team description.
     *
     * @param team_description the team description to set
     */
    public void setTeam_description(String team_description) {
        this.team_description = team_description;
    }

    // EQUALS AND HASHCODE

    /**
     * Checks if the team is equal to another object.
     *
     * @param o the object to compare
     * @return true if the team is equal to the object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(getTeam_id(), team.getTeam_id()) && Objects.equals(getProject_id(), team.getProject_id()) && Objects.equals(getUsers(), team.getUsers()) && Objects.equals(getTeam_name(), team.getTeam_name()) && Objects.equals(getCreation_date(), team.getCreation_date()) && Objects.equals(getTeam_description(), team.getTeam_description());
    }

    /**
     * Generates a hash code for the team.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(getTeam_id(), getProject_id(), getUsers(), getTeam_name(), getCreation_date(), getTeam_description());
    }

    // TO STRING

    /**
     * Gets the team as a string.
     *
     * @return the team as a string
     */
    @Override
    public String toString() {
        return "Team{" +
                "team_id='" + team_id + '\'' +
                ", project_id='" + project_id + '\'' +
                ", users=" + users +
                ", team_name='" + team_name + '\'' +
                ", creation_date=" + creation_date +
                ", team_description='" + team_description + '\'' +
                '}';
    }
}
