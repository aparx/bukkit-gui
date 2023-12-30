package com.github.aparx.bgui.core.provider;

import com.github.aparx.bgui.core.CustomInventory;
import com.github.aparx.bgui.core.content.InventoryContentView;
import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * An interface for updating the inventory content dynamically on different update stages of a
 * {@code CustomInventory}.
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-12-30 08:06
 * @since 2.0
 */
public interface InventoryProvider {

  @NonNull InventoryContentView init();

  @NonNull InventoryContentView update(CustomInventory accessor);

  /**
   * Returns a new provider, that always returns given view.
   * <p>The returned value is guaranteed to always be of type {@code StaticInventoryProvider}.
   *
   * @param staticView the static view that should be provided
   * @return the newly allocated (static) inventory provider, supplying {@code staticView}
   * @see StaticInventoryProvider
   */
  static InventoryProvider of(@NonNull InventoryContentView staticView) {
    Preconditions.checkNotNull(staticView, "View must not be null");
    return new StaticInventoryProvider(staticView);
  }

  @DefaultQualifier(NonNull.class)
  final class StaticInventoryProvider implements InventoryProvider {

    private final InventoryContentView view;

    StaticInventoryProvider(InventoryContentView view) {
      Preconditions.checkNotNull(view, "(Static) View must not be null");
      this.view = view;
    }

    @Override
    public InventoryContentView init() {
      return view;
    }

    @Override
    public InventoryContentView update(CustomInventory accessor) {
      return view;
    }
  }

}