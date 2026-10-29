package com.quocdev.capybackpack.model;

import org.bukkit.Material;

public enum BackpackTier {
    BASE("base", 1, 9, "#B0BEC5", null, Material.BUNDLE),
    IRON("iron", 2, 18, "#ECEFF1", "base", Material.WHITE_BUNDLE),
    GOLD("gold", 3, 27, "#FFE082", "iron", Material.YELLOW_BUNDLE),
    DIAMOND("diamond", 4, 36, "#80DEEA", "gold", Material.LIGHT_BLUE_BUNDLE),
    NETHERITE("netherite", 6, 54, "#B0A8A6", "diamond", Material.BLACK_BUNDLE);

    private final String id;
    private final int rows;
    private final int slots;
    private final String colorHex;
    private final String previousTierId;
    private final Material bundleMaterial;

    BackpackTier(String id, int rows, int slots, String colorHex, String previousTierId, Material bundleMaterial) {
        this.id = id;
        this.rows = rows;
        this.slots = slots;
        this.colorHex = colorHex;
        this.previousTierId = previousTierId;
        this.bundleMaterial = bundleMaterial;
    }

    public String getId() {
        return id;
    }

    public int getRows() {
        return rows;
    }

    public int getSlots() {
        return slots;
    }

    public String getColorHex() {
        return colorHex;
    }

    public String getPreviousTierId() {
        return previousTierId;
    }

    public Material getBundleMaterial() {
        return bundleMaterial;
    }

    public BackpackTier getNextTier() {
        switch (this) {
            case BASE:
                return IRON;
            case IRON:
                return GOLD;
            case GOLD:
                return DIAMOND;
            case DIAMOND:
                return NETHERITE;
            default:
                return null;
        }
    }

    public static BackpackTier fromString(String text) {
        if (text == null) {
            return BASE;
        }
        for (BackpackTier tier : values()) {
            if (tier.id.equalsIgnoreCase(text) || tier.name().equalsIgnoreCase(text)) {
                return tier;
            }
        }
        return BASE;
    }
}

