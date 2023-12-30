package com.github.aparx.bgui.core.item;

import com.github.aparx.bommons.ticks.ticker.Ticker;
import org.bukkit.inventory.Inventory;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 15:03
 * @since 1.0
 */
public interface InventoryItemAccessor {

  String getTitle();

  Inventory getInventory();

  Ticker getUpdateTicker();

}
