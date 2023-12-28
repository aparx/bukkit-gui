package io.github.aparx.bgui.core.content;

import com.google.common.base.Preconditions;
import io.github.aparx.bgui.core.item.InventoryItem;
import io.github.aparx.bgui.core.item.InventoryItemAccessor;
import io.github.aparx.bgui.core.dimension.InventoryDimensions;
import io.github.aparx.bgui.core.dimension.InventoryPosition;
import io.github.aparx.bgui.core.dimension.InventorySection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Deterministic;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * Abstract class representing an adapter and access-point to an underlying two-dimensional pane.
 * <p>The content view is the primary building block of bukkit-gui. It acts as an adapter, with
 * which an item can be requested for a specific (absolute) inventory position. By default
 * definition, a requested item that is null equals out to not being existing. If the underlying
 * view represents a part of a structurally complex layer group, null represents transparency.
 * <p>A view always contains an optional parent, that is mostly only used for reference to make a
 * relative position absolute, so that an item can be requested correctly through this access-point.
 * <p>A view is not only defined by its parent, but also through its {@code area}, which is
 * relative to the parent area.
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 15:25
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public abstract class InventoryContentView {

  private final @Nullable InventorySection parent;
  private final InventorySection absoluteArea, relativeArea;
  private @Nullable InventorySection space;

  public InventoryContentView(InventorySection area, @Nullable InventorySection parent) {
    Preconditions.checkNotNull(area, "Area must not be null");
    this.parent = parent;
    this.relativeArea = area;
    if (hasParent()) {
      this.absoluteArea = area.toAbsolute(parent);
      Preconditions.checkArgument(parent.includes(absoluteArea), "Area is not within parent");
    } else
      this.absoluteArea = area;
  }

  /**
   * Returns the area of {@code view}, or null if it is null.
   *
   * @param view the view to get the area from
   * @return null if {@code view} is null, otherwise it's area
   * @see InventoryContentView#getArea()
   */
  public static @Nullable InventorySection getArea(@Nullable InventoryContentView view) {
    return (view != null ? view.getArea() : null);
  }

  /**
   * Returns the item at given {@code position}.
   * <p>Given {@code position} is <strong>absolute</strong>.
   * <p>Absolute positioning means, that the position is relative to the inventory itself.
   * If this view is only a partition of the entire inventory, the given position will
   * <italic>not</italic> be relative to that partition, but rather be relative in context to the
   * entire inventory (or parent in some cases).
   *
   * @param accessor the accessor, trying to access the underlying item
   * @param position the requesting (absolute!) position to access the target item
   * @return the item requested for {@code position}, or null to represent transparency
   * @apiNote Given {@code position} is <strong>absolute</strong>, meaning it may not be relative to
   * this view (coordinate-wise). Thus, it is possible that an item is requested at a position,
   * that falls out of this view. The solution to this issue is the sole responsibility of any
   * implementation (see {@code includes} methods in {@link InventorySection}).
   */
  public abstract @Nullable InventoryItem get(
      @Nullable InventoryItemAccessor accessor, InventoryPosition position);

  /**
   * Returns the totally absolute area.
   * <p>Totally absolute means, that it is relative to the root (being the inventory itself).
   *
   * @return the absolute area in which this view acts
   * @see #getRelativeArea()
   */
  public final InventorySection getArea() {
    return absoluteArea;
  }

  /**
   * Returns the relative area, being relative to this parent (if given).
   * <p>The relative area is a kind of offset to the parent. To determine the totally absolute
   * area (so including the absolute positions), the relative area is simply offset by the parent
   * area. More specifically, the absolute area equals the relative area plus the beginning index
   * of the parent. If no parent is given, the relative area is equivalent to the absolute area.
   * <p>The relative area is the area, that is initially given to the view.
   *
   * @return the relative area
   */
  public final InventorySection getRelativeArea() {
    return relativeArea;
  }

  /**
   * Returns a section that represents the occupying space, starting at {@code [0, 0]} and
   * expanding towards the dimensions of this view.
   *
   * @return the section's occupying space in a 2D pane (beginning offset subtracted)
   */
  public final InventorySection getSpace() {
    if (space != null)
      return space;
    synchronized (this) {
      if (space != null)
        return space;
      InventoryPosition end = relativeArea.getEnd(), begin = relativeArea.getBegin();
      return (space = InventorySection.of(InventoryPosition.ofZero(), InventoryPosition.ofPoint(
          end.getColumn() - begin.getColumn(), end.getRow() - begin.getRow()
      )));
    }
  }

  @Deterministic
  public final @NonNull InventoryDimensions getDimensions() {
    return getArea().getDimensions();
  }

  public final boolean hasParent() {
    return parent != null;
  }

  public @Nullable InventorySection getParent() {
    return parent;
  }

  /**
   * Flattens (non-relative) {@code position} to a sequential element index relative to this area.
   *
   * @param position the (absolute) view to map to an element index
   * @return the element index, or {@code -1} if {@code position} lies outside this area
   */
  public int toAreaElementIndex(InventoryPosition position) {
    // positionalIndex is the absolute position (not the element index!)
    if (!getArea().includes(position)) return -1;
    InventoryPosition begin = absoluteArea.getBegin();
    return (position.getIndex() - begin.getIndex())
        - (position.getRow() - begin.getRow())
        * (position.getWidth() - getDimensions().getWidth());
  }

  /** @see InventorySection#toAbsolute(InventoryPosition) */
  public InventoryPosition toAbsolute(InventoryPosition position) {
    return absoluteArea.toAbsolute(position);
  }

}
