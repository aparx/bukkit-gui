package io.github.aparx.bgui.core.populators;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.aparx.bgui.core.InventoryContentView;
import io.github.aparx.bgui.core.content.pagination.InventoryPageGroup;
import io.github.aparx.bgui.core.content.pagination.PaginationItemType;
import io.github.aparx.bgui.core.dimension.InventoryPosition;
import io.github.aparx.bgui.core.item.InventoryItem;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-27 01:14
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public abstract class BasePageGroupPopulator<
    T extends InventoryContentView, S extends BasePageGroupPopulator<T, S>>
    implements InventoryPopulator<T> {

  public abstract InventoryPageGroup getPageGroup();

  @SuppressWarnings("unchecked")
  @CanIgnoreReturnValue
  public S setItem(
      PaginationItemType type, InventoryItem item) {
    getPageGroup().getItemHandler().set(type, item);
    return (S) this;
  }

  @SuppressWarnings("unchecked")
  @CanIgnoreReturnValue
  public S setItem(
      PaginationItemType type, InventoryPosition relative, InventoryItem item) {
    getPageGroup().getItemHandler().set(type, relative, item);
    return (S) this;
  }

  @SuppressWarnings("unchecked")
  @CanIgnoreReturnValue
  public S setPlaceholder(@Nullable InventoryItem placeholder) {
    getPageGroup().getItemHandler().setPlaceholder(placeholder);
    return (S) this;
  }

}
