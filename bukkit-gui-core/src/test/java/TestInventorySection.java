import io.github.aparx.bgui.core.InventoryPosition;
import io.github.aparx.bgui.core.InventorySection;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 14:28
 * @since 1.0
 */
public class TestInventorySection {

  @Test
  public void testInterpolation() {
    InventorySection section = InventorySection.of(InventoryPosition.ofIndex(1),
        InventoryPosition.ofIndex(4));
    Iterator<InventoryPosition> iterator = section.iterator();
    for (int i = 1; i <= 4; ++i) {
      Assert.assertTrue(iterator.hasNext());
      Assert.assertEquals(i, iterator.next().getColumn());
    }
    Assert.assertFalse(iterator.hasNext());
  }

  @Test
  public void testSubsection() {
    InventorySection section = InventorySection.of(InventoryPosition.ofIndex(1),
        InventoryPosition.ofIndex(4));
    Assert.assertEquals(section.subsection(0, 3), section);
    Assert.assertEquals(section.subsection(0, 2).getEnd(), InventoryPosition.ofIndex(3));
    Assert.assertEquals(section.subsection(1, 3), section.subsection(1));
  }

  @Test
  public void testCenter() {
    InventorySection pos = InventorySection.of(InventoryPosition.ofPoint(1, 1),
        InventoryPosition.ofPoint(3, 3));
    // Matrix:
    // [ ][ ][ ]
    // [ ][X][ ]
    // [ ][ ][ ]
    // TODO
  }

}
