package io.github.aparx.bgui.core.custom.populators;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.aparx.bgui.core.custom.content.pagination.InventoryPageGroup;
import io.github.aparx.bgui.core.custom.content.pagination.PaginationItemType;
import io.github.aparx.bgui.core.InventoryDimensions;
import io.github.aparx.bgui.core.InventoryPosition;
import io.github.aparx.bgui.core.InventorySection;
import io.github.aparx.bgui.core.custom.InventoryContentFactory;
import io.github.aparx.bgui.core.custom.InventoryContentView;
import io.github.aparx.bgui.core.item.InventoryItem;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.function.Function;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-26 05:28
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public final class InventoryPagePopulator implements InventoryPopulator<InventoryPageGroup> {

  private final InventoryPageGroup view;

  private InventoryPagePopulator(InventoryPageGroup view) {
    this.view = view;
  }

  /**
   * @see InventoryContentFactory#pageGroup(InventorySection)
   * @see InventoryContentFactory#pageGroup(InventoryDimensions)
   * @see InventoryContentFactory#pageGroup(InventoryContentView)
   * @see InventoryContentFactory#pageGroup(InventorySection, InventoryContentView)
   * @see InventoryContentFactory#pageGroup(InventoryDimensions, InventoryContentView)
   */
  public static InventoryPagePopulator create(InventoryPageGroup group) {
    Preconditions.checkNotNull(group, "Group must not be null");
    return new InventoryPagePopulator(group);
  }

  public static InventoryPagePopulator create(InventoryDimensions dimensions) {
    Preconditions.checkNotNull(dimensions, "Dimensions must not be null");
    return create(InventoryContentFactory.pageGroup(dimensions));
  }

  public static InventoryPagePopulator create(
      InventorySection section, InventoryContentView parent) {
    Preconditions.checkNotNull(section, "Section must not be null");
    return create(InventoryContentFactory.pageGroup(section, parent));
  }

  public static InventoryPagePopulator create(InventoryContentView parent) {
    Preconditions.checkNotNull(parent, "Parent must not be null");
    return create(InventoryContentFactory.pageGroup(parent));
  }

  public static InventoryPagePopulator create() {
    return create(InventoryDimensions.DEFAULT_DIMENSIONS);
  }

  @Override
  public InventoryPageGroup getView() {
    return view;
  }

  @CanIgnoreReturnValue
  public InventoryPagePopulator addPage(
      Function<InventoryPageGroup, InventoryContentView> pageFactory) {
    view.addPage(pageFactory.apply(view));
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryPagePopulator addPage(InventoryContentView page) {
    view.addPage(page);
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryPagePopulator addPage(InventoryPopulator<?> page) {
    return addPage(page.getView());
  }

  @CanIgnoreReturnValue
  public InventoryPagePopulator setItem(
      PaginationItemType type, InventoryItem item) {
    view.getItemHandler().set(type, item);
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryPagePopulator setItem(
      PaginationItemType type, InventoryPosition relative, InventoryItem item) {
    view.getItemHandler().set(type, relative, item);
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryPagePopulator setPlaceholder(@Nullable InventoryItem placeholder) {
    view.getItemHandler().setPlaceholder(placeholder);
    return this;
  }

}
