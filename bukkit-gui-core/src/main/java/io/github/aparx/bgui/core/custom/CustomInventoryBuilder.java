package io.github.aparx.bgui.core.custom;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import io.github.aparx.bgui.core.custom.content.InventoryStorageLayer;
import io.github.aparx.bgui.core.custom.populators.InventoryDynamicPagePopulator;
import io.github.aparx.bgui.core.custom.populators.InventoryPagePopulator;
import io.github.aparx.bgui.core.custom.populators.InventoryStoragePopulator;
import io.github.aparx.bgui.core.InventoryDimensions;
import io.github.aparx.bommons.ticks.TickDuration;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.function.BiConsumer;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-25 04:53
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public final class CustomInventoryBuilder {

  private @Nullable String title;
  private @Nullable TickDuration updateInterval;
  private @Nullable InventoryContentView content;
  private @Nullable BiConsumer<CustomInventory, ? extends InventoryContentView> populator;

  private CustomInventoryBuilder() {}

  public static CustomInventoryBuilder builder() {
    return new CustomInventoryBuilder();
  }

  @CanIgnoreReturnValue
  public CustomInventoryBuilder title(@Nullable String title) {
    this.title = title;
    return this;
  }

  public @Nullable String getTitle() {
    return title;
  }

  @CanIgnoreReturnValue
  public CustomInventoryBuilder interval(@Nullable TickDuration updateInterval) {
    this.updateInterval = updateInterval;
    return this;
  }

  public @Nullable TickDuration getUpdateInterval() {
    return updateInterval;
  }

  /**
   * @see #populate(InventoryContentView, BiConsumer)
   * @see InventoryStoragePopulator
   * @see InventoryPagePopulator
   * @see InventoryDynamicPagePopulator
   */
  @CanIgnoreReturnValue
  public CustomInventoryBuilder populate(InventoryContentView content) {
    return populate(content, null);
  }

  /**
   * Populates the built inventory with given {@code content}.
   * <p>The provided {@code populator} is called on build to fill the content.
   * <p>If {@code content} is an instance of {@link CopyableInventoryContentView}, the inventory
   * will be copied and then populated through {@code populator} (if not-null).
   * <p>Following is the order for default builds of custom inventories:
   * <ol>
   *   <li>Allocation of {@link CustomInventory}</li>
   *   <li>Content copy (if instance of {@link CopyableInventoryContentView})</li>
   *   <li>Content population (if {@code populator} is not-null)</li>
   *   <li>Content push (through {@link CustomInventory#updateContent(InventoryContentView)})</li>
   * </ol>
   *
   * @param content   the new content of the inventory
   * @param populator the populator, called upon creation of the inventory (optional)
   * @param <T>       the type of content view
   * @return this builder instance
   * @see InventoryContentFactory
   * @see CustomInventory
   * @see InventoryContentView
   * @see CopyableInventoryContentView
   * @see CustomInventory#updateContent(InventoryContentView)
   */
  @CanIgnoreReturnValue
  public <T extends InventoryContentView> CustomInventoryBuilder populate(
      T content, @Nullable BiConsumer<CustomInventory, T> populator) {
    Preconditions.checkNotNull(content, "Content must not be null");
    this.content = content;
    this.populator = populator;
    return this;
  }

  @CanIgnoreReturnValue
  public CustomInventoryBuilder populate(
      InventoryDimensions dimensions,
      @Nullable BiConsumer<CustomInventory, InventoryStorageLayer> populator) {
    return populate(InventoryContentFactory.storageLayer(dimensions), populator);
  }

  public @Nullable InventoryContentView getContent() {
    return content;
  }

  public @Nullable BiConsumer<CustomInventory, ? extends InventoryContentView> getPopulator() {
    return populator;
  }

  @CheckReturnValue
  @SuppressWarnings({"rawtypes", "unchecked"})
  public CustomInventory build(Plugin plugin) {
    CustomInventory inventory = (updateInterval != null
        ? new CustomInventory(plugin, updateInterval, title)
        : new CustomInventory(plugin, title));
    @Nullable InventoryContentView content = this.content;
    Preconditions.checkNotNull(content, "Content should be defined");
    if (content instanceof CopyableInventoryContentView)
      content = ((CopyableInventoryContentView) content).copy();
    if (populator != null)
      ((BiConsumer) populator).accept(inventory, content);
    inventory.updateContent(content);
    return inventory;
  }

}
