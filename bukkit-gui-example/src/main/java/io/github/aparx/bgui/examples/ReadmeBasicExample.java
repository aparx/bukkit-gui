package io.github.aparx.bgui.examples;

import io.github.aparx.bgui.core.dimension.InventoryPosition;
import io.github.aparx.bgui.core.CustomInventoryBuilder;
import io.github.aparx.bgui.core.populators.InventoryStoragePopulator;
import io.github.aparx.bommons.ticks.TickDuration;
import io.github.aparx.bommons.ticks.TickTimeUnit;
import org.bukkit.ChatColor;
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
public final class ReadmeBasicExample extends BukkitGuiExamplePlugin {

  @Override
  public void showInventory(Player player) {
    CustomInventoryBuilder.builder()
        .title("Teleport inventory")
        // we update the inventory every second
        .updateInterval(TickDuration.ofOne(TickTimeUnit.SECONDS))
        .populate(InventoryStoragePopulator.create()
            .fill(Material.GRAY_STAINED_GLASS_PANE)
            .outline(Material.RED_STAINED_GLASS_PANE)
            .set(InventoryPosition.ofPoint(4, 1), Material.DIAMOND, (item, event) -> {
              event.getWhoClicked().sendMessage(ChatColor.AQUA + "You clicked the diamond!");
            })
            .getView())
        .build(/*plugin*/ this)
        .show(player);
  }
}