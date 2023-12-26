package io.github.aparx.bgui.custom;

import com.google.common.base.Preconditions;
import io.github.aparx.bgui.InventoryDimensions;
import io.github.aparx.bgui.InventoryPosition;
import io.github.aparx.bgui.InventorySection;
import io.github.aparx.bgui.item.InventoryItem;
import io.github.aparx.bgui.item.InventoryItemAccessor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Deterministic;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 15:25
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public abstract class InventoryContentView {

  private final @Nullable InventorySection parent;
  private final InventorySection area;

  public InventoryContentView(InventorySection area, @Nullable InventorySection parent) {
    Preconditions.checkNotNull(area, "Area must not be null");
    this.parent = parent;
    this.area = area;
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

  public final InventorySection getArea() {
    return area;
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
    InventoryPosition begin = area.getBegin();
    return (position.getIndex() - begin.getIndex())
        - (position.getRow() - begin.getRow())
        * (position.getWidth() - getDimensions().getWidth());
  }

  /**
   * Returns the given relative {@code position} as an absolute position, relative to the parent.
   *
   * <p>Assuming the {@code area} begins at {@code [1, 1]} and ends at {@code [3, 3]},
   * following examples can be used:
   * <ul>
   *   <li>{@code fromRelative(InventoryPosition.ofFirst()) ::= [1, 1]}</li>
   *   <li>{@code fromRelative(InventoryPosition.ofPoint(0, 0)) ::= [1, 1]}</li>
   *   <li>{@code fromRelative(InventoryPosition.ofLast(getArea())) ::= [3, 3]}</li>
   *   <li>{@code fromRelative(InventoryPosition.ofPoint(2, 2)) ::= [3, 3]}</li>
   *   <li>{@code fromRelative(InventoryPosition.ofPoint(1, 0)) ::= [2, 1]}</li>
   *   <li>{@code fromRelative(InventoryPosition.ofPoint(2, 0)) ::= [3, 1]}</li>
   *   <li>{@code fromRelative(InventoryPosition.ofPoint(3, 0)) ::= [4, 1]} <b>(outside)</b></li>
   *   <li>{@code fromRelative(InventoryPosition.ofPoint(0, 1)) ::= [1, 2]}</li>
   * </ul>
   *
   * @param position the relative position to transform into a normal position
   * @return the new non-relative position
   */
  public InventoryPosition toAbsolute(InventoryPosition position) {
    return (parent != null ? position.toRelative(parent) : position).shift(area.getBegin().getIndex());
  }

}
