# bukkit-gui
The most sophisticated open-source inventory library for Bukkit.

## Example
A quite basic, but yet advanced example is the following:
```java
    CustomInventoryBuilder.builder()
        .title("Teleport inventory")
        // we update the inventory every second
        .interval(TickDuration.ofOne(TickTimeUnit.SECONDS))
        .populate(InventoryStoragePopulator.create()
            .fill(Material.GRAY_STAINED_GLASS_PANE)
            .outline(Material.RED_STAINED_GLASS_PANE)
            .set(InventoryPosition.ofPoint(4, 1), Material.DIAMOND, (item, event) -> {
              event.getWhoClicked().sendMessage(ChatColor.AQUA + "You clicked the diamond!");
            })
            .getView())
        .build(plugin)
        .show(player);
```
