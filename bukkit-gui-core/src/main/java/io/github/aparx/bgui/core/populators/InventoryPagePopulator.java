package io.github.aparx.bgui.core.populators;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.aparx.bgui.core.content.pagination.InventoryPageGroup;
import io.github.aparx.bgui.core.dimension.InventoryDimensions;
import io.github.aparx.bgui.core.dimension.InventorySection;
import io.github.aparx.bgui.core.content.InventoryContentFactory;
import io.github.aparx.bgui.core.content.InventoryContentView;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.function.Function;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-26 05:28
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public final class InventoryPagePopulator extends BasePageGroupPopulator<InventoryPageGroup, InventoryPagePopulator> {

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
  public static InventoryPagePopulator populate(InventoryPageGroup group) {
    Preconditions.checkNotNull(group, "Group must not be null");
    return new InventoryPagePopulator(group);
  }

  /** @see #populate(InventoryPageGroup) */
  public static InventoryPagePopulator create(InventoryDimensions dimensions) {
    Preconditions.checkNotNull(dimensions, "Dimensions must not be null");
    return populate(InventoryContentFactory.pageGroup(dimensions));
  }

  /** @see #populate(InventoryPageGroup) */
  public static InventoryPagePopulator create(
      InventorySection section, InventoryContentView parent) {
    Preconditions.checkNotNull(section, "Section must not be null");
    return populate(InventoryContentFactory.pageGroup(section, parent));
  }

  /** @see #populate(InventoryPageGroup) */
  public static InventoryPagePopulator create(InventoryContentView parent) {
    Preconditions.checkNotNull(parent, "Parent must not be null");
    return populate(InventoryContentFactory.pageGroup(parent));
  }

  /** @see #populate(InventoryPageGroup) */
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

  @Override
  public InventoryPageGroup getPageGroup() {
    return view;
  }
}
