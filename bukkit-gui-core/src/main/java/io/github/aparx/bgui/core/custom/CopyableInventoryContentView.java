package io.github.aparx.bgui.core.custom;

import io.github.aparx.bgui.core.InventorySection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * InventoryContentView abstraction that also adds the ability to copy the underlying view.
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-12-26 00:54
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public abstract class CopyableInventoryContentView extends InventoryContentView {

  public CopyableInventoryContentView(InventorySection area, @Nullable InventorySection parent) {
    super(area, parent);
  }

  /**
   * Creates a shallow (or deep) copy of the underlying view.
   * <p>A shallow copy should be expected, such that direct mutations onto the returning view do
   * not have an effect onto this view.
   *
   * @return the copied view
   */
  public abstract InventoryContentView copy();

}
