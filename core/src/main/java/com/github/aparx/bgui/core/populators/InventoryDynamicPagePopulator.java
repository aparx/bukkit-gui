package com.github.aparx.bgui.core.populators;

import com.github.aparx.bgui.core.content.InventoryContentFactory;
import com.github.aparx.bgui.core.content.InventoryContentView;
import com.github.aparx.bgui.core.content.pagination.InventoryDynamicPageGroup;
import com.github.aparx.bgui.core.dimension.InventoryDimensions;
import com.github.aparx.bgui.core.dimension.InventorySection;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.github.aparx.bgui.core.content.pagination.InventoryPageGroup;
import com.github.aparx.bgui.core.item.InventoryItem;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-26 17:02
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public class InventoryDynamicPagePopulator extends BasePageGroupPopulator<InventoryDynamicPageGroup, InventoryDynamicPagePopulator> {

  private final InventoryDynamicPageGroup view;

  private InventoryDynamicPagePopulator(InventoryDynamicPageGroup view) {
    this.view = view;
  }

  /**
   * @see InventoryContentFactory#dynamicPageGroup(InventorySection)
   * @see InventoryContentFactory#dynamicPageGroup(InventoryDimensions)
   * @see InventoryContentFactory#dynamicPageGroup(InventoryContentView)
   * @see InventoryContentFactory#dynamicPageGroup(InventorySection, InventoryContentView)
   * @see InventoryContentFactory#dynamicPageGroup(InventoryDimensions, InventoryContentView)
   */
  public static InventoryDynamicPagePopulator populate(InventoryDynamicPageGroup group) {
    Preconditions.checkNotNull(group, "Group must not be null");
    return new InventoryDynamicPagePopulator(group);
  }

  /** @see #populate(InventoryDynamicPageGroup) */
  public static InventoryDynamicPagePopulator create(InventoryDimensions dimensions) {
    Preconditions.checkNotNull(dimensions, "Dimensions must not be null");
    return populate(InventoryContentFactory.dynamicPageGroup(dimensions));
  }

  /** @see #populate(InventoryDynamicPageGroup) */
  public static InventoryDynamicPagePopulator create(
      InventorySection relativeArea, InventoryContentView parent) {
    Preconditions.checkNotNull(relativeArea, "Section must not be null");
    return populate(InventoryContentFactory.dynamicPageGroup(relativeArea, parent));
  }

  /** @see #populate(InventoryDynamicPageGroup) */
  public static InventoryDynamicPagePopulator create(InventoryContentView parent) {
    Preconditions.checkNotNull(parent, "Parent must not be null");
    return populate(InventoryContentFactory.dynamicPageGroup(parent));
  }

  /** @see #populate(InventoryDynamicPageGroup) */
  public static InventoryDynamicPagePopulator create() {
    return create(InventoryDimensions.DEFAULT_DIMENSIONS);
  }

  @Override
  public @NonNull InventoryDynamicPageGroup getView() {
    return view;
  }

  @CanIgnoreReturnValue
  public InventoryDynamicPagePopulator addElement(@Nullable InventoryItem item) {
    view.getElements().add(item);
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryDynamicPagePopulator addElements(
      Collection<@Nullable ? extends InventoryItem> items) {
    view.getElements().addAll(items);
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryDynamicPagePopulator addElements(InventoryItem... item) {
    if (ArrayUtils.isNotEmpty(item))
      return addElements(Arrays.asList(item));
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryDynamicPagePopulator setElements(
      Collection<@Nullable ? extends InventoryItem> items) {
    view.getElements().clear();
    view.getElements().addAll(items);
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryDynamicPagePopulator setElements(InventoryItem... item) {
    return setElements(Arrays.asList(item));
  }

  @CanIgnoreReturnValue
  public InventoryDynamicPagePopulator clear() {
    view.getElements().clear();
    return this;
  }

  @Override
  public InventoryPageGroup getPageGroup() {
    return view.getGroup();
  }
}
