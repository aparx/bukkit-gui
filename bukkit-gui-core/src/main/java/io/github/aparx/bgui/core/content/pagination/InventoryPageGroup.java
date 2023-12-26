package io.github.aparx.bgui.core.content.pagination;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.aparx.bgui.core.dimension.InventoryPosition;
import io.github.aparx.bgui.core.dimension.InventorySection;
import io.github.aparx.bgui.core.CopyableInventoryContentView;
import io.github.aparx.bgui.core.InventoryContentView;
import io.github.aparx.bgui.core.item.InventoryItem;
import io.github.aparx.bgui.core.item.InventoryItemAccessor;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An {@code InventoryContentView} implementation, that has the ability to contain multiple pages
 * that can be displayed as wanted.
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 21:24
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public class InventoryPageGroup extends CopyableInventoryContentView implements Iterable<InventoryContentView> {

  private final ArrayList<InventoryContentView> pages = new ArrayList<>();

  private final PaginationItemHandler itemHandler;

  private @NonNegative int pageIndex;

  public InventoryPageGroup(InventorySection area, @Nullable InventorySection parent) {
    super(area, parent);
    this.itemHandler = new PaginationItemHandler(this);
  }

  @Override
  public InventoryPageGroup copy() {
    InventoryPageGroup pageGroup = new InventoryPageGroup(getArea(), getParent());
    pageGroup.pages.addAll(pages);
    pageGroup.itemHandler.setPlaceholder(itemHandler.getPlaceholder());
    for (PaginationItemType type : PaginationItemType.values())
      pageGroup.itemHandler.set(type, itemHandler.get(type));
    return pageGroup;
  }

  /**
   * Returns true if pagination is present.
   * <p>This implicitly means that if the returning value is true, pagination items should be
   * displayed for the user to walk through pages.
   *
   * @return true if pagination is apparent
   * @implSpec The default implementation returns true if the page count is greater than one, as
   * in there is multiple pages that can be paginated through.
   */
  public boolean hasPagination() {
    return getPageCount() > 1;
  }

  @Override
  public @Nullable InventoryItem get(
      @Nullable InventoryItemAccessor accessor, InventoryPosition position) {
    if (hasPagination())
      for (PaginationItemType type : PaginationItemType.values()) {
        PaginationItemHandler.PaginationItem item = itemHandler.get(type);
        if (position.getIndex() == item.getAbsolutePosition().getIndex()) {
          if (hasMore(type.getSkipType(), 1))
            return item.getItem();
          @Nullable InventoryItem placeholder = itemHandler.getPlaceholder();
          if (placeholder != null) return placeholder;
          else break;
        }
      }
    @Nullable InventoryContentView page = getCurrentPage();
    return (page != null ? page.get(accessor, position) : null);
  }

  @CanIgnoreReturnValue
  public boolean paginate(int toIndex) {
    if (toIndex < 0 || toIndex >= pages.size())
      return false;
    this.pageIndex = toIndex;
    // TODO force re-render?
    return true;
  }

  @CanIgnoreReturnValue
  public boolean paginate(PaginationSkipType type, int amount) {
    int newPageIndex = Math.max(0, Math.min(pageIndex + amount * type.getFactor(), pages.size()));
    return pageIndex != newPageIndex && paginate(newPageIndex);
  }

  public boolean hasMore(PaginationSkipType type, int multiplier) {
    int target = pageIndex + multiplier * type.getFactor();
    if (type == PaginationSkipType.NEXT)
      return target >= 0 && target < pages.size();
    if (type == PaginationSkipType.PREVIOUS)
      return target >= 0 && !pages.isEmpty();
    return false;
  }

  public boolean hasNextPage() {
    return hasMore(PaginationSkipType.NEXT, 1);
  }

  public boolean hasPreviousPage() {
    return hasMore(PaginationSkipType.PREVIOUS, 1);
  }

  public void clear() {
    pages.clear();
  }

  @CanIgnoreReturnValue
  public int addPage(InventoryContentView page) {
    Preconditions.checkArgument(getArea().includes(page.getArea()), "Page is larger than parent");
    int index = 1 + pages.size();
    pages.add(page);
    return index;
  }

  public InventoryContentView getPage(int index) {
    Preconditions.checkElementIndex(index, pages.size());
    return pages.get(index);
  }

  public @Nullable InventoryContentView getCurrentPage() {
    return (pageIndex < pages.size() ? getPage(pageIndex) : null);
  }

  public int getPageCount() {
    return pages.size();
  }

  public int getPageIndex() {
    return pageIndex;
  }

  public PaginationItemHandler getItemHandler() {
    return itemHandler;
  }

  @Override
  public Iterator<InventoryContentView> iterator() {
    return new Iterator<>() {

      int cursor = 0;

      @Override
      public boolean hasNext() {
        return cursor < pages.size();
      }

      @Override
      public InventoryContentView next() {
        return getPage(cursor++);
      }
    };
  }


}
