package it.unisannio.studenti.qualitag.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {
    @Test
    public void testTagConstructorWithColor() {
        int[] color = {255, 0, 0};
        Tag tag = new Tag("1", "1", "1", "Test", color);
        assertEquals("1", tag.getTag_id());
        assertEquals("1", tag.getProject_id());
        assertEquals("1", tag.getUser_id());
        assertEquals("Test", tag.getValue());
        assertArrayEquals(color, tag.getRgbAsIntArr());
    }

    @Test
    public void testTagConstructorWithDefaultColor() {
        Tag tag = new Tag("1", "1", "1", "Test");
        assertEquals("1", tag.getTag_id());
        assertEquals("1", tag.getProject_id());
        assertEquals("1", tag.getUser_id());
        assertEquals("Test", tag.getValue());
        assertNotNull(tag.getRgbAsIntArr());
    }

    @Test
    public void testSettersAndGetters() {
        Tag tag = new Tag("1", "1", "1", "Test");
        tag.setTag_id("2");
        tag.setProject_id("2");
        tag.setUser_id("2");
        tag.setValue("Updated");
        int[] newColor = {0, 255, 0};
        tag.setRgb(newColor);

        assertEquals("2", tag.getTag_id());
        assertEquals("2", tag.getProject_id());
        assertEquals("2", tag.getUser_id());
        assertEquals("Updated", tag.getValue());
        assertArrayEquals(newColor, tag.getRgbAsIntArr());
    }

    @Test
    public void testEqualsAndHashCode() {
        Tag tag1 = new Tag("1", "1", "1", "Test", new int[]{255, 255, 255});
        Tag tag2 = new Tag("1", "1", "1", "Test", new int[]{255, 255, 255});
        Tag different_tag = new Tag("1", "1", "1", "Test", new int[]{0, 0, 0});
        assertEquals(tag1, tag2);
        assertEquals(tag1.hashCode(), tag2.hashCode());

        assertNotEquals(tag1, different_tag);
        assertNotEquals(tag1.hashCode(), different_tag.hashCode());
    }

    @Test
    public void testToString() {
        Tag tag = new Tag("1", "1", "1", "Test");
        String expected = "Tag{tag_id='1', project_id='1', user_id='1', value='Test', rgb=[255, 255, 255]}";
        assertTrue(tag.toString().contains("tag_id='1'"));
        assertTrue(tag.toString().contains("project_id='1'"));
        assertTrue(tag.toString().contains("user_id='1'"));
        assertTrue(tag.toString().contains("value='Test'"));
    }

    @Test
    public void testSetRgbWithThreeParameters() {
        Tag tag = new Tag("1", "1", "1", "Test");
        tag.setRgb(0, 128, 255);
        int[] expectedColor = {0, 128, 255};
        assertArrayEquals(expectedColor, tag.getRgbAsIntArr());
    }

    @Test
    public void testSetRgbWithHex() {
        Tag tag = new Tag("1", "1", "1", "Test");
        tag.setRgb("#0080ff");
        int[] expectedColor = {0, 128, 255};
        assertArrayEquals(expectedColor, tag.getRgbAsIntArr());
    }

    @Test
    public void testGetRgbAsHexString() {
        Tag tag = new Tag("1", "1", "1", "Test", new int[]{0, 128, 255});
        String expectedHex = "#0080ff";
        assertEquals(expectedHex, tag.getRgbAsHexString());
    }
}