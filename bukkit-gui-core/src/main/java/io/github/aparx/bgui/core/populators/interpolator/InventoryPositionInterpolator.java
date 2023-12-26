package io.github.aparx.bgui.core.populators.interpolator;

import com.google.common.collect.ImmutableList;
import io.github.aparx.bgui.core.dimension.InventoryPosition;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Iterator;
import java.util.List;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-26 03:27
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public abstract class InventoryPositionInterpolator implements Iterable<InventoryPosition> {

  private @Nullable List<InventoryPosition> collection;

  /**
   * Returns a new iterator, that iterates over all interpolated positions.
   * <p>The returning iterator must not access the cached collection.
   *
   * @return the new interpolating iterator
   */
  @Override
  public abstract Iterator<InventoryPosition> iterator();

  /**
   * Returns all positions of this interpolator.
   * This method collects all positions of this interpolator and caches it on first invocation,
   * such that this method always returns the same object ({@code collect() == collect()}).
   *
   * @return all positions, collected and cached
   * @see #iterator()
   */
  public List<InventoryPosition> collect() {
    if (collection != null)
      return collection;
    synchronized (this) {
      if (collection != null)
        return collection;
      ImmutableList.Builder<InventoryPosition> builder = ImmutableList.builder();
      for (InventoryPosition position : this) builder.add(position);
      return builder.build();
    }
  }
}
