package com.github.aparx.bgui.core;

import com.github.aparx.bgui.core.content.InventoryContentView;
import com.github.aparx.bgui.core.dimension.InventoryDimensions;
import com.github.aparx.bgui.core.provider.InventoryProvider;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import io.github.aparx.bommons.core.WeakHashSet;
import com.github.aparx.bgui.core.item.InventoryItem;
import com.github.aparx.bgui.core.item.InventoryItemAccessor;
import io.github.aparx.bommons.ticks.TickDuration;
import io.github.aparx.bommons.ticks.ticker.DefaultTicker;
import io.github.aparx.bommons.ticks.ticker.Ticker;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 15:25
 * @since 1.0
 */
// a CustomInventory is used to display, render and update inventories (menus)
@DefaultQualifier(NonNull.class)
public class CustomInventory implements InventoryItemAccessor {

  private final transient Object lock = new Object();

  private final Plugin plugin;
  private final TickDuration updateInterval;
  /** Similar to Bukkit's viewer list, this is for internal registry only */
  private final WeakHashSet<Player> viewers = new WeakHashSet<>();
  private final Ticker updateTicker;

  /** Current update task running for all viewers */
  protected @Nullable BukkitTask task;
  protected @Nullable Inventory inventory;

  private @Nullable InventoryContentView content;
  private @Nullable InventoryProvider provider;
  private @Nullable String title;

  protected final CustomInventoryListener listener = new CustomInventoryListener(this);

  public CustomInventory(Plugin plugin) {
    this(plugin, null);
  }

  public CustomInventory(Plugin plugin, @Nullable String title) {
    this(plugin, TickDuration.ofNil(), title);
  }

  public CustomInventory(Plugin plugin, TickDuration updateInterval, @Nullable String title) {
    Preconditions.checkNotNull(plugin, "Plugin must not be null");
    Preconditions.checkNotNull(updateInterval, "Interval must not be null");
    this.plugin = plugin;
    this.updateInterval = updateInterval;
    this.updateTicker = new DefaultTicker(updateInterval);
    this.title = title;
  }

  /** Returns the current internal content, provided by the {@code InventoryProvider} */
  public final @Nullable InventoryContentView getContent() {
    return content;
  }

  public final @Nullable InventoryProvider getProvider() {
    return provider;
  }

  public final @Nullable String getTitle() {
    return title;
  }

  public final Plugin getPlugin() {
    return plugin;
  }

  public final void update() {
    if (render(updateTicker.tick() > 1))
      updateTicker.reset();
  }

  public void update(@Nullable InventoryProvider provider, @Nullable String title) {
    this.title = title;
    if (provider != null && !provider.equals(this.provider)) {
      this.provider = provider;
      InventoryContentView initialContent = provider.init();
      Preconditions.checkNotNull(initialContent, "Provider returned null as content at init");
      this.content = initialContent;
    }
    update();
  }

  public void update(@Nullable InventoryProvider provider) {
    update(provider, this.title);
  }

  public void update(InventoryContentView content, @Nullable String title) {
    update(InventoryProvider.of(content), title);
  }

  public void update(InventoryContentView content) {
    update(InventoryProvider.of(content), title);
  }

  public void update(@Nullable String title) {
    Preconditions.checkNotNull(content, "Content must not be null");
    update(InventoryProvider.of(content), title);
  }

  @CanIgnoreReturnValue
  public boolean show(Player... players) {
    if (ArrayUtils.isNotEmpty(players))
      return show(Arrays.asList(players));
    return false;
  }

  @CanIgnoreReturnValue
  public boolean show(Iterable<? extends Player> viewers) {
    Preconditions.checkNotNull(viewers, "Viewers must not be null");
    if (inventory == null) createInventory(getTitle(), true);
    int viewerCount = 0;
    boolean success = false;
    for (Player viewer : viewers) {
      Preconditions.checkNotNull(viewer, "Viewer is null");
      success |= this.viewers.add(viewer);
      viewer.openInventory(inventory);
      ++viewerCount;
    }
    if (viewerCount != 0)
      start();
    return success;
  }

  @CanIgnoreReturnValue
  public boolean show(Player viewer) {
    Preconditions.checkNotNull(viewer, "Viewer must not be null");
    return show(List.of(viewer));
  }

  @CanIgnoreReturnValue
  public boolean close(Player viewer) {
    if (viewer.getOpenInventory().getTopInventory().equals(inventory))
      viewer.closeInventory();
    synchronized (lock) {
      if (!viewers.remove(viewer))
        return false;
      revalidateTask();
      return true;
    }
  }

  public boolean isViewer(Player player) {
    synchronized (lock) {
      return viewers.contains(player);
    }
  }

  /**
   * Renders this inventory and returns true if the update task is stopped
   *
   * @param checkForViewers if true, checks for the number of viewers and returns true
   *                        (implies stop) if there is no viewer is viewing this inventory anymore
   * @return true if the internal updating task is stopped, false if not
   */
  @CanIgnoreReturnValue
  public boolean render(boolean checkForViewers) {
    if (revalidateTask()) return true;
    if (provider == null) return false;
    if (content != null && inventory != null)
      content.getArea().forEach((position) -> {
        @Nullable InventoryItem item = content.get(this, position);
        inventory.setItem(position.getIndex(), (item != null ? item.get(this) : null));
      });
    if (checkForViewers) {
      List<Player> removeViewers = new ArrayList<>(0);
      viewers.forEach((viewer) -> {
        Inventory topInventory = viewer.getOpenInventory().getTopInventory();
        if (!Objects.equals(topInventory, inventory))
          removeViewers.add(viewer);
      });
      removeViewers.forEach(viewers::remove);
      if (viewers.isEmpty())
        return stop();
    }
    InventoryContentView newContent = provider.update(this);
    Preconditions.checkNotNull(newContent, "Provider return null as content at update");
    if (reassignContent(newContent, title))
      createInventory(title, true);
    return false;
  }

  /**
   * Reassigns the internal content and title property and returns a boolean that defines whether a
   * re-render or re-creation of the inventory is necessary.
   *
   * @param content the new content to reassign to
   * @param title   the title of the inventory
   * @return true, if the content or title is different, which requires an inventory recreation.
   * False, if the content is similar to the one before, and a re-render is necessary.
   */
  @CheckReturnValue
  protected boolean reassignContent(InventoryContentView content, @Nullable String title) {
    Preconditions.checkNotNull(content, "Content must not be null");
    Preconditions.checkArgument(
        content.getArea().getBegin().getIndex() == 0,
        "Inventory content at root must begin at [0, 0]");
    @Nullable InventoryDimensions currentDimensions = (
        this.content != null ? this.content.getDimensions() : null);
    this.content = content;
    return !Objects.equals(this.title, (this.title = title)) ||
        !Objects.equals(currentDimensions, content.getDimensions());
  }

  @CanIgnoreReturnValue
  protected boolean start() {
    if (task != null)
      return false;
    synchronized (lock) {
      if (task != null)
        return false;
      // render the inventory with viewer check
      this.task = Bukkit.getScheduler().runTaskTimer(plugin, () -> this.update(),
          updateInterval.toTicks(), updateInterval.toTicks());
      Bukkit.getPluginManager().registerEvents(listener, plugin);
      return true;
    }
  }

  @CanIgnoreReturnValue
  protected boolean stop() {
    if (task == null)
      return false;
    synchronized (lock) {
      if (task == null)
        return false;
      task.cancel();
      task = null;
      viewers.clear();
      updateTicker.reset();
      HandlerList.unregisterAll(listener);
      return true;
    }
  }

  @CanIgnoreReturnValue
  private boolean revalidateTask() {
    if (viewers.isEmpty() || inventory == null)
      return stop();
    return false;
  }

  private void createInventory(@Nullable String title, boolean render) {
    Preconditions.checkNotNull(content, "Content is undefined");
    this.inventory = (title != null
        ? Bukkit.createInventory(null, content.getDimensions().size(), title)
        : Bukkit.createInventory(null, content.getDimensions().size()));
    if (render) render(false);
    viewers.forEach((viewer) -> viewer.openInventory(inventory));
  }

  /**
   * @deprecated Usage of {@code getInventory} is not advised, since showing the inventory
   * manually to other players using the returning inventory will not trigger the internal
   * scheduler or listener to be created and updated. The usage of the "raw" inventory for other
   * reasons, should be totally valid.
   */
  @Deprecated
  @SuppressWarnings("DeprecatedIsStillUsed")
  public @Nullable Inventory getInventory() {
    return inventory;
  }

  public WeakHashSet<Player> getViewers() {
    return viewers;
  }

  public TickDuration getUpdateInterval() {
    return updateInterval;
  }

  @Override
  public Ticker getUpdateTicker() {
    return updateTicker;
  }

}
