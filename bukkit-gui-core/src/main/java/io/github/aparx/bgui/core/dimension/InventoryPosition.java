package io.github.aparx.bgui.core.dimension;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CheckReturnValue;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.awt.*;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 12:46
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public final class InventoryPosition implements Comparable<InventoryPosition>,
    ConfigurationSerializable {

  /** Pool of {@code InventoryPosition} instances frequently used (avoids allocation overhead) */
  private static final InventoryPositionInstancePool positionInstancePool =
      new InventoryPositionInstancePool(InventoryDimensions.DEFAULT_WIDTH, 4);

  private static final InventoryPosition ZERO_POSITION =
      new InventoryPosition(0, InventoryDimensions.DEFAULT_WIDTH);

  private final int index;
  /** The initial carrier width that forms `column` and `row` (and more) */
  private final int width;
  /** Default column and row position for `width` */
  private final int column, row;

  private InventoryPosition(int index, int width) {
    this.index = index;
    this.width = width;
    this.column = toColumn(index, width);
    this.row = toRow(index, width);
  }

  public static InventoryPosition ofZero() {
    return ZERO_POSITION;
  }

  public static InventoryPosition ofLast(InventorySection section) {
    return ofIndex(section.size() - 1, section.getDimensions().getWidth());
  }

  public static InventoryPosition ofLast(InventoryDimensions dimensions) {
    return ofIndex(dimensions.size() - 1, dimensions.getWidth());
  }

  public static InventoryPosition ofPoint(int column, int row, int width) {
    if (column == 0 && row == 0 && width == ZERO_POSITION.getWidth())
      return ZERO_POSITION;
    Preconditions.checkArgument(column >= 0, "Column must not be negative");
    Preconditions.checkArgument(row >= 0, "Row must not be negative");
    return positionInstancePool.atPoint(column, row, width);
  }

  public static InventoryPosition ofPoint(int column, int row) {
    return ofPoint(column, row, InventoryDimensions.DEFAULT_WIDTH);
  }

  public static InventoryPosition ofPoint(Point point, int width) {
    return ofPoint(point.x, point.y, width);
  }

  public static InventoryPosition ofPoint(Point point) {
    return ofPoint(point, InventoryDimensions.DEFAULT_WIDTH);
  }

  public static InventoryPosition ofIndex(int index, int width) {
    return ofPoint(index % width, index / width, width);
  }

  public static InventoryPosition ofIndex(int index) {
    return ofIndex(index, InventoryDimensions.DEFAULT_WIDTH);
  }

  public static int toIndex(int column, int row, int width) {
    Preconditions.checkArgument(column < width, "Column must be less than the width", column, width);
    return column + Math.max(row * width, 0);
  }

  public static int toIndex(int column, int row) {
    return toIndex(column, row, InventoryDimensions.DEFAULT_WIDTH);
  }

  public static int toColumn(int index, int width) {
    // hotspot inline from JIT expected
    return index % width;
  }

  public static int toRow(int index, int width) {
    // hotspot inline from JIT expected
    return index / width;
  }

  public static InventoryPosition getMin(InventoryPosition x, InventoryPosition y) {
    return ofPoint(Math.min(x.getColumn(), y.getColumn()), Math.min(x.getRow(), y.getRow()));
  }

  public static InventoryPosition getMax(InventoryPosition x, InventoryPosition y) {
    return ofPoint(Math.max(x.getColumn(), y.getColumn()), Math.max(x.getRow(), y.getRow()));
  }

  public static InventoryPosition deserialize(Map<?, ?> args) {
    @Nullable Object widthObject = args.get("width");
    return ofIndex(NumberConversions.toInt(args.get("index")),
        widthObject != null
            ? NumberConversions.toInt(widthObject)
            : InventoryDimensions.DEFAULT_WIDTH);
  }

  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> map = new HashMap<>();
    map.put("index", index);
    if (width != InventoryDimensions.DEFAULT_WIDTH)
      map.put("width", width);
    return map;
  }

  public int distance(InventoryPosition other) {
    return other.getIndex() - getIndex();
  }

  /**
   * Returns a position relative to given {@code dimensions}.
   *
   * @param dimensions the relative dimensions
   * @return the new position, relative to {@code dimensions}
   */
  public InventoryPosition toRelative(InventoryDimensions dimensions) {
    final int width = dimensions.getWidth();
    return (this.width != width ? ofIndex(getIndex(), width) : this);
  }

  /**
   * Returns a new position that is set relative to {@code section}'s boundaries, such that a
   * returning zero position would represent the closest top left corner within a matrix of
   * {@code section} (so the beginning position).
   *
   * @param section the section to make this position relative to
   * @return the relative position
   * @throws IllegalArgumentException if this position lies outside {@code section}, such that
   *                                  {@code section.includes(this)} evaluates false.
   * @see InventorySection#includes(InventoryPosition)
   */
  public InventoryPosition toRelative(InventorySection section) {
    Preconditions.checkArgument(section.includes(this), "Section does not contain position");
    InventoryPosition begin = section.getBegin();
    int width = section.getDimensions().getWidth();
    return InventoryPosition.ofIndex((getIndex() - begin.getIndex())
        - (getRow() - begin.getRow()) * (getWidth() - width), width);
  }

  @CheckReturnValue
  public InventoryPosition shift(int indexOffset, int width) {
    return ofIndex(getIndex() + indexOffset, width);
  }

  @CheckReturnValue
  public InventoryPosition shift(int indexOffset) {
    return shift(indexOffset, width);
  }

  @CheckReturnValue
  public InventoryPosition add(int columnOffset, int rowOffset, int width) {
    return ofPoint(column + columnOffset, row + rowOffset, width);
  }

  @CheckReturnValue
  public InventoryPosition add(int columnOffset, int rowOffset) {
    return add(columnOffset, rowOffset, width);
  }

  @CheckReturnValue
  public InventoryPosition subtract(int columnOffset, int rowOffset, int width) {
    return ofPoint(column - columnOffset, row - rowOffset, width);
  }

  @CheckReturnValue
  public InventoryPosition subtract(int columnOffset, int rowOffset) {
    return subtract(columnOffset, rowOffset, width);
  }

  public boolean isEdge(int height) {
    return isEdge(width, height);
  }

  public boolean isEdge(int width, int height) {
    final int column = getColumn(width);
    final int row = getRow(width);
    return (column == 0 || column == width - 1) && (row == 0 || row == height - 1);
  }

  public int getColumn(int width) {
    if (width == this.width)
      return toColumn(index, width);
    return column;
  }

  public int getRow(int width) {
    if (width != this.width)
      return toRow(index, width);
    return row;
  }

  public int getWidth() {
    return width;
  }

  public int getIndex() {
    return index;
  }

  public int getColumn() {
    return column;
  }

  public boolean isAtColumnEnd() {
    return column == width - 1;
  }

  public int getRow() {
    return row;
  }

  @Override
  public int compareTo(InventoryPosition o) {
    return Integer.compare(getIndex(), o.getIndex());
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    InventoryPosition that = (InventoryPosition) object;
    return index == that.index && width == that.width;
  }

  @Override
  public int hashCode() {
    return Objects.hash(index, width);
  }

  @Override
  public String toString() {
    return "InventoryPosition{" +
        "index=" + index +
        ", width=" + width +
        ", column=" + column +
        ", row=" + row +
        '}';
  }

  static final class InventoryPositionInstancePool {

    private final int width;

    private final @Nullable SoftReference<InventoryPosition>[] pool;

    @SuppressWarnings("unchecked")
    public InventoryPositionInstancePool(int width, int maxRows) {
      Preconditions.checkArgument(width >= 0 && maxRows >= 0);
      this.pool = new SoftReference[maxRows * width];
      this.width = width;
    }

    public boolean isCoveredByPool(int index, int width) {
      return width == this.width && index >= 0 && index < pool.length;
    }

    public InventoryPosition atIndex(int index, int width) {
      if (!isCoveredByPool(index, width))
        return new InventoryPosition(index, width);
      @Nullable SoftReference<InventoryPosition> ref = pool[index];
      @Nullable InventoryPosition position = (ref != null ? ref.get() : null);
      if (position != null)
        return position;
      InventoryPosition newPosition = new InventoryPosition(index, width);
      pool[index] = new SoftReference<>(newPosition);
      return newPosition;
    }

    public InventoryPosition atIndex(int index) {
      return atIndex(index, width);
    }

    public InventoryPosition atPoint(int column, int row, int width) {
      return atIndex(InventoryPosition.toIndex(column, row, width), width);
    }

    public InventoryPosition atPoint(int column, int row) {
      return atPoint(column, row, width);
    }

    public int getWidth() {
      return width;
    }
  }

}
