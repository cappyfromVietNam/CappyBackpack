package com.quocdev.capybackpack.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class TextUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private TextUtil() {
    }

    /**
     * Parses a MiniMessage formatted string into a Component,
     * converting raw text to small caps and enforcing NO bold and NO default italic.
     */
    public static Component parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        // Convert text content to small caps while keeping MiniMessage tags intact
        String converted = SmallCaps.convertPreservingTags(input);

        // Strip any accidental bold tags
        converted = converted.replace("<bold>", "").replace("</bold>", "")
                             .replace("<b>", "").replace("</b>", "");

        return MINI_MESSAGE.deserialize(converted)
                .decoration(TextDecoration.BOLD, false)
                .decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Formats a raw title/plain text into small caps Component without italics or bold.
     */
    public static Component plainSmallCaps(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        return Component.text(SmallCaps.to(text))
                .decoration(TextDecoration.BOLD, false)
                .decoration(TextDecoration.ITALIC, false);
    }
}

