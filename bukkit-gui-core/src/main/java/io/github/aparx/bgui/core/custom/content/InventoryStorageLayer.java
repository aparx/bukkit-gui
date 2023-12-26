package io.github.aparx.bgui.core.custom.content;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.aparx.bommons.core.IndexMap;
import io.github.aparx.bgui.core.InventoryDimensions;
import io.github.aparx.bgui.core.InventoryPosition;
import io.github.aparx.bgui.core.InventorySection;
import io.github.aparx.bgui.core.custom.CopyableInventoryContentView;
import io.github.aparx.bgui.core.item.InventoryItem;
import io.github.aparx.bgui.core.item.InventoryItemAccessor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Iterator;
import java.util.function.IntFunction;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 15:27
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public class InventoryStorageLayer extends CopyableInventoryContentView implements Iterable<@Nullable InventoryItem> {

  /** Growable and shrinkable array to avoid memory overhead */
  private final IndexMap<@Nullable InventoryItem> elementIndexMap = new IndexMap<>();

  public InventoryStorageLayer(InventorySection area, @Nullable InventorySection parent) {
    super(area, parent);
  }

  @Override
  public InventoryStorageLayer copy() {
    InventoryStorageLayer layer = new InventoryStorageLayer(getArea(), getParent());
    layer.elementIndexMap.putAll(elementIndexMap);
    return layer;
  }

  public boolean includes(int index) {
    return getArea().includes(index);
  }

  public boolean includes(InventoryPosition position) {
    return getArea().includes(position);
  }

  @Override
  public @Nullable InventoryItem get(
      @Nullable InventoryItemAccessor accessor, InventoryPosition position) {
    int elementIndex = toAreaElementIndex(position);
    if (elementIndex >= 0 && elementIndex < elementIndexMap.capacity())
      return elementIndexMap.get(elementIndex);
    return null;
  }

  @CanIgnoreReturnValue
  public @Nullable InventoryItem set(InventoryPosition position, @Nullable InventoryItem item) {
    int elementIndex = toAreaElementIndex(toAbsolute(position));
    if (elementIndex < 0)
      throw new IllegalArgumentException("Position is outside the view");
    return elementIndexMap.put(elementIndex, item);
  }

  @CanIgnoreReturnValue
  public @Nullable InventoryItem set(int elementIndex, @Nullable InventoryItem item) {
    Preconditions.checkElementIndex(elementIndex, getArea().size());
    return elementIndexMap.put(elementIndex, item);
  }

  @CanIgnoreReturnValue
  public @Nullable InventoryItem remove(int elementIndex) {
    Preconditions.checkElementIndex(elementIndex, getArea().size());
    return elementIndexMap.remove(elementIndex);
  }

  @CanIgnoreReturnValue
  public boolean remove(int elementIndex, @Nullable InventoryItem item) {
    Preconditions.checkElementIndex(elementIndex, getArea().size());
    return elementIndexMap.remove(elementIndex, item);
  }

  public void clear() {
    elementIndexMap.clear();
  }

  public void fill(IntFunction<@Nullable InventoryItem> itemFactory) {
    final int length = getArea().size();
    elementIndexMap.ensureCapacity(length);
    for (int i = 0; i < length; ++i)
      elementIndexMap.put(i, itemFactory.apply(i));
  }

  public void fill(@Nullable InventoryItem item) {
    fill((index) -> item);
  }

  public void fillEdges(@Nullable InventoryItem item) {
    int height = getDimensions().getHeight();
    if (height >= 1)
      fillTop(item);
    if (height >= 2)
      fillBottom(item);
    fillSides(item);
  }

  public void fillTop(@Nullable InventoryItem item) {
    for (int i = getDimensions().getWidth(); i > 0; --i)
      elementIndexMap.put(i - 1, item);
  }

  public void fillBottom(@Nullable InventoryItem item) {
    InventoryDimensions dim = getDimensions();
    int width = dim.getWidth();
    int fromIndex = dim.size() - width;
    for (int i = 0; i < width; ++i)
      elementIndexMap.put(fromIndex + i, item);
  }

  public void fillSides(@Nullable InventoryItem item) {
    InventoryDimensions dim = getDimensions();
    int width = dim.getWidth(), height = dim.getHeight();
    for (int row = 0; row < height; ++row) {
      set(InventoryPosition.ofPoint(0, row, width), item);
      set(InventoryPosition.ofPoint(width - 1, row, width), item);
    }
  }

  public void fillLeft(@Nullable InventoryItem item) {
    InventoryDimensions dim = getDimensions();
    int width = dim.getWidth(), height = dim.getHeight();
    for (int row = 0; row < height; ++row) {
      set(InventoryPosition.ofPoint(0, row, width), item);
    }
  }

  public void fillRight(@Nullable InventoryItem item) {
    InventoryDimensions dim = getDimensions();
    int width = dim.getWidth(), height = dim.getHeight();
    for (int row = 0; row < height; ++row) {
      set(InventoryPosition.ofPoint(width - 1, row, width), item);
    }
  }

  @Override
  public Iterator<@Nullable InventoryItem> iterator() {
    return new Iterator<>() {
      int cursor = 0;

      @Override
      public boolean hasNext() {
        return cursor < Math.min(getArea().size(), elementIndexMap.capacity());
      }

      @Override
      public @Nullable InventoryItem next() {
        return elementIndexMap.get(cursor++);
      }
    };
  }
}
