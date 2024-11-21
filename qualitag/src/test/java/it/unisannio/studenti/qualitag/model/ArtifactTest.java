package it.unisannio.studenti.qualitag.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArtifactTest {

    @Test
    public void testArtifactDefaultConstructor() {
        Artifact artifact = new Artifact();
        artifact.setArtifactId("defaultId");
        artifact.setContent("defaultContent");

        assertEquals("defaultId", artifact.getArtifactId());
        assertEquals("defaultContent", artifact.getContent());
    }

    @Test
    public void testArtifactConstructor() {
        Artifact artifact = new Artifact("1", "Content");

        assertEquals("1", artifact.getArtifactId());
        assertEquals("Content", artifact.getContent());
    }

    @Test
    public void testSettersAndGetters() {
        Artifact artifact = new Artifact("1", "Content");
        artifact.setArtifactId("2");
        artifact.setContent("Content2");

        assertEquals("2", artifact.getArtifactId());
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

        assertEquals("Artifact{artifactId='1', tags=[], content='Content'}", artifact.toString());
    }
}
