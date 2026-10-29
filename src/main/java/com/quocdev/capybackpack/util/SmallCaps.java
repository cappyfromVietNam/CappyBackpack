package com.quocdev.capybackpack.util;

public final class SmallCaps {

    private SmallCaps() {
    }

    private static final char[] SMALL_CAPS = new char[128];

    static {
        // Initialize with default ASCII chars
        for (int i = 0; i < 128; i++) {
            SMALL_CAPS[i] = (char) i;
        }

        // Map lowercase a-z
        SMALL_CAPS['a'] = '\u1D00'; // ᴀ
        SMALL_CAPS['b'] = '\u0299'; // ʙ
        SMALL_CAPS['c'] = '\u1D04'; // ᴄ
        SMALL_CAPS['d'] = '\u1D05'; // ᴅ
        SMALL_CAPS['e'] = '\u1D07'; // ᴇ
        SMALL_CAPS['f'] = '\uA730'; // ꜰ
        SMALL_CAPS['g'] = '\u0262'; // ɢ
        SMALL_CAPS['h'] = '\u029C'; // ʜ
        SMALL_CAPS['i'] = '\u026A'; // ɪ
        SMALL_CAPS['j'] = '\u1D0A'; // ᴊ
        SMALL_CAPS['k'] = '\u1D0B'; // ᴋ
        SMALL_CAPS['l'] = '\u029F'; // ʟ
        SMALL_CAPS['m'] = '\u1D0D'; // ᴍ
        SMALL_CAPS['n'] = '\u0274'; // ɴ
        SMALL_CAPS['o'] = '\u1D0F'; // ᴏ
        SMALL_CAPS['p'] = '\u1D18'; // ᴘ
        SMALL_CAPS['q'] = '\uA7AF'; // ꞯ
        SMALL_CAPS['r'] = '\u0280'; // ʀ
        SMALL_CAPS['s'] = '\uA731'; // ꜱ
        SMALL_CAPS['t'] = '\u1D1B'; // ᴛ
        SMALL_CAPS['u'] = '\u1D1C'; // ᴜ
        SMALL_CAPS['v'] = '\u1D20'; // ᴠ
        SMALL_CAPS['w'] = '\u1D21'; // ᴡ
        SMALL_CAPS['x'] = 'x';      // x
        SMALL_CAPS['y'] = '\u028F'; // ʏ
        SMALL_CAPS['z'] = '\u1D22'; // ᴢ

        // Map uppercase A-Z to the same small caps
        for (char c = 'A'; c <= 'Z'; c++) {
            SMALL_CAPS[c] = SMALL_CAPS[Character.toLowerCase(c)];
        }
    }

    /**
     * Converts a raw string to small caps.
     */
    public static String to(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c < 128) {
                sb.append(SMALL_CAPS[c]);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Converts text containing MiniMessage tags to small caps,
     * preserving tag names like <gray>, <#ffffff>, etc. without corrupting them.
     */
    public static String convertPreservingTags(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder(input.length());
        boolean insideTag = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '<') {
                insideTag = true;
                result.append(c);
            } else if (c == '>') {
                insideTag = false;
                result.append(c);
            } else if (insideTag) {
                result.append(c);
            } else {
                if (c < 128) {
                    result.append(SMALL_CAPS[c]);
                } else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }
}

