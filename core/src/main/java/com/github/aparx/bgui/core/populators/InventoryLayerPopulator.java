package com.github.aparx.bgui.core.populators;

import com.github.aparx.bgui.core.content.InventoryContentFactory;
import com.github.aparx.bgui.core.content.InventoryContentView;
import com.github.aparx.bgui.core.content.InventoryLayerGroup;
import com.github.aparx.bgui.core.dimension.InventoryDimensions;
import com.github.aparx.bgui.core.dimension.InventorySection;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Collection;
import java.util.function.Function;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-27 01:53
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public class InventoryLayerPopulator implements InventoryPopulator<InventoryLayerGroup> {

  private final InventoryLayerGroup view;

  private InventoryLayerPopulator(InventoryLayerGroup view) {
    this.view = view;
  }

  /**
   * @see InventoryContentFactory#layerGroup(InventorySection)
   * @see InventoryContentFactory#layerGroup(InventoryDimensions)
   * @see InventoryContentFactory#layerGroup(InventoryContentView)
   * @see InventoryContentFactory#layerGroup(InventorySection, InventoryContentView)
   * @see InventoryContentFactory#layerGroup(InventoryDimensions, InventoryContentView)
   */
  public static InventoryLayerPopulator populate(InventoryLayerGroup group) {
    Preconditions.checkNotNull(group, "Group must not be null");
    return new InventoryLayerPopulator(group);
  }

  /** @see #populate(InventoryLayerGroup) */
  public static InventoryLayerPopulator create(InventoryDimensions dimensions) {
    Preconditions.checkNotNull(dimensions, "Dimensions must not be null");
    return populate(InventoryContentFactory.layerGroup(dimensions));
  }

  /** @see #populate(InventoryLayerGroup) */
  public static InventoryLayerPopulator create(
      InventorySection relativeArea, InventoryContentView parent) {
    Preconditions.checkNotNull(relativeArea, "Section must not be null");
    return populate(InventoryContentFactory.layerGroup(relativeArea, parent));
  }

  /** @see #populate(InventoryLayerGroup) */
  public static InventoryLayerPopulator create(InventoryContentView parent) {
    Preconditions.checkNotNull(parent, "Parent must not be null");
    return populate(InventoryContentFactory.layerGroup(parent));
  }

  /** @see #populate(InventoryLayerGroup) */
  public static InventoryLayerPopulator create() {
    return create(InventoryDimensions.DEFAULT_DIMENSIONS);
  }

  @CanIgnoreReturnValue
  public InventoryLayerPopulator addLayer(InventoryContentView layer) {
    this.view.addLayer(layer);
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryLayerPopulator addLayer(
      Function<InventoryLayerGroup, InventoryContentView> layerFactory) {
    this.view.addLayer(layerFactory.apply(this.view));
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryLayerPopulator addLayers(InventoryContentView... layers) {
    this.view.addLayers(layers);
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryLayerPopulator addLayers(
      Function<InventoryLayerGroup, Collection<? extends InventoryContentView>> layerFactory) {
    // TODO add addLayers(Iterable) & addLayers(Collection) to InventoryLayerView and this populator
    return addLayers(layerFactory.apply(this.view).toArray(new InventoryContentView[0]));
  }

  @CanIgnoreReturnValue
  public InventoryLayerPopulator setLayer(int index, InventoryContentView layer) {
    this.view.setLayer(index, layer);
    return this;
  }

  public InventoryLayerGroup getView() {
    return view;
  }
}
