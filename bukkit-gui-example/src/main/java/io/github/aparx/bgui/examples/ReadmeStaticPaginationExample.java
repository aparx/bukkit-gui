package io.github.aparx.bgui.examples;

import io.github.aparx.bgui.core.dimension.InventoryDimensions;
import io.github.aparx.bgui.core.CustomInventoryBuilder;
import io.github.aparx.bgui.core.item.InventoryItemFactory;
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
public final class ReadmeStaticPaginationExample extends BukkitGuiExamplePlugin {

  @Override
  public void showInventory(Player player) {
    CustomInventoryBuilder.builder()
        .title("Multiple pages, like magic!")
        .populate(InventoryPagePopulator.create(InventoryDimensions.ofHeight(3))
            // update the placeholder for when there's no pagination item (optional)
            .setPlaceholder(InventoryItemFactory.cancel(Material.GRAY_STAINED_GLASS_PANE))
            .addPage((parent) -> InventoryStoragePopulator.create(parent)
                .fill(Material.GREEN_STAINED_GLASS_PANE)
                .set(parent.getArea().center(), Material.EMERALD)
                .getView())
            .addPage((parent) -> InventoryStoragePopulator.create(parent)
                .fill(Material.RED_STAINED_GLASS_PANE)
                .set(parent.getArea().center(), Material.REDSTONE)
                .getView())
            // you can even update the pagination items if you want to
            // .setItem(PaginationItemType.PREVIOUS_PAGE, ...)
            // .setItem(PaginationItemType.NEXT_PAGE, ...)
            .getView())
        .build(/*plugin*/ this)
        .show(player);
  }
}