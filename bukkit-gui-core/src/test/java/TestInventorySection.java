import io.github.aparx.bgui.core.dimension.InventoryPosition;
import io.github.aparx.bgui.core.dimension.InventorySection;
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
  public void testToRelative() {
    InventorySection section = InventorySection.of(1, 1, 4, 4);
    InventoryPosition position = InventoryPosition.ofPoint(1, 1);
    Assert.assertTrue(section.toRelative(position).equalIndex(0));
    position = InventoryPosition.ofPoint(2, 1);
    Assert.assertTrue(section.toRelative(position).equalIndex(1));
    position = InventoryPosition.ofPoint(2, 4);
    Assert.assertTrue(section.toRelative(position).equalIndex(13));
    position = InventoryPosition.ofPoint(2, 5);
    Assert.assertTrue(section.toRelative(InventoryPosition.ofPoint(1, 1)).equalIndex(0));
    final InventoryPosition finalPosition = position;
    Assert.assertThrows(IllegalArgumentException.class, () -> section.toRelative(finalPosition));
  }

  @Test
  public void testToAbsolute0() {
    InventorySection section = InventorySection.of(1, 1, 4, 4);
    Assert.assertEquals(InventoryPosition.ofPoint(1, 1),
        section.toAbsolute(InventoryPosition.ofZero()));
    Assert.assertEquals(InventoryPosition.ofPoint(2, 1),
        section.toAbsolute(InventoryPosition.ofIndex(1)));
    Assert.assertEquals(InventoryPosition.ofPoint(2, 2),
        section.toAbsolute(InventoryPosition.ofPoint(1, 1)));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> section.toAbsolute(InventoryPosition.ofPoint(1, 5)));
  }

  @Test
  public void testToAbsolute1() {
    InventorySection section = InventorySection.of(1, 1, 3, 3);
    Assert.assertEquals(InventoryPosition.ofPoint(1, 1),
        section.toAbsolute(InventoryPosition.ofZero()));
    Assert.assertEquals(InventoryPosition.ofPoint(2, 1),
        section.toAbsolute(InventoryPosition.ofIndex(1)));
    Assert.assertEquals(InventoryPosition.ofPoint(2, 2),
        section.toAbsolute(InventoryPosition.ofPoint(1, 1)));
  }

  @Test
  public void testToAbsoluteSection() {
    InventorySection parent = InventorySection.of(1, 1, 3, 3);
    Assert.assertEquals(parent, InventorySection.of(0, 0, 2, 2).toAbsolute(parent));
    Assert.assertEquals(InventorySection.of(2, 2, 3, 3),
        InventorySection.of(1, 1, 2, 2).toAbsolute(parent));
    Assert.assertEquals(InventorySection.of(2, 2, 2, 2),
        InventorySection.of(1, 1, 1, 1).toAbsolute(parent));
  }

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

}
