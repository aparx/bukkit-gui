
package com.github.aparx.bgui.examples;

import com.github.aparx.bgui.core.CustomInventoryBuilder;
import com.github.aparx.bgui.core.dimension.InventoryDimensions;
import com.github.aparx.bgui.core.dimension.InventorySection;
import com.github.aparx.bgui.core.populators.InventoryLayerPopulator;
import com.github.aparx.bgui.core.populators.InventoryStoragePopulator;
import com.github.aparx.bommons.ticks.TickDuration;
import com.github.aparx.bommons.ticks.TickTimeUnit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * Very simple example, that shows how to fill, outline and set one clickable item to an inventory.
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-12-27 00:21
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public final class MultilayerExample extends BukkitGuiExamplePlugin {

  @Override
  public void showInventory(Player player) {
    CustomInventoryBuilder.builder()
        .title("Teleport inventory")
        // we update the inventory every second
        .updateInterval(TickDuration.ofOne(TickTimeUnit.SECONDS))
        .populate(InventoryLayerPopulator.create(InventoryDimensions.ofHeight(5))
            .addLayer((lg0) -> InventoryStoragePopulator.create(lg0)
                .fill(Material.REDSTONE_BLOCK)
                .getView())
            .addLayer((lg0) -> InventoryLayerPopulator.create(InventorySection.of(1, 1, 3, 3), lg0)
                .addLayer((lg1) -> InventoryLayerPopulator.create(InventorySection.of(0, 0, 1, 1), lg1)
                    .addLayer((lg2) -> InventoryStoragePopulator.create(lg2)
                        .fill(Material.RED_STAINED_GLASS_PANE)
                        .getView())
                    .getView())
                .getView())
            .addLayer((lg0) -> InventoryLayerPopulator.create(InventorySection.of(5, 1, 7, 3), lg0)
                .addLayer((lg1) -> InventoryStoragePopulator.create(lg1)
                    .fill(Material.BLUE_STAINED_GLASS_PANE)
                    .getView())
                .getView())
            .getView())
        .build(/*plugin*/ this)
        .show(player);
  }
}