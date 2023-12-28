package com.github.aparx.bgui.core.content.pagination;

import com.github.aparx.bgui.core.content.CopyableInventoryContentView;
import com.github.aparx.bgui.core.content.InventoryContentView;
import com.github.aparx.bgui.core.dimension.InventoryPosition;
import com.github.aparx.bgui.core.dimension.InventorySection;
import com.github.aparx.bgui.core.content.InventoryStorageLayer;
import com.github.aparx.bgui.core.item.InventoryItem;
import com.github.aparx.bgui.core.item.InventoryItemAccessor;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-25 03:12
 * @since 1.0
 */
@DefaultQualifier(NonNull.class)
public class InventoryDynamicPageGroup extends CopyableInventoryContentView {

  private final transient Object lock = new Object();

  private final InventoryPageGroup group;
  private final List<@Nullable InventoryItem> elements = new ElementList();

  private volatile boolean updatePages;

  public InventoryDynamicPageGroup(InventorySection area, @Nullable InventorySection parent) {
    this(new InventoryPageGroup(area, parent));
  }

  public InventoryDynamicPageGroup(InventoryPageGroup pagesGroup) {
    super(pagesGroup.getSpace(), pagesGroup.getParent());
    this.group = pagesGroup;
  }

  @Override
  public InventoryDynamicPageGroup copy() {
    InventoryDynamicPageGroup pageGroup = new InventoryDynamicPageGroup(group.copy());
    pageGroup.elements.addAll(elements);
    return pageGroup;
  }

  @Override
  public @Nullable InventoryItem get(
      @Nullable InventoryItemAccessor accessor, InventoryPosition position) {
    synchronized (lock) {
      try {
        if (updatePages) createPages();
        return group.get(accessor, position);
      } finally {
        // we have (!) to ensure `updatePages` is false, after lookups
        updatePages = false;
      }
    }
  }

  public InventoryPageGroup getGroup() {
    return group;
  }

  /**
   * Returns a mutable list of elements.
   * <p>Changes made in the returning list will reflect changes in this content view.
   * <p>Adding and removing from the below list leads to a forced re-creation of all pages.
   *
   * @return all elements from this group
   */
  public List<@Nullable InventoryItem> getElements() {
    return elements;
  }

  /**
   * Returns the expected number of pages, for given maximum elements size per page, based off of
   * the amount of elements currently given.
   *
   * @param maxPerPage the maximum size per page
   * @return the expected number of pages
   */
  public final int getExpectedNumberOfPages(int maxPerPage) {
    return (int) Math.ceil(elements.size() / (double) maxPerPage);
  }

  /**
   * Returns the default maximum size of elements per page.
   *
   * @return the default maximum element size per page.
   */
  public int getMaximumSizePerPage() {
    int areaSize = group.getArea().size();
    int pageCount = getExpectedNumberOfPages(areaSize);
    if (group.hasPagination(pageCount))
      return areaSize - getExcludingElementIndices(pageCount).length;
    return areaSize;
  }

  /**
   * Returns the expected number of pages, using the default maximum element size per page.
   * <p>The expected page count depends on the currently given size of elements.
   *
   * @return the expected number of pages
   * @see #getExpectedNumberOfPages(int)
   * @see #getMaximumSizePerPage()
   */
  public int getExpectedNumberOfPages() {
    return getExpectedNumberOfPages(getMaximumSizePerPage());
  }

  /** @see #createPages(int) */
  public void createPages() {
    createPages(getMaximumSizePerPage());
  }

  /**
   * Recreates all pages to the expected amount of pages, based off of given maximum number of
   * elements per page.
   *
   * @param maxPerPage the maximum number of elements per page
   */
  public void createPages(int maxPerPage) {
    synchronized (lock) {
      final int elemSize = elements.size();
      group.clear();
      int expectedPageCount = getExpectedNumberOfPages(maxPerPage);
      for (int c = elemSize + maxPerPage, i = 0; (c -= maxPerPage) > 0; ++i)
        group.addPage(createPage(expectedPageCount, i, elements.subList(i * maxPerPage,
            Math.min((1 + i) * maxPerPage, elemSize))));
    }
  }

  protected InventoryContentView createPage(
      int pageCount, int pageIndex, List<@Nullable InventoryItem> elements) {
    InventoryStorageLayer storage = new InventoryStorageLayer(group.getArea(), group.getParent());
    int[] excludeIndices = getExcludingElementIndices(pageCount);
    int insertIndex = 0;
    for (InventoryItem element : elements) {
      // skip all indices that involve any of the pagination items
      while (ArrayUtils.contains(excludeIndices, insertIndex))
        ++insertIndex;
      storage.set(insertIndex, element);
      ++insertIndex;
    }
    return storage;
  }

  /**
   * Returns an array of relative indices that represent pagination items, or an empty array if
   * no pagination items should be displayed.
   *
   * @param pageCount the amount of pages expected, being passed to
   *                  {@code InventoryPageGroup#hasPagination(int)}
   * @return excluding element indices, that should be skipped when filling elements
   * @apiNote Every invocation creates a copy of the underlying array, such that modifications on
   * the returning array have no effect on this group.
   */
  protected int[] getExcludingElementIndices(int pageCount) {
    if (group.hasPagination(pageCount))
      return getEnsuredExcludingElementIndices();
    return ArrayUtils.EMPTY_INT_ARRAY;
  }

  /** Returns the relative indices of all pagination items (regardless of pagination itself) */
  private int[] getEnsuredExcludingElementIndices() {
    PaginationItemType[] types = PaginationItemType.values();
    int[] excludeIndices = new int[types.length];
    for (int i = 0; i < types.length; ++i)
      excludeIndices[i] = group.getItemHandler().get(types[i])
          .getAbsolutePosition().toRelative(getArea()).getIndex();
    return excludeIndices;
  }

  private class ElementList extends ArrayList<@Nullable InventoryItem> {

    @Override
    public void clear() {
      synchronized (lock) {
        super.clear();
        updatePages = true;
      }
    }

    @Override
    public boolean add(@Nullable InventoryItem item) {
      synchronized (lock) {
        if (!super.add(item))
          return false;
        return updatePages = true;
      }
    }

    @Override
    public void add(int index, @Nullable InventoryItem element) {
      synchronized (lock) {
        super.add(index, element);
        updatePages = true;
      }
    }

    @Override
    public boolean remove(Object o) {
      synchronized (lock) {
        if (!super.remove(o))
          return false;
        return updatePages = true;
      }
    }

    @Override
    public @Nullable InventoryItem remove(int index) {
      synchronized (lock) {
        @Nullable InventoryItem item = super.remove(index);
        updatePages |= item != null;
        return item;
      }
    }

    @Override
    public boolean addAll(Collection<? extends @Nullable InventoryItem> c) {
      synchronized (lock) {
        if (!super.addAll(c))
          return false;
        return updatePages = true;
      }
    }

    @Override
    public boolean addAll(int index, Collection<? extends @Nullable InventoryItem> c) {
      synchronized (lock) {
        if (!super.addAll(index, c))
          return false;
        return updatePages = true;
      }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      synchronized (lock) {
        if (!super.removeAll(c))
          return false;
        return updatePages = true;
      }
    }

    @Override
    public boolean removeIf(Predicate<? super @Nullable InventoryItem> filter) {
      synchronized (lock) {
        if (!super.removeIf(filter))
          return false;
        return updatePages = true;
      }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      synchronized (lock) {
        if (!super.retainAll(c))
          return false;
        return updatePages = true;
      }
    }

  }

}
