package it.unisannio.studenti.qualitag.Model;

import java.util.ArrayList;
import java.util.Objects;

public class Artifact {
    private String artifact_id;
    private ArrayList<Tag> tags;
    private String content;

    /**
     * Default constructor for Artifact
     *
     */
    public Artifact() {
        this.tags = new ArrayList<Tag>();
    }

    /**
     * Constructor for Artifact
     *
     * @param artifact_id the id of the artifact
     * @param content the content of the artifact
     */

    public Artifact(String artifact_id, String content) {
        this.artifact_id = artifact_id;
        this.tags = new ArrayList<Tag>();
        this.content = content;
    }

    /**
     * Adds a tag to the artifact
     * @param tag the tag to add
     */
    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    /**
     * Removes a tag from the artifact
     * @param tag the tag to remove
     */
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

    /**
     * Checks if the artifact has a specific tag
     * @param tag the tag to check
     * @return true if the artifact has the tag, false otherwise
     */
    public boolean isTagInArtifact(Tag tag) {
        return this.tags.contains(tag);
    }

    //GETTERS AND SETTERS

    /**
     * Gets the id of the artifact
     * @return the id of the artifact
     */
    public String getArtifact_id() {
        return artifact_id;
    }

    /**
     * Sets the id of the artifact
     * @param artifact_id the id of the artifact
     */
    public void setArtifact_id(String artifact_id) {
        this.artifact_id = artifact_id;
    }

    /**
     * Gets the tags of the artifact
     * @return the tags of the artifact
     */
    public ArrayList<Tag> getTags() {
        return tags;
    }

    /**
     * Sets the tags of the artifact
     * @param tags the tags of the artifact
     */
    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Gets the content of the artifact
     * @return the content of the artifact
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the artifact
     * @param content the content of the artifact
     */
    public void setContent(String content) {
        this.content = content;
    }

    //EQUALS AND HASHCODE

    /**
     * Checks if two artifacts are equal
     * @param o the object to compare
     * @return true if the artifacts are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artifact artifact = (Artifact) o;
        return Objects.equals(artifact_id, artifact.getArtifact_id()) &&
                Objects.equals(tags, artifact.getTags()) &&
                Objects.equals(content, artifact.getContent());
    }

    /**
     * Generates the hashcode of the artifact
     * @return the hashcode of the artifact
     */
    @Override
    public int hashCode() {
        return Objects.hash(getArtifact_id(), getTags(), getContent());
    }

    //TO STRING
    /**
     * Generates the string representation of the artifact
     * @return the string representation of the artifact
     */
    public String toString() {
        return "Artifact{" +
                "artifact_id='" + artifact_id + '\'' +
                ", tags=" + tags +
                ", content='" + content + '\'' +
                '}';
    }
}
