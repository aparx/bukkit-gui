package io.github.aparx.bgui.core.dimension;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.*;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 13:00
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public class InventorySection implements Iterable<InventoryPosition> {

  /** Inclusive section beginning */
  private final InventoryPosition begin;
  /** Inclusive section ending */
  private final InventoryPosition end;

  /** All positions between {@code begin} and {@code end} (lazily interpolated) */
  private @Nullable Collection<InventoryPosition> interpolation;

  /** The dimensions of this section */
  private final InventoryDimensions dimensions;

  private InventorySection(InventoryPosition begin, InventoryPosition end) {
    this.begin = begin;
    this.end = end;
    this.dimensions = InventoryDimensions.ofSize(size(), 1 + end.getColumn() - begin.getColumn());
  }

  /**
   * Allocates a new {@code InventorySection} instance that encompasses all positions in-between
   * {@code begin} (inclusive) and {@code end} (inclusive).
   *
   * @param begin the beginning position of the section (inclusive)
   * @param end   the ending position of the section (inclusive)
   * @return the newly allocated section
   * @throws NullPointerException     if {@code begin} and/or {@code end} is null
   * @throws IllegalArgumentException if the width in {@code begin} and {@code end} are unequal;
   *                                  or if {@code begin} is larger (as in index is greater) than
   *                                  {@code end}, such that {@code begin.compareTo(end)}
   *                                  evaluates greater than zero.
   */
  public static InventorySection of(InventoryPosition begin, InventoryPosition end) {
    Preconditions.checkNotNull(begin, "Begin must not be null");
    Preconditions.checkNotNull(end, "End must not be null");
    Preconditions.checkArgument(begin.getWidth() == end.getWidth(), "Mismatching width");
    Preconditions.checkArgument(begin.compareTo(end) <= 0, "Beginning position is larger than end");
    return new InventorySection(InventoryPosition.getMin(begin, end),
        InventoryPosition.getMax(begin, end));
  }

  public static InventorySection of(int fromX, int fromY, int toX, int toY) {
    return of(InventoryPosition.ofPoint(fromX, fromY), InventoryPosition.ofPoint(toX, toY));
  }

  public static InventorySection of(@NonNegative int fromIndex, @NonNegative int toIndex) {
    return of(InventoryPosition.ofIndex(fromIndex), InventoryPosition.ofIndex(toIndex));
  }

  public static InventorySection of(InventoryDimensions dimensions) {
    return of(InventoryPosition.ofZero(),
        InventoryPosition.ofIndex(dimensions.size() - 1, dimensions.getWidth()));
  }

  public int size() {
    return (1 + end.getRow() - begin.getRow()) * (1 + end.getColumn() - begin.getColumn());
  }

  public InventorySection subsection(int fromRelativeIndex, int toRelativeIndex) {
    Preconditions.checkArgument(fromRelativeIndex >= 0, "Index must not be negative");
    Preconditions.checkArgument(toRelativeIndex >= fromRelativeIndex,
        "toRelativeIndex < fromRelativeIndex");
    int diff; // differences measured in indices
    InventoryPosition begin = this.begin.shift(fromRelativeIndex);
    diff = this.end.getIndex() - begin.getIndex();
    if (diff < 0) throw new IndexOutOfBoundsException("begin: " + diff);
    InventoryPosition end = this.begin.shift(toRelativeIndex);
    diff = this.end.getIndex() - end.getIndex();
    if (diff < 0) throw new IndexOutOfBoundsException("end: " + diff);
    return of(begin, end);
  }

  public InventorySection subsection(int fromRelativeIndex) {
    Preconditions.checkArgument(fromRelativeIndex >= 0, "Index must not be negative");
    return subsection(fromRelativeIndex, size());
  }

  public InventorySection shrink(int column, int row) {
    return of(begin.add(column, row), end.subtract(column, row));
  }

  public InventorySection expand(int column, int row) {
    return of(begin.subtract(column, row), end.add(column, row));
  }

  public boolean includes(int index) {
    return includes(InventoryPosition.ofIndex(index, begin.getWidth()));
  }

  public boolean includes(InventoryPosition position) {
    return position.getRow() >= begin.getRow() && position.getRow() <= end.getRow()
        && position.getColumn() >= begin.getColumn() && position.getColumn() <= end.getColumn();
  }

  public boolean includes(InventorySection section) {
    if (section.size() > size())
      return false;
    return includes(section.getBegin()) && includes(section.getEnd());
  }

  // +------------------ Position utilities ------------------+

  public InventoryPosition rowCenter() {
    return InventoryPosition.ofPoint(0, begin.getRow() + (dimensions.getHeight()) / 2);
  }

  public InventoryPosition columnCenter() {
    return InventoryPosition.ofPoint(begin.getColumn() + (dimensions.getWidth()) / 2, 0);
  }

  public InventoryPosition center() {
    return InventoryPosition.ofPoint(
        begin.getColumn() + (dimensions.getWidth()) / 2,
        begin.getRow() + (dimensions.getHeight()) / 2);
  }

  // +------------------ Iterator and getters ------------------+

  /**
   * Returns an immutable collection of all positions within this section.
   * <p>The returned value is memoized, such that the allocation is only done once for this section.
   *
   * @return the positions (inclusively) in-between this {@code begin} and {@code end} position.
   * @see #getBegin()
   * @see #getEnd()
   */
  public Collection<InventoryPosition> getPositions() {
    if (interpolation != null)
      return interpolation;
    synchronized (this) {
      if (interpolation != null)
        return interpolation;
      // interpolation from `begin` to (inclusively) `end`
      // TODO map each position to its actual index
      final int length = size();
      ImmutableList.Builder<InventoryPosition> builder =
          ImmutableList.builderWithExpectedSize(1 + length);
      int beginColumn = begin.getColumn();
      int xLength = end.getColumn() - beginColumn;
      int yLength = end.getRow() - begin.getRow();
      for (int x = 0; x <= xLength; ++x)
        for (int y = 0; y <= yLength; ++y)
          builder.add(begin.add(x, y, beginColumn + 1 + xLength));
      this.interpolation = builder.build();
      return this.interpolation;
    }
  }

  @Override
  public Iterator<InventoryPosition> iterator() {
    return getPositions().iterator();
  }

  /**
   * Returns the inclusive beginning position of this section.
   *
   * @return the (inclusive) begin of this section
   */
  public InventoryPosition getBegin() {
    return begin;
  }

  /**
   * Returns the inclusive ending position of this section.
   *
   * @return the (inclusive) end of this section
   */
  public InventoryPosition getEnd() {
    return end;
  }

  /**
   * Returns the dimensions of this section.
   *
   * @return the dimensions of this section
   */
  public InventoryDimensions getDimensions() {
    return dimensions;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    InventorySection section = (InventorySection) object;
    return Objects.equals(begin, section.begin) && Objects.equals(end, section.end);
  }

  @Override
  public int hashCode() {
    return Objects.hash(begin, end);
  }

  @Override
  public String toString() {
    return "InventorySection{" +
        "begin=" + begin +
        ", end=" + end +
        '}';
  }
}
