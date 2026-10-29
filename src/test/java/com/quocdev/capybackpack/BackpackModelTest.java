package com.quocdev.capybackpack;

import com.quocdev.capybackpack.model.BackpackData;
import com.quocdev.capybackpack.model.BackpackModule;
import com.quocdev.capybackpack.model.BackpackTier;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

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

        assertEquals(Material.BUNDLE, BackpackTier.BASE.getBundleMaterial());
        assertEquals(Material.WHITE_BUNDLE, BackpackTier.IRON.getBundleMaterial());
        assertEquals(Material.YELLOW_BUNDLE, BackpackTier.GOLD.getBundleMaterial());
        assertEquals(Material.LIGHT_BLUE_BUNDLE, BackpackTier.DIAMOND.getBundleMaterial());
        assertEquals(Material.BLACK_BUNDLE, BackpackTier.NETHERITE.getBundleMaterial());
    }

    @Test
    public void testModuleParsing() {
        assertEquals(BackpackModule.FURNACE, BackpackModule.fromString("furnace"));
        assertEquals(BackpackModule.AUTO_FOOD, BackpackModule.fromString("autofood"));
        assertEquals(BackpackModule.MAGNET, BackpackModule.fromString("magnet"));
        assertEquals(BackpackModule.RESTOCK, BackpackModule.fromString("restock"));
        assertEquals(BackpackModule.COMPACTOR, BackpackModule.fromString("compactor"));
        assertNull(BackpackModule.fromString("unknown"));
    }

    @Test
    public void testBackpackDataResizingAndSingleModule() {
        BackpackData data = new BackpackData(UUID.randomUUID(), BackpackTier.BASE, null, null, 0);
        assertEquals(9, data.getContents().length);
        assertFalse(data.hasAnyModule());
        assertNull(data.getModule());

        // Upgrade to IRON
        data.setTier(BackpackTier.IRON);
        assertEquals(18, data.getContents().length);

        // Set single module
        data.setModule(BackpackModule.MAGNET);
        assertTrue(data.hasAnyModule());
        assertTrue(data.hasModule(BackpackModule.MAGNET));
        assertFalse(data.hasModule(BackpackModule.AUTO_FOOD));
        assertEquals(BackpackModule.MAGNET, data.getModule());

        // Clear module (Remover)
        data.clearModule();
        assertFalse(data.hasAnyModule());
        assertNull(data.getModule());
    }
}
