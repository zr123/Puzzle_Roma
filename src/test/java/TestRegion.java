import main.Arrow;
import main.Cell;
import main.Region;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestRegion {

    @Test
    public void testCheckCorrectnes_True(){
        Region testRegion = new Region();
        testRegion.addCell(new Cell(Arrow.UP));
        testRegion.addCell(new Cell(Arrow.DOWN));
        testRegion.addCell(new Cell(Arrow.LEFT));
        testRegion.addCell(new Cell(Arrow.RIGHT));
        assertTrue(testRegion.checkCorrectness());
    }

    @Test
    public void testCheckCorrectnes_False(){
        Region testRegion = new Region();
        testRegion.addCell(new Cell(Arrow.UP));
        testRegion.addCell(new Cell(Arrow.UP));
        testRegion.addCell(new Cell(Arrow.LEFT));
        assertFalse(testRegion.checkCorrectness());
    }
}
