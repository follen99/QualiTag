package it.unisannio.studenti.qualitag.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArtifactTest {

    private Artifact artifact;

    @BeforeEach
    public void setUp() {
        artifact = new Artifact("content");
    }

    @Test
    public void testGetArtifactId() {
        assertNull(artifact.getArtifactId()); //initially artifactId is null
    }

    @Test
    public void testGetContent() {
        assertEquals("content", artifact.getContent());
    }

    @Test
    public void testSetContent() {
        artifact.setContent("newContent");
        assertEquals("newContent", artifact.getContent());
    }

    @Test
    public void addAndRemoveTagId() {
        artifact.addTagId("tagId");
        assertTrue(artifact.getTagIds().contains("tagId"));
        artifact.removeTagId("tagId");
        assertFalse(artifact.getTagIds().contains("tagId"));
    }

    @Test
    public void testEqualsAndHashCode() {
        Artifact artifact1 = new Artifact("content");
        artifact1.addTagId("tagId");
        Artifact artifact2 = new Artifact("content");
        artifact2.addTagId("tagId");
        assertEquals(artifact1, artifact2);
        assertEquals(artifact1.hashCode(), artifact2.hashCode());
    }

    @Test
    public void testToString(){
        String expected = "Artifact{artifactId='null', content='content', tagIds=[]}";
        assertEquals(expected, artifact.toString());
    }
}
