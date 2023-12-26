package io.github.aparx.bgui.core.populators.interpolator;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import io.github.aparx.bgui.core.dimension.InventoryPosition;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Iterator;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-26 03:26
 * @see
 * <a href="https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm#Algorithm_for_integer_arithmetic">
 * https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm#Algorithm_for_integer_arithmetic</a>
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public class LineInterpolator extends InventoryPositionInterpolator {

  private final InventoryPosition start, stop;

  public LineInterpolator(InventoryPosition start, InventoryPosition stop) {
    Preconditions.checkNotNull(start, "Start must not be null");
    Preconditions.checkNotNull(stop, "Stop must not be null");
    Preconditions.checkArgument(start.compareTo(stop) <= 0, "start > stop");
    this.start = start;

    if (stop.getIndex() != start.getIndex())
      this.stop = stop.add((!stop.isAtColumnEnd() && !start.isAtColumnEnd()
          ? Integer.compare(stop.getColumn(), start.getColumn())
          : 0), Integer.compare(stop.getRow(), start.getRow()));
    else
      this.stop = (!stop.isAtColumnEnd() ? stop.add(1, 0) : stop);
  }

  public InventoryPosition getStart() {
    return start;
  }

  public InventoryPosition getStop() {
    return stop;
  }

  @Override
  public Iterator<InventoryPosition> iterator() {
    return new LineIterator();
  }

  // error correction for Bresenham's line algorithm
  private final class LineIterator extends AbstractIterator<InventoryPosition> {

    int x0 = start.getColumn(), y0 = start.getRow();
    int x1 = stop.getColumn(), y1 = stop.getRow();
    int dx = Math.abs(x1 - x0), dy = -Math.abs(y1 - y0);
    int sx = x0 < x1 ? 1 : -1;
    int sy = y0 < y1 ? 1 : -1;
    int error = dx + dy;

    @Override
    protected InventoryPosition computeNext() {
      InventoryPosition position = InventoryPosition.ofPoint(x0, y0);
      if (x0 == x1 && y0 == y1) {
        endOfData();
        return position;
      }
      int e2 = 2 * error;
      if (e2 >= dy) {
        if (x0 == x1) {
          endOfData();
          return position;
        }
        error = error + dy;
        x0 = x0 + sx;
      }
      if (e2 <= dx) {
        if (y0 == y1) {
          endOfData();
          return position;
        }
        error = error + dx;
        y0 = y0 + sy;
      }
      return position;
    }
  }

}
