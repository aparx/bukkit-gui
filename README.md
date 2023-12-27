# bukkit-gui
The most sophisticated open-source inventory library for Bukkit.

# Ideology
The idea behind bukkit-gui is to be able to easily create very complex but also very simple inventories, that act as a user interface, with which a player or group of players can easily interact with. Inventories are updated in an interval automatically, or when players interact with items that are clickable.<br/><br/>
In bukkit-gui an inventory is represented by a class that wraps around a mutable content attribute, which can be updated at any time. This content is an abstract class, which can represent virtually anything happening on a 2D plane. There are very useful default content views, that enable virtually anything: from layers, to pagination, to simple pages evolving around filling material.

# Basic example
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

### Dynamic and static pagination
Pagination can be a head-scratcher sometimes, but with this library it is easily doable. You can customize the pagination item position and the items themselves, as well as override entire behaviour of default pagination.

#### Static pagination (InventoryPageGroup)
Static pagination does not inherently mean that the pages are static, it means that pages are not created or removed automatically when inventory items are added or removed (like it is done through `InventoryDynamicPageGroup`). Rather, you yourself add, remove and manage pages.

```java
    CustomInventoryBuilder.builder()
        .title("Multiple pages, like magic!")
        .populate(InventoryPagePopulator.create()
            // update the placeholder for when there's no pagination item (optional)
            .setPlaceholder(InventoryItemFactory.cancel(Material.GRAY_STAINED_GLASS_PANE))
            .addPage((parent) -> InventoryStoragePopulator.create(parent)
                .fill(Material.GREEN_STAINED_GLASS_PANE)
                .set(parent.getArea().center(), Material.EMERALD)
                .getView())
            .addPage((parent) -> InventoryStoragePopulator.create(parent)
                .fill(Material.RED_STAINED_GLASS_PANE)
                .set(parent.getArea().center(), Material.REDSTONE)
                .getView())
            // you can even update the pagination items if you want to
            // .setItem(PaginationItemType.PREVIOUS_PAGE, ...)
            // .setItem(PaginationItemType.NEXT_PAGE, ...)
            .getView())
        .build(/*plugin*/ this)
        .show(player);
```

#### Dynamic pagination (InventoryDynamicPageGroup)
Dynamic pagination is, as can already be seen, dynamic, as in pages are created and removed dynamically by adding and removing elements to and from a collection.

[ FOLLOWS ]
