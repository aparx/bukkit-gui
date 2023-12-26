package io.github.aparx.bgui;

import com.google.common.base.Preconditions;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Map;
import java.util.function.IntConsumer;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 12:55
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public final class InventoryDimensions implements ConfigurationSerializable {

  public static final InventoryDimensions DEFAULT_DIMENSIONS = InventoryDimensions.ofHeight(4);

  public static final int DEFAULT_WIDTH = 9;

  /** The width (or column-length) of the inventory */
  private final int width;

  /** The width (or row-length) of the inventory */
  private final int height;

  private InventoryDimensions(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public static InventoryDimensions of(@NonNegative int width, @NonNegative int height) {
    Preconditions.checkArgument(width >= 1, "Width must at least be one");
    Preconditions.checkArgument(height >= 1, "Height must at least be one");
    return new InventoryDimensions(width, height);
  }

  public static InventoryDimensions ofHeight(@NonNegative int height) {
    Preconditions.checkArgument(height >= 1, "Height must at least be one");
    return new InventoryDimensions(DEFAULT_WIDTH, height);
  }

  public static InventoryDimensions ofSize(int size, int width) {
    Preconditions.checkArgument(size >= 1, "Size must at least be one");
    return of(width, Math.max(InventoryPosition.toRow(size, width), 1));
  }

  public static InventoryDimensions deserialize(Map<?, ?> args) {
    return of(NumberConversions.toInt(args.get("width")),
        NumberConversions.toInt(args.get("height")));
  }

  @Override
  public Map<String, Object> serialize() {
    return Map.of("width", width, "height", height);
  }

  /**
   * Returns the capacity from these dimensions.
   *
   * @return the total capacity of row <strong>and</strong> column elements
   */
  public int size() {
    return width * height;
  }

  public boolean includes(int index) {
    return index >= 0 && index < size();
  }

  public boolean includes(InventoryPosition position) {
    return includes(position.getIndex());
  }

  /**
   * Returns the width dimension (being the column-length).
   *
   * @return the maximum possible amount of column elements
   */
  public int getWidth() {
    return width;
  }

  /**
   * Returns the height dimension (being the row-length).
   *
   * @return the maximum possible amount of row elements
   */
  public int getHeight() {
    return height;
  }

  public void forEach(IntConsumer consumer) {
    for (int i = 0, len = size(); i < len; ++i)
      consumer.accept(i);
  }

  @Override
  public String toString() {
    return "InventoryDimensions{" +
        "width=" + width +
        ", height=" + height +
        '}';
  }

}
