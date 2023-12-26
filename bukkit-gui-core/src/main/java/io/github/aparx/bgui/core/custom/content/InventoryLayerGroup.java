package io.github.aparx.bgui.core.custom.content;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.aparx.bgui.core.InventoryPosition;
import io.github.aparx.bgui.core.InventorySection;
import io.github.aparx.bgui.core.custom.CopyableInventoryContentView;
import io.github.aparx.bgui.core.item.InventoryItem;
import io.github.aparx.bgui.core.item.InventoryItemAccessor;
import io.github.aparx.bgui.core.custom.InventoryContentView;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 15:28
 * @since 1.0
 */
// A page consists out of multiple layers of content sections
// A page represents the top layer content of an inventory
@DefaultQualifier(NonNull.class)
public class InventoryLayerGroup extends CopyableInventoryContentView {

  /** A map of each index of this page, mapped to a specific section */
  private final List<InventoryContentView> layers = new ArrayList<>();

  public InventoryLayerGroup(InventorySection area, @Nullable InventorySection parent) {
    super(area, parent);
  }

  @Override
  public InventoryContentView copy() {
    InventoryLayerGroup layerGroup = new InventoryLayerGroup(getArea(), getParent());
    layerGroup.layers.addAll(layers);
    return layerGroup;
  }

  public void clear() {
    layers.clear();
  }

  public void addLayer(InventoryContentView layerView) {
    Preconditions.checkNotNull(layerView, "Layer must not be null");
    InventorySection section = layerView.getArea();
    Preconditions.checkArgument(getArea().includes(section), "Layer is not within page");
    layers.add(layerView);
  }

  public void addLayers(InventoryContentView... layers) {
    Validate.noNullElements(layers, "Layer(s) must not be null");
    this.layers.addAll(Arrays.asList(layers));
  }

  @CanIgnoreReturnValue
  public @Nullable InventoryContentView setLayer(int layerIndex, InventoryContentView layerView) {
    Preconditions.checkNotNull(layerView, "Layer must not be null");
    return layers.set(layerIndex, layerView);
  }

  public InventoryContentView getLayer(int layerIndex) {
    Preconditions.checkElementIndex(layerIndex, layers.size());
    return layers.get(layerIndex);
  }

  @Override
  public @Nullable InventoryItem get(
      @Nullable InventoryItemAccessor accessor, InventoryPosition position) {
    if (!getArea().includes(position))
      return null;
    for (int i = layers.size(); i > 0; --i) {
      InventoryContentView layerView = layers.get(i - 1);
      @Nullable InventoryItem inventoryItem = layerView.get(accessor, position);
      if (inventoryItem != null) return inventoryItem;
    }
    return null;
  }

}