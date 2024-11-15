package it.unisannio.studenti.qualitag.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultColorTest {

    @Test
    public void testGetRgb() {
        DefaultColor color = DefaultColor.PASTEL_GRAY_DARK;
        int[] rgb = color.getRgb();
        assertEquals(166, rgb[0]);
        assertEquals(174, rgb[1]);
        assertEquals(191, rgb[2]);
    }
}