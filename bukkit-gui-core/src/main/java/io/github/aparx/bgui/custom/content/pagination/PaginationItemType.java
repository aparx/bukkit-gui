package io.github.aparx.bgui.custom.content.pagination;

/**
 *
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-12-25 04:09
 * @since 1.0
 */
public enum PaginationItemType {

  PREVIOUS_PAGE(PaginationSkipType.PREVIOUS),
  NEXT_PAGE(PaginationSkipType.NEXT);

  private final PaginationSkipType skipType;

  PaginationItemType(PaginationSkipType skipType) {
    this.skipType = skipType;
  }

  public PaginationSkipType getSkipType() {
    return skipType;
  }
}
