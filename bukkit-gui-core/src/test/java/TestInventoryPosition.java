import io.github.aparx.bgui.InventoryDimensions;
import io.github.aparx.bgui.InventoryPosition;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-23 13:43
 * @since 1.0
 */
public class TestInventoryPosition {

  @Test
  public void testToIndex() {
    Assert.assertEquals(0, InventoryPosition.toIndex(0, 0));
    Assert.assertEquals(7, InventoryPosition.toIndex(7, 0));
    Assert.assertEquals(8, InventoryPosition.toIndex(8, 0));
    Assert.assertThrows(IllegalArgumentException.class, () -> InventoryPosition.toIndex(9, 0));
    Assert.assertEquals(9, InventoryPosition.toIndex(0, 1));
    Assert.assertEquals(18, InventoryPosition.toIndex(0, 2));
    Assert.assertEquals(21, InventoryPosition.toIndex(3, 2));
  }

  @Test
  public void testToColumn() {
    Assert.assertEquals(2, InventoryPosition.toColumn(2, InventoryDimensions.DEFAULT_WIDTH));
    Assert.assertEquals(0, InventoryPosition.toColumn(9, InventoryDimensions.DEFAULT_WIDTH));
    Assert.assertEquals(1, InventoryPosition.toColumn(10, InventoryDimensions.DEFAULT_WIDTH));
    Assert.assertEquals(0, InventoryPosition.toColumn(18, InventoryDimensions.DEFAULT_WIDTH));
  }

  @Test
  public void testToRow() {
    Assert.assertEquals(0, InventoryPosition.toRow(0, InventoryDimensions.DEFAULT_WIDTH));
    Assert.assertEquals(1, InventoryPosition.toRow(9, InventoryDimensions.DEFAULT_WIDTH));
    Assert.assertEquals(1, InventoryPosition.toRow(10, InventoryDimensions.DEFAULT_WIDTH));
    Assert.assertEquals(1, InventoryPosition.toRow(17, InventoryDimensions.DEFAULT_WIDTH));
    Assert.assertEquals(2, InventoryPosition.toRow(18, InventoryDimensions.DEFAULT_WIDTH));
    Assert.assertEquals(2, InventoryPosition.toRow(19, InventoryDimensions.DEFAULT_WIDTH));
    Assert.assertEquals(2, InventoryPosition.toRow(25, InventoryDimensions.DEFAULT_WIDTH));
    Assert.assertEquals(2, InventoryPosition.toRow(26, InventoryDimensions.DEFAULT_WIDTH));
    Assert.assertEquals(3, InventoryPosition.toRow(27, InventoryDimensions.DEFAULT_WIDTH));
    Assert.assertEquals(3, InventoryPosition.toRow(28, InventoryDimensions.DEFAULT_WIDTH));
  }

  @Test
  public void testGetMin() {
    Assert.assertEquals(InventoryPosition.ofIndex(0),
        InventoryPosition.getMin(InventoryPosition.ofIndex(0), InventoryPosition.ofIndex(1)));
    Assert.assertEquals(InventoryPosition.ofIndex(1),
        InventoryPosition.getMin(InventoryPosition.ofIndex(3), InventoryPosition.ofIndex(1)));
  }

  @Test
  public void testGetMax() {
    Assert.assertEquals(InventoryPosition.ofIndex(1),
        InventoryPosition.getMax(InventoryPosition.ofIndex(0), InventoryPosition.ofIndex(1)));
    Assert.assertEquals(InventoryPosition.ofIndex(3),
        InventoryPosition.getMax(InventoryPosition.ofIndex(3), InventoryPosition.ofIndex(1)));
  }

  @Test
  public void testDistance() {
    Assert.assertEquals(3, InventoryPosition.ofIndex(0).distance(InventoryPosition.ofIndex(3)));
    Assert.assertEquals(9, InventoryPosition.ofIndex(0).distance(InventoryPosition.ofPoint(0, 1)));
    Assert.assertEquals(-7, InventoryPosition.ofPoint(0, 1).distance(InventoryPosition.ofIndex(2)));
  }

  @Test
  public void testShift() {
    Assert.assertEquals(3, InventoryPosition.ofIndex(0).shift(3).getIndex());
    Assert.assertEquals(6, InventoryPosition.ofIndex(1).shift(5).getIndex());
    Assert.assertEquals(0, InventoryPosition.ofIndex(1).shift(5).getRow());
    Assert.assertEquals(1, InventoryPosition.ofIndex(1).shift(9).getColumn());
    Assert.assertEquals(0, InventoryPosition.ofIndex(1).shift(-1).getIndex());
    Assert.assertThrows(IllegalArgumentException.class,
        () -> InventoryPosition.ofIndex(1).shift(-2));
  }

  @Test
  public void testAdd() {
    InventoryPosition point = InventoryPosition.ofPoint(3, 2);
    Assert.assertEquals(3, point.add(0, 0).getColumn());
    Assert.assertEquals(2, point.add(0, 0).getRow());

    Assert.assertEquals(4, point.add(1, 3).getColumn());
    Assert.assertEquals(5, point.add(1, 3).getRow());

    Assert.assertEquals(2, point.add(-1, 3).getColumn());
    Assert.assertEquals(1, point.add(1, -1).getRow());
    Assert.assertThrows(IllegalArgumentException.class, () -> point.add(7, 3));
  }

  @Test
  public void testSubtract() {
    InventoryPosition point = InventoryPosition.ofPoint(3, 2);
    Assert.assertEquals(3, point.subtract(0, 0).getColumn());
    Assert.assertEquals(2, point.subtract(0, 0).getRow());

    Assert.assertEquals(2, point.subtract(1, 2).getColumn());
    Assert.assertEquals(0, point.subtract(1, 2).getRow());

    Assert.assertEquals(4, point.subtract(-1, 0).getColumn());
    Assert.assertEquals(3, point.subtract(1, -1).getRow());

    Assert.assertThrows(IllegalArgumentException.class, () -> point.subtract(7, 3));
    Assert.assertThrows(IllegalArgumentException.class, () -> point.subtract(-7, 3));
  }

  @Test
  public void testRelative() {

  }

}
