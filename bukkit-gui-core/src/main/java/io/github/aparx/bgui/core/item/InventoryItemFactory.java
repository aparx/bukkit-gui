package io.github.aparx.bgui.core.item;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import io.github.aparx.bommons.item.ItemStackSupplier;
import io.github.aparx.bommons.item.WrappedItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.function.Function;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 15:05
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public final class InventoryItemFactory {

  private InventoryItemFactory() {
    throw new AssertionError();
  }

  public static InventoryItemBuilder builder() {
    return new InventoryItemBuilder();
  }

  public static InventoryItemBuilder builder(InventoryItem source) {
    return new InventoryItemBuilder().item(source::get).setClickHandler(source);
  }

  public static MutableInventoryItem of(@Nullable ItemStack itemStack) {
    return builder().item(itemStack).build();
  }

  public static MutableInventoryItem of(@Nullable WrappedItemStack itemStack) {
    return builder().item(itemStack).build();
  }

  public static MutableInventoryItem cancel(@Nullable ItemStack itemStack) {
    return builder().item(itemStack).cancel().build();
  }

  public static MutableInventoryItem cancel(@Nullable WrappedItemStack itemStack) {
    return builder().item(itemStack).cancel().build();
  }

  public static MutableInventoryItem cancel(Material material) {
    return cancel(new ItemStack(material));
  }

  public static MutableInventoryItem cancel(Material material, int amount) {
    return cancel(new ItemStack(material, amount));
  }

  public static MutableInventoryItem cancel(
      @Nullable ItemStack itemStack, @Nullable InventoryClickHandler clickAction) {
    return builder().item(itemStack).cancel().addClickHandler(clickAction).build();
  }

  public static MutableInventoryItem cancel(
      @Nullable WrappedItemStack itemStack, @Nullable InventoryClickHandler clickAction) {
    return builder().item(itemStack).cancel().addClickHandler(clickAction).build();
  }

  public static MutableInventoryItem cancel(
      Material material, @Nullable InventoryClickHandler clickAction) {
    return cancel(new ItemStack(material), clickAction);
  }

  public static MutableInventoryItem cancel(
      Material material, int amount, @Nullable InventoryClickHandler clickAction) {
    return cancel(new ItemStack(material, amount), clickAction);
  }

  public static class InventoryItemBuilder {
    private @Nullable Function<InventoryItemAccessor, @Nullable ItemStack> itemFactory;
    private @Nullable InventoryClickHandler handler;

    protected InventoryItemBuilder() {}

    @CanIgnoreReturnValue
    public InventoryItemBuilder setClickHandler(@Nullable InventoryClickHandler handler) {
      this.handler = handler;
      return this;
    }

    @CanIgnoreReturnValue
    public InventoryItemBuilder addClickHandler(@Nullable InventoryClickHandler handler) {
      this.handler = (this.handler != null ? this.handler.andThen(handler) : handler);
      return this;
    }

    @CanIgnoreReturnValue
    public InventoryItemBuilder cancel() {
      this.handler = (handler != null ? handler.cancel() : InventoryClickHandler.CANCEL);
      return this;
    }

    @CanIgnoreReturnValue
    public InventoryItemBuilder item(
        @Nullable Function<InventoryItemAccessor, @Nullable ItemStack> itemFactory) {
      this.itemFactory = itemFactory;
      return this;
    }

    @CanIgnoreReturnValue
    public InventoryItemBuilder item(@Nullable ItemStackSupplier itemFactory) {
      this.itemFactory = (itemFactory != null ? (accessor) -> itemFactory.getItemStack() : null);
      return this;
    }

    @CanIgnoreReturnValue
    public InventoryItemBuilder item(@Nullable ItemStack itemStack) {
      this.itemFactory = (itemStack != null ? (accessor) -> itemStack : null);
      return this;
    }

    @CheckReturnValue
    public MutableInventoryItem build() {
      MutableInventoryItem inventoryItem = new MutableInventoryItem(itemFactory);
      inventoryItem.setClickHandler(handler);
      return inventoryItem;
    }
  }

}
