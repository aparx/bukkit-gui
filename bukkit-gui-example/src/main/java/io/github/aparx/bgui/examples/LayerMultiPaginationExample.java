package io.github.aparx.bgui.examples;

import io.github.aparx.bgui.core.CustomInventoryBuilder;
import io.github.aparx.bgui.core.dimension.InventorySection;
import io.github.aparx.bgui.core.populators.InventoryLayerPopulator;
import io.github.aparx.bgui.core.populators.InventoryPagePopulator;
import io.github.aparx.bgui.core.populators.InventoryStoragePopulator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-27 00:21
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public final class LayerMultiPaginationExample extends BukkitGuiExamplePlugin {

  @Override
  public void showInventory(Player player) {
    CustomInventoryBuilder.builder()
        .title("Multiple pages, like magic!")
        .populate(InventoryLayerPopulator.create()
            // first layer is a section at the first half of the inventory
            .addLayer((x) -> InventoryPagePopulator.create(InventorySection.of(0, 0, 3, 3), x)
                .addPage((pageGroup) -> InventoryStoragePopulator.create(pageGroup)
                    .fill(Material.RED_STAINED_GLASS_PANE)
                    .getView())
                .addPage((pageGroup) -> InventoryStoragePopulator.create(pageGroup)
                    .fill(Material.GREEN_STAINED_GLASS_PANE)
                    .getView())
                .getView())
            // second layer is a section at the second half of the inventory
            .addLayer((x) -> InventoryPagePopulator.create(InventorySection.of(5, 0, 8, 3), x)
                .addPage((pageGroup) -> InventoryStoragePopulator.create(pageGroup)
                    .fill(Material.BLUE_STAINED_GLASS_PANE)
                    .getView())
                .addPage((pageGroup) -> InventoryStoragePopulator.create(pageGroup)
                    .fill(Material.YELLOW_STAINED_GLASS_PANE)
                    .getView())
                .getView())
            .getView())
        .build(/*plugin*/ this)
        .show(player);
  }
}