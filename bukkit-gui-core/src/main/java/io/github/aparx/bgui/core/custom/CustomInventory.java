package io.github.aparx.bgui.core.custom;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.aparx.bommons.core.WeakHashSet;
import io.github.aparx.bgui.core.InventoryDimensions;
import io.github.aparx.bgui.core.item.InventoryItem;
import io.github.aparx.bgui.core.item.InventoryItemAccessor;
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
  protected @Nullable InventoryContentView content;
  protected @Nullable Inventory inventory;

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

  public final @Nullable InventoryContentView getContent() {
    return content;
  }

  public final @Nullable String getTitle() {
    return title;
  }

  public final Plugin getPlugin() {
    return plugin;
  }

  public void updateContent(InventoryContentView content, @Nullable String title) {
    Preconditions.checkNotNull(content, "Content must not be null");
    Preconditions.checkArgument(
        content.getArea().getBegin().getIndex() == 0,
        "Root content must have no offset: begin must be (0, 0)");
    @Nullable InventoryDimensions currentDimensions = (
        this.content != null ? this.content.getDimensions() : null);
    this.content = content;
    if (Objects.equals(title, this.title) &&
        Objects.equals(currentDimensions, content.getDimensions()))
      renderInventory(false); // force re-render
    else createInventory(title);
    this.title = title;
  }

  public void updateContent(InventoryContentView content) {
    updateContent(content, this.title);
  }

  public void updateTitle(@Nullable String title) {
    Preconditions.checkNotNull(content, "Content must not be null");
    updateContent(content, title);
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
    if (inventory == null) createInventory(getTitle());
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

  public void updateInventory() {
    if (!renderInventory(updateTicker.getElapsed() > 1))
      updateTicker.tick(); // increase ticker
    else
      updateTicker.reset();
  }

  /** Renders this inventory and returns true if the update task is stopped */
  @CanIgnoreReturnValue
  public boolean renderInventory(boolean checkForViewers) {
    if (revalidateTask()) return true;
    if (content == null || inventory == null) return false;
    content.getArea().forEach((position) -> {
      @Nullable InventoryItem item = content.get(this, position);
      inventory.setItem(position.getIndex(), (item != null ? item.get(this) : null));
    });
    if (!checkForViewers)
      return false;
    List<Player> removeViewers = new ArrayList<>(0);
    viewers.forEach((viewer) -> {
      Inventory topInventory = viewer.getOpenInventory().getTopInventory();
      if (!Objects.equals(topInventory, inventory))
        removeViewers.add(viewer);
    });
    removeViewers.forEach(viewers::remove);
    return !removeViewers.isEmpty() && revalidateTask();
  }

  @CanIgnoreReturnValue
  protected boolean start() {
    if (task != null)
      return false;
    synchronized (lock) {
      if (task != null)
        return false;
      // render the inventory with viewer check
      this.task = Bukkit.getScheduler().runTaskTimer(plugin, this::updateInventory,
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

  private void createInventory(@Nullable String title) {
    Preconditions.checkNotNull(content, "Content is undefined");
    updateTicker.reset(); // reset first, to avoid automatic `checkForViewers`
    this.inventory = (title != null
        ? Bukkit.createInventory(null, content.getDimensions().size(), title)
        : Bukkit.createInventory(null, content.getDimensions().size()));
    renderInventory(false); // force re-render
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
