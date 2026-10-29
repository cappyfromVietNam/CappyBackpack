package com.quocdev.capybackpack.model;

public enum BackpackModule {
    FURNACE("furnace", "#FFAB91", "smelts items using fuel"),
    AUTO_FOOD("autofood", "#A5D6A7", "feeds when hungry"),
    MAGNET("magnet", "#80CBC4", "vacuums nearby items"),
    RESTOCK("restock", "#CE93D8", "refills hotbar items"),
    COMPACTOR("compactor", "#FFCC80", "compacts ingots into blocks");

    private final String id;
    private final String colorHex;
    private final String description;

    BackpackModule(String id, String colorHex, String description) {
        this.id = id;
        this.colorHex = colorHex;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getColorHex() {
        return colorHex;
    }

    public String getDescription() {
        return description;
    }

    public static BackpackModule fromString(String text) {
        if (text == null) {
            return null;
        }
        for (BackpackModule mod : values()) {
            if (mod.id.equalsIgnoreCase(text) || mod.name().equalsIgnoreCase(text)) {
                return mod;
            }
        }
        return null;
    }
}
