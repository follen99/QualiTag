package it.unisannio.studenti.qualitag.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArtifactTest {

    @Test
    public void testArtifactDefaultConstructor() {
        Artifact artifact = new Artifact();
        artifact.setArtifact_id("default_id");
        artifact.setContent("default_content");

        assertEquals("default_id", artifact.getArtifact_id());
        assertEquals("default_content", artifact.getContent());
    }

    @Test
    public void testArtifactConstructor() {
        Artifact artifact = new Artifact("1", "Content");

        assertEquals("1", artifact.getArtifact_id());
        assertEquals("Content", artifact.getContent());
    }

    @Test
    public void testSettersAndGetters() {
        Artifact artifact = new Artifact("1", "Content");
        artifact.setArtifact_id("2");
        artifact.setContent("Content2");

        assertEquals("2", artifact.getArtifact_id());
        assertEquals("Content2", artifact.getContent());
    }

    @Test
    public void testEqualsAndHashCode(){
        Artifact artifact = new Artifact("1", "Content");
        Artifact same_artifact = new Artifact("1", "Content");
        Artifact different_artifact = new Artifact("2", "Different");

        assertEquals(artifact, same_artifact);
        assertNotEquals(artifact, different_artifact);
        assertEquals(artifact.hashCode(), same_artifact.hashCode());
        assertNotEquals(artifact.hashCode(), different_artifact.hashCode());
    }

    @Test
    public void testToString() {
        Artifact artifact = new Artifact("1", "Content");

        assertEquals("Artifact{artifact_id='1', tags=[], content='Content'}", artifact.toString());
    }
}
