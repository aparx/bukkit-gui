package com.github.aparx.bgui.core.item;

import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 15:01
 * @since 1.0
 */
public interface InventoryItem extends InventoryClickHandler {

  @Nullable ItemStack get(@NonNull InventoryItemAccessor accessor);

}
