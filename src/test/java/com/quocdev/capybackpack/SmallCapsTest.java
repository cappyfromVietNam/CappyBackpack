package com.quocdev.capybackpack;

import com.quocdev.capybackpack.util.SmallCaps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SmallCapsTest {

    @Test
    public void testBasicSmallCaps() {
        String result = SmallCaps.to("backpack");
        assertEquals("ʙᴀᴄᴋᴘᴀᴄᴋ", result);

        String upper = SmallCaps.to("BACKPACK");
        assertEquals("ʙᴀᴄᴋᴘᴀᴄᴋ", upper);
    }

    @Test
    public void testPreserveMiniMessageTags() {
        String input = "<gray>tier: <white>iron</white></gray>";
        String converted = SmallCaps.convertPreservingTags(input);

        // Tags should NOT be transformed into small caps, but the text inside should
        assertTrue(converted.contains("<gray>"));
        assertTrue(converted.contains("<white>"));
        assertTrue(converted.contains("</white>"));
        assertTrue(converted.contains("</gray>"));
        assertTrue(converted.contains("ᴛɪᴇʀ: "));
        assertTrue(converted.contains("ɪʀᴏɴ"));
    }

    @Test
    public void testPunctuationAndNumbers() {
        String input = "18 slots · 100%";
        String converted = SmallCaps.to(input);
        assertEquals("18 ꜱʟᴏᴛꜱ · 100%", converted);
    }
}
