package com.github.aparx.bgui.core.item;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-26 01:54
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public class MutableInventoryItem implements InventoryItem {

  private @Nullable Function<InventoryItemAccessor, @Nullable ItemStack> itemFactory;
  private @Nullable InventoryClickHandler clickHandler;

  public MutableInventoryItem(
      @Nullable Function<InventoryItemAccessor, @Nullable ItemStack> itemFactory) {
    this.itemFactory = itemFactory;
  }

  public void update(MutableInventoryItem newItem) {
    set(newItem.getFactory());
    setClickHandler(newItem.getClickHandler());
  }

  public void set(Material material) {
    Preconditions.checkNotNull(material, "Material must not be null");
    set((accessor) -> new ItemStack(material));
  }

  public void set(@Nullable ItemStack itemStack) {
    set((accessor) -> itemStack);
  }

  public void set(@Nullable Function<InventoryItemAccessor, @Nullable ItemStack> itemFactory) {
    this.itemFactory = itemFactory;
  }

  public @Nullable Function<InventoryItemAccessor, @Nullable ItemStack> getFactory() {
    return itemFactory;
  }

  public void setClickHandler(@Nullable InventoryClickHandler clickHandle) {
    this.clickHandler = clickHandle;
  }

  public @Nullable InventoryClickHandler getClickHandler() {
    return clickHandler;
  }

  @Override
  public @Nullable ItemStack get(@NonNull InventoryItemAccessor accessor) {
    return (itemFactory != null ? itemFactory.apply(accessor) : null);
  }

  @Override
  public void handleClick(@NonNull InventoryItem item, @NonNull InventoryClickEvent event) {
    if (clickHandler != null) clickHandler.handleClick(item, event);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    MutableInventoryItem that = (MutableInventoryItem) object;
    return Objects.equals(itemFactory, that.itemFactory)
        && Objects.equals(clickHandler, that.clickHandler);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemFactory, clickHandler);
  }
}
