package com.github.aparx.bgui.examples;

import com.github.aparx.bgui.core.CustomInventoryBuilder;
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
public final class BasicLayerExample extends BukkitGuiExamplePlugin {

  @Override
  public void showInventory(Player player) {
    CustomInventoryBuilder.builder()
        .title("Basic example")
        // we update the inventory every second
        .updateInterval(TickDuration.ofOne(TickTimeUnit.SECONDS))
        .populate(InventoryLayerPopulator.create()
            .addLayer((parent) -> InventoryStoragePopulator.create(parent)
                .fill(Material.RED_STAINED_GLASS_PANE)
                .getView())
            .getView())
        .build(/*plugin*/ this)
        .show(player);
  }
}