package com.github.aparx.bgui.core.item;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 15:04
 * @since 1.0
 */
@FunctionalInterface
public interface InventoryClickHandler {

  InventoryClickHandler CANCEL = (__, event) -> event.setCancelled(true);

  void handleClick(@NonNull InventoryItem item, @NonNull InventoryClickEvent event);

  default InventoryClickHandler andThen(InventoryClickHandler handler) {
    return (handler != null ? (item, event) -> {
      this.handleClick(item, event);
      handler.handleClick(item, event);
    } : this);
  }

  /**
   * Returns a new handler that first invokes this handler and then cancels any passed event.
   *
   * @return the newly allocated handler
   */
  default InventoryClickHandler cancel() {
    return andThen(CANCEL);
  }

  /**
   * Returns a new handler that first invokes this handler and then updates the cancellation
   * state to {@code cancelled}.
   *
   * @return the newly allocated handler
   */
  default InventoryClickHandler cancel(boolean cancelled) {
    return (cancelled ? andThen(CANCEL) : andThen((__, event) -> event.setCancelled(false)));
  }

  /**
   * Returns a new action that calls {@code handler} when the click type equals to the given.
   *
   * @param handler the handler to be called when a click of given {@code type} is handled
   * @param type    the click type that triggers {@code handler} to be called
   * @return the newly allocated click handler
   */
  static InventoryClickHandler of(InventoryClickHandler handler, ClickType type) {
    Preconditions.checkNotNull(handler, "Handler must not be null");
    Preconditions.checkNotNull(type, "Type must not be null");
    return (item, event) -> {
      if (Objects.equals(type, event.getClick()))
        handler.handleClick(item, event);
    };
  }

  /**
   * Returns a new action that calls {@code handler} when the click type is any of the given.
   *
   * @param handler the handler to be called when a click of any given {@code types} is handled
   * @param types   the click type that triggers {@code handler} to be called
   * @return the newly allocated click handler
   */
  static InventoryClickHandler of(InventoryClickHandler handler, ClickType... types) {
    Preconditions.checkNotNull(handler, "Handler must not be null");
    Validate.noNullElements(types, "Type(s) must not be null");
    return (item, event) -> {
      if (ArrayUtils.contains(types, event.getClick()))
        handler.handleClick(item, event);
    };
  }

}
