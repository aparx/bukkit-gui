package io.github.aparx.bgui.examples;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-27 00:23
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public abstract class BukkitGuiExamplePlugin extends JavaPlugin implements Listener {

  public abstract void showInventory(Player player);

  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  void onInteract(PlayerInteractEvent event) {
    showInventory(event.getPlayer());
  }

}
