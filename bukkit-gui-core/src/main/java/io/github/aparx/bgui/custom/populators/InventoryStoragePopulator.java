package io.github.aparx.bgui.custom.populators;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.aparx.bgui.custom.populators.interpolator.LineInterpolator;
import io.github.aparx.bgui.InventoryDimensions;
import io.github.aparx.bgui.InventoryPosition;
import io.github.aparx.bgui.InventorySection;
import io.github.aparx.bgui.custom.InventoryContentFactory;
import io.github.aparx.bgui.custom.InventoryContentView;
import io.github.aparx.bgui.custom.content.InventoryStorageLayer;
import io.github.aparx.bgui.item.InventoryItem;
import io.github.aparx.bgui.item.InventoryItemClickAction;
import io.github.aparx.bgui.item.InventoryItemFactory;
import io.github.aparx.bommons.item.WrappedItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-26 02:48
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public final class InventoryStoragePopulator implements InventoryPopulator<InventoryStorageLayer> {

  private final InventoryStorageLayer view;

  private InventoryStoragePopulator(InventoryStorageLayer view) {
    this.view = view;
  }

  /**
   * @see InventoryContentFactory#storageLayer(InventorySection)
   * @see InventoryContentFactory#storageLayer(InventoryDimensions)
   * @see InventoryContentFactory#storageLayer(InventoryContentView)
   * @see InventoryContentFactory#storageLayer(InventorySection, InventoryContentView)
   * @see InventoryContentFactory#storageLayer(InventoryDimensions, InventoryContentView)
   */
  public static InventoryStoragePopulator create(InventoryStorageLayer view) {
    Preconditions.checkNotNull(view, "View must not be null");
    return new InventoryStoragePopulator(view);
  }

  public static InventoryStoragePopulator create(InventoryDimensions dimensions) {
    Preconditions.checkNotNull(dimensions, "Dimensions must not be null");
    return create(InventoryContentFactory.storageLayer(dimensions));
  }

  public static InventoryStoragePopulator create(
      InventorySection section, InventoryContentView parent) {
    Preconditions.checkNotNull(section, "Section must not be null");
    return create(InventoryContentFactory.storageLayer(section, parent));
  }

  public static InventoryStoragePopulator create(InventoryContentView parent) {
    Preconditions.checkNotNull(parent, "Parent must not be null");
    return create(InventoryContentFactory.storageLayer(parent));
  }

  public static InventoryStoragePopulator create() {
    return create(InventoryDimensions.DEFAULT_DIMENSIONS);
  }

  @Override
  public InventoryStorageLayer getView() {
    return view;
  }

  // +------------------ set (1) ------------------+

  @CanIgnoreReturnValue
  public InventoryStoragePopulator set(
      InventoryPosition position,
      @Nullable InventoryItem item) {
    view.set(position, item);
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator set(
      InventoryPosition position,
      @Nullable ItemStack itemStack) {
    return set(position, InventoryItemFactory.cancel(itemStack));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator set(
      InventoryPosition position,
      @Nullable ItemStack itemStack,
      @Nullable InventoryItemClickAction clickHandler) {
    return set(position, InventoryItemFactory.cancel(itemStack, clickHandler));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator set(
      InventoryPosition position,
      @Nullable WrappedItemStack itemStack) {
    return set(position, InventoryItemFactory.cancel(itemStack));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator set(
      InventoryPosition position,
      @Nullable WrappedItemStack itemStack,
      @Nullable InventoryItemClickAction clickHandler) {
    return set(position, InventoryItemFactory.cancel(itemStack, clickHandler));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator set(InventoryPosition position, Material material) {
    return set(position, InventoryItemFactory.cancel(material));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator set(
      InventoryPosition position,
      Material material,
      @Nullable InventoryItemClickAction clickHandler) {
    return set(position, InventoryItemFactory.cancel(material, clickHandler));
  }

  // +------------------ set (2) ------------------+

  @CanIgnoreReturnValue
  public InventoryStoragePopulator set(
      int relativeIndex,
      @Nullable InventoryItem item) {
    view.set(relativeIndex, item);
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator set(
      int relativeIndex,
      @Nullable ItemStack itemStack) {
    return set(relativeIndex, InventoryItemFactory.cancel(itemStack));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator set(
      int relativeIndex,
      @Nullable ItemStack itemStack,
      @Nullable InventoryItemClickAction clickHandler) {
    return set(relativeIndex, InventoryItemFactory.cancel(itemStack, clickHandler));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator set(
      int relativeIndex,
      @Nullable WrappedItemStack itemStack) {
    return set(relativeIndex, InventoryItemFactory.cancel(itemStack));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator set(
      int relativeIndex,
      @Nullable WrappedItemStack itemStack,
      @Nullable InventoryItemClickAction clickHandler) {
    return set(relativeIndex, InventoryItemFactory.cancel(itemStack, clickHandler));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator set(int relativeIndex, Material material) {
    return set(relativeIndex, InventoryItemFactory.cancel(material));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator set(
      int relativeIndex,
      Material material,
      @Nullable InventoryItemClickAction clickHandler) {
    return set(relativeIndex, InventoryItemFactory.cancel(material, clickHandler));
  }

  // +------------------ outline ------------------+

  @CanIgnoreReturnValue
  public InventoryStoragePopulator outline(@Nullable InventoryItem item) {
    view.fillEdges(item);
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator outline(@Nullable ItemStack itemStack) {
    view.fillEdges(InventoryItemFactory.cancel(itemStack));
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator outline(
      @Nullable ItemStack itemStack, @Nullable InventoryItemClickAction clickHandler) {
    view.fillEdges(InventoryItemFactory.cancel(itemStack, clickHandler));
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator outline(@Nullable WrappedItemStack itemStack) {
    view.fillEdges(InventoryItemFactory.cancel(itemStack));
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator outline(
      @Nullable WrappedItemStack itemStack, @Nullable InventoryItemClickAction clickHandler) {
    view.fillEdges(InventoryItemFactory.cancel(itemStack, clickHandler));
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator outline(Material material) {
    view.fillEdges(InventoryItemFactory.cancel(material));
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator outline(
      Material material, @Nullable InventoryItemClickAction clickHandler) {
    view.fillEdges(InventoryItemFactory.cancel(material, clickHandler));
    return this;
  }

  // +------------------ fill ------------------+

  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(@Nullable InventoryItem item) {
    view.fill(item);
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(@Nullable ItemStack itemStack) {
    view.fill(InventoryItemFactory.cancel(itemStack));
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      @Nullable ItemStack itemStack, @Nullable InventoryItemClickAction clickHandler) {
    view.fill(InventoryItemFactory.cancel(itemStack, clickHandler));
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(@Nullable WrappedItemStack itemStack) {
    view.fill(InventoryItemFactory.cancel(itemStack));
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      @Nullable WrappedItemStack itemStack, @Nullable InventoryItemClickAction clickHandler) {
    view.fill(InventoryItemFactory.cancel(itemStack, clickHandler));
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(Material material) {
    view.fill(InventoryItemFactory.cancel(material));
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      Material material, @Nullable InventoryItemClickAction clickHandler) {
    view.fill(InventoryItemFactory.cancel(material, clickHandler));
    return this;
  }

  // +------------------ fill (2) ------------------+

  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      FillType fillType,
      @Nullable InventoryItem item) {
    fillType.fill(view, item);
    return this;
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      FillType fillType,
      @Nullable ItemStack itemStack) {
    return fill(fillType, InventoryItemFactory.cancel(itemStack));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      FillType fillType,
      @Nullable ItemStack itemStack,
      @Nullable InventoryItemClickAction clickHandler) {
    return fill(fillType, InventoryItemFactory.cancel(itemStack, clickHandler));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      FillType fillType,
      @Nullable WrappedItemStack itemStack) {
    return fill(fillType, InventoryItemFactory.cancel(itemStack));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      FillType fillType,
      @Nullable WrappedItemStack itemStack,
      @Nullable InventoryItemClickAction clickHandler) {
    return fill(fillType, InventoryItemFactory.cancel(itemStack, clickHandler));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      FillType fillType,
      Material material) {
    return fill(fillType, InventoryItemFactory.cancel(material));
  }

  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      FillType fillType,
      Material material,
      @Nullable InventoryItemClickAction clickHandler) {
    return fill(fillType, InventoryItemFactory.cancel(material, clickHandler));
  }

  // +------------------ fill (3) ------------------+

  /**
   * Fills this view with given {@code area}.
   * <p>The input area is <strong>relative</strong> to this view's area.
   *
   * @param area the area to fill
   * @param item the item to be used as the filling
   * @return this instance
   */
  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      InventorySection area,
      @Nullable InventoryItem item) {
    area.forEach((pos) -> view.set(pos, item));
    return this;
  }

  /** @see #fill(InventorySection, InventoryItem) */
  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      InventorySection area,
      @Nullable ItemStack itemStack) {
    return fill(area, InventoryItemFactory.cancel(itemStack));
  }

  /** @see #fill(InventorySection, InventoryItem) */
  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      InventorySection area,
      @Nullable ItemStack itemStack,
      @Nullable InventoryItemClickAction clickHandler) {
    return fill(area, InventoryItemFactory.cancel(itemStack, clickHandler));
  }

  /** @see #fill(InventorySection, InventoryItem) */
  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      InventorySection area,
      @Nullable WrappedItemStack itemStack) {
    return fill(area, InventoryItemFactory.cancel(itemStack));
  }

  /** @see #fill(InventorySection, InventoryItem) */
  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      InventorySection area,
      @Nullable WrappedItemStack itemStack,
      @Nullable InventoryItemClickAction clickHandler) {
    return fill(area, InventoryItemFactory.cancel(itemStack, clickHandler));
  }

  /** @see #fill(InventorySection, InventoryItem) */
  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      InventorySection area,
      Material material) {
    return fill(area, InventoryItemFactory.cancel(material));
  }

  /** @see #fill(InventorySection, InventoryItem) */
  @CanIgnoreReturnValue
  public InventoryStoragePopulator fill(
      InventorySection area,
      Material material,
      @Nullable InventoryItemClickAction clickHandler) {
    return fill(area, InventoryItemFactory.cancel(material, clickHandler));
  }

  // +------------------ line ------------------+

  /**
   * Draws a line with {@code item} from {@code start} (inclusive) to {@code stop} (inclusive).
   * <p>The underlying algorithm is {@code Bresenham's Line Algorithm}.
   * <p>The input positions are <strong>relative</strong> to this storage's area.
   *
   * @param start the position from where to start the line (inclusive).
   * @param stop  the position to where the line leads to (inclusive).
   * @param item  the item to be put between both points
   * @return this populator
   * @see
   * <a href="https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm#Algorithm_for_integer_arithmetic">
   * https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm#Algorithm_for_integer_arithmetic</a>
   */
  @CanIgnoreReturnValue
  public InventoryStoragePopulator line(
      InventoryPosition start, InventoryPosition stop, @Nullable InventoryItem item) {
    Preconditions.checkArgument(view.includes(start), "Starting position is out of layer's area");
    Preconditions.checkArgument(view.includes(stop), "Stopping position is out of layer's area");
    // https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
    boolean swapPos = start.compareTo(stop) > 0;
    new LineInterpolator(
        swapPos ? stop : start,
        swapPos ? start : stop
    ).forEach((pos) -> view.set(pos, item));
    return this;
  }

  /** @see #line(InventoryPosition, InventoryPosition, InventoryItem) */
  @CanIgnoreReturnValue
  public InventoryStoragePopulator line(
      InventoryPosition start, InventoryPosition stop, @Nullable ItemStack itemStack) {
    return line(start, stop, InventoryItemFactory.cancel(itemStack));
  }

  /** @see #line(InventoryPosition, InventoryPosition, InventoryItem) */
  @CanIgnoreReturnValue
  public InventoryStoragePopulator line(
      InventoryPosition start,
      InventoryPosition stop,
      @Nullable ItemStack itemStack,
      InventoryItemClickAction clickHandler) {
    return line(start, stop, InventoryItemFactory.cancel(itemStack, clickHandler));
  }

  /** @see #line(InventoryPosition, InventoryPosition, InventoryItem) */
  @CanIgnoreReturnValue
  public InventoryStoragePopulator line(
      InventoryPosition start, InventoryPosition stop, @Nullable WrappedItemStack itemStack) {
    return line(start, stop, InventoryItemFactory.cancel(itemStack));
  }

  /** @see #line(InventoryPosition, InventoryPosition, InventoryItem) */
  @CanIgnoreReturnValue
  public InventoryStoragePopulator line(
      InventoryPosition start,
      InventoryPosition stop,
      @Nullable WrappedItemStack itemStack,
      InventoryItemClickAction clickHandler) {
    return line(start, stop, InventoryItemFactory.cancel(itemStack, clickHandler));
  }

  /** @see #line(InventoryPosition, InventoryPosition, InventoryItem) */
  @CanIgnoreReturnValue
  public InventoryStoragePopulator line(
      InventoryPosition start, InventoryPosition stop, Material material) {
    return line(start, stop, InventoryItemFactory.cancel(material));
  }

  /** @see #line(InventoryPosition, InventoryPosition, InventoryItem) */
  @CanIgnoreReturnValue
  public InventoryStoragePopulator line(
      InventoryPosition start,
      InventoryPosition stop,
      Material material,
      InventoryItemClickAction clickHandler) {
    return line(start, stop, InventoryItemFactory.cancel(material, clickHandler));
  }

  public enum FillType {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
    SIDES,
    EDGES,
    /** Fill the entire section */
    COVER;

    public final void fill(InventoryStorageLayer layer, @Nullable InventoryItem item) {
      switch (this) {
        case TOP:
          layer.fillTop(item);
          break;
        case BOTTOM:
          layer.fillBottom(item);
          break;
        case LEFT:
          layer.fillLeft(item);
          break;
        case RIGHT:
          layer.fillRight(item);
          break;
        case EDGES:
          layer.fillEdges(item);
          break;
        case SIDES:
          layer.fillSides(item);
          break;
        case COVER:
          layer.fill(item);
          break;
        default:
          throw new IllegalArgumentException("fillType unsupported");
      }
    }
  }
}
