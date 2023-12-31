package com.github.aparx.bgui.core;

import com.github.aparx.bgui.core.content.CopyableInventoryContentView;
import com.github.aparx.bgui.core.content.InventoryContentFactory;
import com.github.aparx.bgui.core.content.InventoryContentView;
import com.github.aparx.bgui.core.dimension.InventoryDimensions;
import com.github.aparx.bgui.core.provider.InventoryProvider;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.github.aparx.bgui.core.content.InventoryStorageLayer;
import com.github.aparx.bgui.core.populators.InventoryDynamicPagePopulator;
import com.github.aparx.bgui.core.populators.InventoryPagePopulator;
import com.github.aparx.bgui.core.populators.InventoryStoragePopulator;
import com.github.aparx.bommons.ticks.TickDuration;
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
  private @Nullable InventoryProvider provider;
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

  /**
   * Updates the interval with which this inventory updates. If null is supplied, a default
   * interval is being used at build.
   *
   * @param updateInterval the interval to be used
   * @return this builder
   * @see #updateInterval(TickDuration)
   * @deprecated This method was replaced in favor of {@code updateInterval}, which
   * works equivalently, with the difference being the more fitting name. This method has not yet
   * been removed due to backwards compatibility, but will (!) be removed in near-future.
   */
  @Deprecated(forRemoval = true)
  @CanIgnoreReturnValue
  public CustomInventoryBuilder interval(@Nullable TickDuration updateInterval) {
    this.updateInterval = updateInterval;
    return this;
  }

  /**
   * Updates the interval with which this inventory updates. If null is supplied, a default
   * interval is being used at build.
   *
   * @param updateInterval the target new interval
   * @return this builder
   */
  @CanIgnoreReturnValue
  public CustomInventoryBuilder updateInterval(@Nullable TickDuration updateInterval) {
    this.updateInterval = updateInterval;
    return this;
  }

  public @Nullable TickDuration getUpdateInterval() {
    return updateInterval;
  }

  /** @since 2.0 */
  @CanIgnoreReturnValue
  public CustomInventoryBuilder populate(InventoryProvider provider) {
    this.provider = provider;
    this.populator = null;
    return this;
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
   *   <li>Content push (through {@link CustomInventory#update(InventoryContentView)})</li>
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
   * @see CustomInventory#update(InventoryContentView)
   */
  @CanIgnoreReturnValue
  public <T extends InventoryContentView> CustomInventoryBuilder populate(
      T content, @Nullable BiConsumer<CustomInventory, T> populator) {
    Preconditions.checkNotNull(content, "Content must not be null");
    this.provider = InventoryProvider.of(content);
    this.populator = populator;
    return this;
  }

  /**
   * @see #populate(InventoryContentView, BiConsumer)
   * @see InventoryContentFactory#storageLayer(InventoryDimensions)
   */
  @CanIgnoreReturnValue
  public CustomInventoryBuilder populate(
      InventoryDimensions dimensions,
      @Nullable BiConsumer<CustomInventory, InventoryStorageLayer> populator) {
    return populate(InventoryContentFactory.storageLayer(dimensions), populator);
  }

  public @Nullable InventoryProvider getProvider() {
    return provider;
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
    Preconditions.checkNotNull(provider, "No content provider is apparent");
    if (provider instanceof InventoryProvider.StaticInventoryProvider) {
      InventoryContentView content = provider.init();
      Preconditions.checkNotNull(content, "Provider returned null as content at init");
      if (content instanceof CopyableInventoryContentView)
        content = ((CopyableInventoryContentView) content).copy();
      if (populator != null)
        ((BiConsumer) populator).accept(inventory, content);
      inventory.update(content);
    } else {
      inventory.update(provider);
    }
    return inventory;
  }

}
