# bukkit-gui
A very sophisticated open-source inventory library for Bukkit.<br/>
This library is based on [bommons](https://github.com/aparx/bommons) - a Bukkit library for common utilities.

## Ideology
The idea behind bukkit-gui is to be able to easily create very complex but also very simple inventories, that act as a user interface, with which a player or group of players can easily interact with. Inventories are updated in an interval automatically, or when players interact with items that are clickable.<br/><br/>
In bukkit-gui an inventory is represented by a class that wraps around a mutable content attribute, which can be updated at any time. This content is an abstract class, which can represent virtually anything happening on a 2D pane. There are very useful default content views, that enable virtually anything: from layers, to pagination, to simple pages evolving around filling material.

## Basic example
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

## Installation
[![](https://jitpack.io/v/aparx/bukkit-gui.svg)](https://jitpack.io/#aparx/bukkit-gui)

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.aparx.bukkit-gui</groupId>
    <artifactId>core</artifactId>
    <version>2.0.0-beta.1</version>
  </dependency>
</dependencies>
```

[For more ways of installing read here](https://jitpack.io/#aparx/bukkit-gui)

## Dynamic and static pagination
Pagination can be a head-scratcher sometimes, but with this library it is easily doable. You can customize the pagination item position and the items themselves, as well as override entire behaviour of default pagination.

### Static pagination (InventoryPageGroup)
Static pagination does not inherently mean that the pages are static, it means that pages are not created or removed automatically when inventory items are added or removed (like it is done through `InventoryDynamicPageGroup`). Rather, you yourself add, remove and manage pages.

```java
    CustomInventoryBuilder.builder()
        .title("Multiple pages, like magic!")
        .populate(InventoryPagePopulator.create(InventoryDimensions.ofHeight(3))
            .addPage((parent) -> InventoryStoragePopulator.create(parent)
                .fill(Material.GREEN_STAINED_GLASS_PANE)
                .set(parent.getArea().center(), Material.EMERALD)
                .getView())
            .addPage((parent) -> InventoryStoragePopulator.create(parent)
                .fill(Material.RED_STAINED_GLASS_PANE)
                .set(parent.getArea().center(), Material.REDSTONE)
                .getView())
            // you can update the placeholder for when there's no pagination item
            // .setPlaceholder(InventoryItemFactory.cancel(Material.GRAY_STAINED_GLASS_PANE))
            // you can even update the pagination items if you want to
            // .setItem(PaginationItemType.PREVIOUS_PAGE, ...)
            // .setItem(PaginationItemType.NEXT_PAGE, ...)
            // ...
            .getView())
        .build(plugin)
        .show(player);
```
<img src="https://i.gyazo.com/06ac56b7456d0e5c3c75715bc063aa59.gif" width="250" alt="Result of static pagination"/>

### Dynamic pagination (InventoryDynamicPageGroup)
Dynamic pagination is, as can already be seen, dynamic, as in pages are created and removed dynamically by adding and removing elements to and from a collection.

```java
    // explicitly state, so we can determine how many pages of redstone we want
    InventoryDimensions dimensions = InventoryDimensions.ofHeight(3);

    // allocate ordinary amount of items, everything after the size of the inventory will
    // be put into separate inventories and pagination items will be shown
    InventoryItem[] items = new InventoryItem[2 /* pages */ * (dimensions.size() - 2)];
    for (int i = 0; i < items.length; ++i)
      items[i] = InventoryItemFactory.cancel(Material.REDSTONE, 1 + i);

    CustomInventoryBuilder.builder()
        .title("Dynamic pages!")
        .populate(InventoryDynamicPagePopulator.create(dimensions)
            .setElements(items)
            // same as in InventoryPagePopulator:
            // .setPlaceholder(InventoryItemFactory.cancel(Material.GRAY_STAINED_GLASS_PANE))
            // .setItem(PaginationItemType.PREVIOUS_PAGE, ...)
            // .setItem(PaginationItemType.NEXT_PAGE, ...)
            // ...
            .getView())
        .build(plugin)
        .show(player);
```

### Multiple pagination groups in one page
Due to layers and partitioning, you can have multiple regions within an inventory in which content is shown. For pagination this means you can (theoretically) have unlimited amount of pagination groups in one single page displayed at the same time.

```java
    CustomInventoryBuilder.builder()
        .title("Multiple pages, like magic!")
        .populate(InventoryLayerPopulator.create()
            // first layer is a section at the first half of the inventory
            .addLayer((x) -> InventoryPagePopulator.create(InventorySection.of(0, 0, 3, 3), x)
                .addPage((pageGroup) -> InventoryStoragePopulator.create(pageGroup)
                    .fill(Material.RED_STAINED_GLASS_PANE)
                    .getView())
                .addPage((pageGroup) -> InventoryStoragePopulator.create(pageGroup)
                    .fill(Material.GREEN_STAINED_GLASS_PANE)
                    .getView())
                .getView())
            // second layer is a section at the second half of the inventory
            .addLayer((x) -> InventoryPagePopulator.create(InventorySection.of(5, 0, 8, 3), x)
                .addPage((pageGroup) -> InventoryStoragePopulator.create(pageGroup)
                    .fill(Material.BLUE_STAINED_GLASS_PANE)
                    .getView())
                .addPage((pageGroup) -> InventoryStoragePopulator.create(pageGroup)
                    .fill(Material.YELLOW_STAINED_GLASS_PANE)
                    .getView())
                .getView())
            .getView())
        .build(plugin)
        .show(player);
```

<img src="https://i.gyazo.com/800c910ba22c6a059187874600026581.gif" width="250" alt="Result of partitioned pagination groups"/>

**Note:** it is generally recommended to not use the inventory builder for very complex inventories, but rather to create custom inventory implementations or be more verbose with creating layers and content. Using the `InventoryContentFactory` will help make your complex code more reliable and recognizable.

**There is so much more to this, that it will be eventually covered in a wiki in the future.**
