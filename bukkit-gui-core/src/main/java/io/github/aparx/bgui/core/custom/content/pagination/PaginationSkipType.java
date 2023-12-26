package io.github.aparx.bgui.core.custom.content.pagination;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-25 04:10
 * @since 1.0
 */
public enum PaginationSkipType {

  PREVIOUS(-1),
  NEXT(1);

  private final int factor;

  PaginationSkipType(int factor) {
    this.factor = factor;
  }

  public int getFactor() {
    return factor;
  }

}
