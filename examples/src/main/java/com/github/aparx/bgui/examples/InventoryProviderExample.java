package com.github.aparx.bgui.examples;

import com.github.aparx.bgui.core.CustomInventory;
import com.github.aparx.bgui.core.CustomInventoryBuilder;
import com.github.aparx.bgui.core.content.InventoryContentView;
import com.github.aparx.bgui.core.dimension.InventoryDimensions;
import com.github.aparx.bgui.core.populators.InventoryStoragePopulator;
import com.github.aparx.bgui.core.provider.InventoryProvider;
import io.github.aparx.bommons.ticks.TickTimeUnit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-30 08:41
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public class InventoryProviderExample extends BukkitGuiExamplePlugin {

  @Override
  public void showInventory(Player player) {
    CustomInventoryBuilder.builder()
        .title("Custom inventory!")
        .populate(new TestProvider(InventoryDimensions.ofHeight(3)))
        .build(this)
        .show(player);
  }

  @DefaultQualifier(NonNull.class)
  public static class TestProvider implements InventoryProvider {

    private final InventoryDimensions dimensions;

    private @Nullable InventoryContentView redstones, emeralds;

    public TestProvider(InventoryDimensions dimensions) {
      this.dimensions = dimensions;
    }

    @Override
    public @NonNull InventoryContentView init() {
      this.redstones = InventoryStoragePopulator.create(dimensions)
          .fill(Material.REDSTONE)
          .getView();
      this.emeralds = InventoryStoragePopulator.create(dimensions.add(1))
          .fill(Material.EMERALD)
          .getView();
      return redstones;
    }

    @Override
    public @NonNull InventoryContentView update(CustomInventory accessor) {
      boolean useEmeralds = accessor.getUpdateTicker().getElapsed(TickTimeUnit.SECONDS) % 2 != 0;
      return Objects.requireNonNull((useEmeralds ? emeralds : redstones));
    }
  }
}
