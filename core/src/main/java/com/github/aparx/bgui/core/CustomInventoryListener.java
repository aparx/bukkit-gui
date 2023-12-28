package com.github.aparx.bgui.core;

import com.github.aparx.bgui.core.content.InventoryContentView;
import com.google.common.base.Preconditions;
import com.github.aparx.bgui.core.item.InventoryItem;
import com.github.aparx.bgui.core.dimension.InventoryPosition;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.lang.ref.WeakReference;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 20:56
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public class CustomInventoryListener implements Listener {

  private final WeakReference<CustomInventory> inventory;

  public CustomInventoryListener(CustomInventory inventory) {
    Preconditions.checkNotNull(inventory, "Inventory must not be null");
    this.inventory = new WeakReference<>(inventory);
  }

  @EventHandler(priority = EventPriority.HIGH)
  void onInteract(InventoryClickEvent event) {
    if (event.isCancelled()) return;
    HumanEntity humanEntity = event.getWhoClicked();
    if (!(humanEntity instanceof Player)) return;
    Player player = (Player) humanEntity;
    int slot = event.getSlot();
    if (slot >= 0 && event.getSlotType() == InventoryType.SlotType.CONTAINER) {
      @Nullable CustomInventory thisInventory = this.inventory.get();
      //noinspection deprecation
      if (thisInventory != null && thisInventory.isViewer(player)
          && event.getInventory().equals(thisInventory.getInventory())) {
        @Nullable InventoryContentView content = thisInventory.getContent();
        if (content == null) return;
        @Nullable InventoryItem inventoryItem = content.get(thisInventory,
            InventoryPosition.ofIndex(slot, content.getDimensions().getWidth()));
        if (inventoryItem != null) {
          inventoryItem.handleClick(inventoryItem, event);
          thisInventory.renderInventory(false); // force re-render due to click
        }
      } else if (thisInventory != null)
        HandlerList.unregisterAll(this);
    }
  }

  public CustomInventory getInventory() {
    return Preconditions.checkNotNull(inventory.get());
  }
}
