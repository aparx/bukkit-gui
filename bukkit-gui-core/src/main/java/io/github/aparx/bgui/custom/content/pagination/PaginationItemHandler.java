package io.github.aparx.bgui.custom.content.pagination;

import com.google.common.base.Preconditions;
import io.github.aparx.bgui.InventoryPosition;
import io.github.aparx.bgui.item.InventoryItem;
import io.github.aparx.bgui.item.InventoryItemFactory;
import io.github.aparx.bommons.item.ItemStackBuilders;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.EnumMap;
import java.util.Objects;

/**
 * Class for handling pagination items.
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-12-25 23:37
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public final class PaginationItemHandler {

  private static final InventoryItem DEFAULT_PREVIOUS_PAGE = InventoryItemFactory.cancel(
      ItemStackBuilders.create(Material.ARROW).name("Previous").build());

  private static final InventoryItem DEFAULT_NEXT_PAGE = InventoryItemFactory.cancel(
      ItemStackBuilders.create(Material.ARROW).name("Next").build());

  private static final InventoryItem DEFAULT_PLACEHOLDER = InventoryItemFactory.cancel(
      ItemStackBuilders.create(Material.GRAY_STAINED_GLASS_PANE).name(StringUtils.SPACE).build());

  private final InventoryPageGroup group;

  private final EnumMap<PaginationItemType, PaginationItem> items =
      new EnumMap<>(PaginationItemType.class);

  private @Nullable InventoryItem placeholder;

  public PaginationItemHandler(InventoryPageGroup group) {
    Preconditions.checkNotNull(group);
    this.group = group;
    InventoryPosition lastPosition = InventoryPosition.ofLast(group.getArea());
    set(PaginationItemType.PREVIOUS_PAGE,
        lastPosition.shift(1 - group.getArea().getDimensions().getWidth()),
        DEFAULT_PREVIOUS_PAGE);
    set(PaginationItemType.NEXT_PAGE, lastPosition, DEFAULT_NEXT_PAGE);
    setPlaceholder(DEFAULT_PLACEHOLDER);
  }

  public void set(PaginationItemType type, PaginationItem item) {
    set(type, item.getAbsolutePosition(), item.getItem());
  }

  public void set(PaginationItemType type, InventoryItem item) {
    set(type, items.get(type).getAbsolutePosition(), item);
  }

  public void set(PaginationItemType type, InventoryPosition relative, InventoryItem item) {
    Preconditions.checkNotNull(type, "Type must not be null");
    Preconditions.checkNotNull(relative, "Position must not be null");
    Preconditions.checkNotNull(item, "Item must not be null");
    items.put(type, new PaginationItem(type, group.toAbsolute(relative),
        InventoryItemFactory.builder(item)
            .addClickHandler((__, event) -> group.paginate(type.getSkipType(), 1))
            .build()));
  }

  public PaginationItem get(PaginationItemType type) {
    return Objects.requireNonNull(items.get(type));
  }

  public InventoryPageGroup getGroup() {
    return group;
  }

  /**
   * Returns the placeholder for when no pagination item should be displayed, but a placeholder,
   * that indicates no specific action to be available.
   *
   * @return the placeholder, {@code nullable}
   */
  public @Nullable InventoryItem getPlaceholder() {
    return placeholder;
  }

  public void setPlaceholder(@Nullable InventoryItem placeholder) {
    this.placeholder = placeholder;
  }

  public static class PaginationItem {
    private final PaginationItemType type;
    /** The position absolutely aligned (to the parent) */
    private final InventoryPosition absolutePosition;
    private final InventoryItem item;

    public PaginationItem(
        PaginationItemType type,
        InventoryPosition absolutePosition,
        InventoryItem item) {
      Preconditions.checkNotNull(type, "Type must not be null");
      Preconditions.checkNotNull(absolutePosition, "Position must not be null");
      Preconditions.checkNotNull(item, "Item must not be null");
      this.type = type;
      this.absolutePosition = absolutePosition;
      this.item = item;
    }

    public PaginationItemType getType() {
      return type;
    }

    public InventoryPosition getAbsolutePosition() {
      return absolutePosition;
    }

    public InventoryItem getItem() {
      return item;
    }
  }
}
