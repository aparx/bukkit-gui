import com.github.aparx.bgui.core.dimension.InventoryDimensions;
import com.github.aparx.bgui.core.dimension.InventoryPosition;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-12-30 09:36
 * @since 2.0
 */
public class TestInventoryDimensions {

  @Test
  public void testGetMin() {
    Assert.assertEquals(InventoryDimensions.of(3, 4), InventoryDimensions.getMin(
        InventoryDimensions.of(9, 4), InventoryDimensions.of(3, 5)
    ));
    Assert.assertEquals(InventoryDimensions.of(3, 4), InventoryDimensions.getMin(
        InventoryDimensions.of(3, 4), InventoryDimensions.of(3, 5)
    ));
    Assert.assertEquals(InventoryDimensions.of(3, 2), InventoryDimensions.getMin(
        InventoryDimensions.of(5, 4), InventoryDimensions.of(3, 2)
    ));
    Assert.assertEquals(InventoryDimensions.of(3, 2), InventoryDimensions.getMin(
        InventoryDimensions.of(3, 2), InventoryDimensions.of(5, 4)
    ));
  }

  @Test
  public void testGetMax() {
    Assert.assertEquals(InventoryDimensions.of(9, 5), InventoryDimensions.getMax(
        InventoryDimensions.of(9, 4), InventoryDimensions.of(3, 5)
    ));
    Assert.assertEquals(InventoryDimensions.of(7, 5), InventoryDimensions.getMax(
        InventoryDimensions.of(7, 4), InventoryDimensions.of(3, 5)
    ));
    Assert.assertEquals(InventoryDimensions.of(5, 4), InventoryDimensions.getMax(
        InventoryDimensions.of(5, 4), InventoryDimensions.of(3, 2)
    ));
    Assert.assertEquals(InventoryDimensions.of(5, 4), InventoryDimensions.getMax(
        InventoryDimensions.of(3, 2), InventoryDimensions.of(5, 4)
    ));
  }

  @Test
  public void testIncludes() {
    InventoryDimensions dimensions = InventoryDimensions.ofHeight(3);
    Assert.assertTrue(dimensions.includes(InventoryPosition.ofPoint(8, 0)));
    Assert.assertTrue(dimensions.includes(InventoryPosition.ofPoint(8, 1)));
    Assert.assertTrue(dimensions.includes(InventoryPosition.ofPoint(8, 2)));
    Assert.assertFalse(dimensions.includes(InventoryPosition.ofPoint(8, 3)));
  }

}
