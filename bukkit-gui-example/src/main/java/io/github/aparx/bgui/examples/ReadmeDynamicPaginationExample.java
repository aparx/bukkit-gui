package io.github.aparx.bgui.examples;

import io.github.aparx.bgui.core.CustomInventoryBuilder;
import io.github.aparx.bgui.core.dimension.InventoryDimensions;
import io.github.aparx.bgui.core.item.InventoryItem;
import io.github.aparx.bgui.core.item.InventoryItemFactory;
import io.github.aparx.bgui.core.populators.InventoryDynamicPagePopulator;
import io.github.aparx.bgui.core.populators.InventoryPagePopulator;
import io.github.aparx.bgui.core.populators.InventoryStoragePopulator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Arrays;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-27 00:21
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public final class ReadmeDynamicPaginationExample extends BukkitGuiExamplePlugin {

  @Override
  public void showInventory(Player player) {
    // we explicitly state dimensions, so we can determine how many pages of redstone we want
    InventoryDimensions dimensions = InventoryDimensions.ofHeight(3);

    // allocate ordinary amount of items, everything after the size of the inventory will
    // be put into separate inventories and pagination items will be shown
    InventoryItem[] items = new InventoryItem[2 /* pages */ * dimensions.size()];
    for (int i = 0; i < items.length; ++i)
      items[i] = InventoryItemFactory.cancel(Material.REDSTONE, 1 + i);

    CustomInventoryBuilder.builder()
        .title("Dynamic pages!")
        .populate(InventoryDynamicPagePopulator.create(dimensions)
            .setElements(items)
            .getView())
        .build(/*plugin*/ this)
        .show(player);
  }
}