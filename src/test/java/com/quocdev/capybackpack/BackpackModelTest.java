package com.quocdev.capybackpack;

import com.quocdev.capybackpack.model.BackpackData;
import com.quocdev.capybackpack.model.BackpackModule;
import com.quocdev.capybackpack.model.BackpackTier;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BackpackModelTest {

    @Test
    public void testTierProgression() {
        assertEquals(BackpackTier.IRON, BackpackTier.BASE.getNextTier());
        assertEquals(BackpackTier.GOLD, BackpackTier.IRON.getNextTier());
        assertEquals(BackpackTier.DIAMOND, BackpackTier.GOLD.getNextTier());
        assertEquals(BackpackTier.NETHERITE, BackpackTier.DIAMOND.getNextTier());
        assertNull(BackpackTier.NETHERITE.getNextTier());

        assertEquals(9, BackpackTier.BASE.getSlots());
        assertEquals(18, BackpackTier.IRON.getSlots());
        assertEquals(27, BackpackTier.GOLD.getSlots());
        assertEquals(36, BackpackTier.DIAMOND.getSlots());
        assertEquals(54, BackpackTier.NETHERITE.getSlots());

        assertEquals(org.bukkit.Material.BUNDLE, BackpackTier.BASE.getBundleMaterial());
        assertEquals(org.bukkit.Material.WHITE_BUNDLE, BackpackTier.IRON.getBundleMaterial());
        assertEquals(org.bukkit.Material.YELLOW_BUNDLE, BackpackTier.GOLD.getBundleMaterial());
        assertEquals(org.bukkit.Material.LIGHT_BLUE_BUNDLE, BackpackTier.DIAMOND.getBundleMaterial());
        assertEquals(org.bukkit.Material.BLACK_BUNDLE, BackpackTier.NETHERITE.getBundleMaterial());
    }

    @Test
    public void testModuleParsing() {
        assertEquals(BackpackModule.FURNACE, BackpackModule.fromString("furnace"));
        assertEquals(BackpackModule.AUTO_FOOD, BackpackModule.fromString("autofood"));
        assertNull(BackpackModule.fromString("unknown"));
    }

    @Test
    public void testBackpackDataResizing() {
        BackpackData data = new BackpackData(UUID.randomUUID(), BackpackTier.BASE, EnumSet.noneOf(BackpackModule.class), null, 0);
        assertEquals(9, data.getContents().length);

        // Upgrade to IRON
        data.setTier(BackpackTier.IRON);
        assertEquals(18, data.getContents().length);

        // Add module
        assertTrue(data.addModule(BackpackModule.FURNACE));
        assertTrue(data.hasModule(BackpackModule.FURNACE));
        assertFalse(data.hasModule(BackpackModule.AUTO_FOOD));
    }
}

