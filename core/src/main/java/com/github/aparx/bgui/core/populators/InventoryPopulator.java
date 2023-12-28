package com.github.aparx.bgui.core.populators;

import com.github.aparx.bgui.core.content.InventoryContentView;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-26 05:29
 * @since 1.0
 */
public interface InventoryPopulator<T extends InventoryContentView> {

  @NonNull T getView();

}
