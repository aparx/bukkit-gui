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
public class InventorySection implements Iterable<InventoryPosition>, InventorySizable {

  public static final InventorySection DEFAULT_SECTION =
      InventorySection.of(InventoryDimensions.DEFAULT_DIMENSIONS);

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
    return of(dimensions, dimensions.getWidth());
  }

  public static InventorySection of(InventoryDimensions dimensions, int width) {
    return of(InventoryPosition.ofZero(width), InventoryPosition.ofIndex(dimensions.size() - 1,
        width));
  }

  /**
   * Returns a to this section relative position based off of given parent position.
   * <p>Assuming this section is equal to {@code [1, 1]} to {@code [3, 3]}, following examples
   * would apply:
   * <ul>
   *   <li>{@code toRelative(InventoryPosition.ofPoint(1, 1)) ::= [0, 0]}</li>
   *   <li>{@code toRelative(InventoryPosition.ofPoint(1, 2)) ::= [0, 1]}</li>
   *   <li>{@code toRelative(InventoryPosition.ofPoint(3, 2)) ::= [2, 1]}</li>
   *   <li>{@code toRelative(InventoryPosition.ofPoint(4, 2)) ::= error}</li>
   * </ul>
   * Assuming, the example section is a subsection of {@code [0, 0]} to {@code [4, 4]} the example
   * section would be located as highlighted in this matrix:
   * <code><pre>
   *   parent
   *   [ X ][ X ][ X ][ X ][ X ]
   *   [ X ][0,0][1,0][2,0][ X ]
   *   [ X ][0,1][1,1][2,1][ X ]
   *   [ X ][0,2][1,2][2,2][ X ]
   *   [ X ][ X ][ X ][ X ][ X ]
   * </pre></code>
   * The {@code X} represents the parent section. Within this section are coordinates,
   * representing all possible <strong>relative</strong> positions for our example section.
   * Whereas (parent-)absolute position {@code [0, 0]} would equal to the most top-left {@code X}
   * position in the parent section, since input {@code position} is relative to its parent, by
   * default being the root inventory of: {@code [0, 0]} to {@code [9, n]}.
   *
   * @return given absolute position as a relative to this section
   * @throws IllegalArgumentException if {@code position} (absolute) is not within this section
   */
  public InventoryPosition toRelative(InventoryPosition position) {
    Preconditions.checkArgument(includes(position, begin.getWidth()),
        "Section does not contain position");
    return InventoryPosition.ofIndex((
        position.getIndex() - begin.getIndex())
        - (position.getRow(begin.getWidth()) - begin.getRow())
        * (position.getWidth() - dimensions.getWidth()
    ), dimensions.getWidth());
  }

  public InventorySection toRelative(InventorySection parent) {
    return of(parent.toRelative(begin), parent.toRelative(end));
  }

  /**
   * Returns the {@code position} as relative position of this section to an absolute position.
   * <p>This method is basically the opposite of {@link #toRelative(InventoryPosition)}.
   *
   * @param position the relative position to transform to an absolute position
   * @return given (to this section) relative position as an absolute position
   * @see #toRelative(InventoryPosition)
   */
  public InventoryPosition toAbsolute(InventoryPosition position) {
    return begin.add(position.getColumn(), position.getRow());
  }

  /**
   * Returns this section relative to {@code parent}.
   *
   * @param parent the parent to make this section relative to
   * @return this section, but relative to {@code parent}
   */
  public InventorySection toAbsolute(InventorySection parent) {
    Preconditions.checkArgument(size() <= parent.size(), "Section is too large for parent");
    return of(parent.toAbsolute(begin), parent.toAbsolute(end));
  }

  /** Returns this section relative to the default inventory section. */
  public InventorySection toAbsolute() {
    return toAbsolute(InventorySection.DEFAULT_SECTION);
  }

  public int size() {
    return (1 + end.getRow() - begin.getRow()) * (1 + end.getColumn() - begin.getColumn());
  }

  public InventorySection subsection(int fromInclusiveIndex, int toInclusiveIndex) {
    Preconditions.checkArgument(fromInclusiveIndex >= 0, "Index must not be negative");
    Preconditions.checkArgument(toInclusiveIndex >= fromInclusiveIndex,
        "toRelativeIndex < fromRelativeIndex");
    Preconditions.checkArgument(toInclusiveIndex - fromInclusiveIndex < size(),
        "Subsection is too large");
    return of(InventoryPosition.ofIndex(begin.getIndex() + fromInclusiveIndex, begin.getWidth()),
        InventoryPosition.ofIndex(begin.getIndex() + toInclusiveIndex, end.getWidth()));
  }

  public InventorySection subsection(int fromInclusiveIndex) {
    Preconditions.checkArgument(fromInclusiveIndex >= 0, "Index must not be negative");
    return subsection(fromInclusiveIndex, size());
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

  public boolean includes(InventoryPosition position, int width) {
    return position.getRow(width) >= begin.getRow(width)
        && position.getRow(width) <= end.getRow(width)
        && position.getColumn(width) >= begin.getColumn(width)
        && position.getColumn() <= end.getColumn(width);
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
  @Override
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
