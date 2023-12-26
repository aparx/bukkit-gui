package io.github.aparx.bgui.core.item;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 15:04
 * @since 1.0
 */
@FunctionalInterface
public interface InventoryItemClickAction {

  InventoryItemClickAction CANCELLING = (i, e) -> e.setCancelled(true);

  void handleClick(@NonNull InventoryItem item, @NonNull InventoryClickEvent event);

  default InventoryItemClickAction andThen(InventoryItemClickAction action) {
    return (action != null ? (item, event) -> {
      this.handleClick(item, event);
      action.handleClick(item, event);
    } : this);
  }

}
