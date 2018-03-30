import main.Arrow;
import main.Cell;
import main.Region;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TestRegion {

    @Test
    public void testCheckCorrectness_True(){
        Region testRegion = new Region();
        testRegion.addCell(new Cell(Arrow.UP));
        testRegion.addCell(new Cell(Arrow.DOWN));
        testRegion.addCell(new Cell(Arrow.LEFT));
        testRegion.addCell(new Cell(Arrow.RIGHT));
        assertTrue(testRegion.checkCorrectness());
    }

    @Test
    public void testCheckCorrectness_False(){
        Region testRegion = new Region();
        testRegion.addCell(new Cell(Arrow.UP));
        testRegion.addCell(new Cell(Arrow.UP));
        testRegion.addCell(new Cell(Arrow.LEFT));
        assertFalse(testRegion.checkCorrectness());
    }

    @Test
    public void testCheckCopyConstructor(){
        Region testRegion = new Region();
        testRegion.addCell(new Cell(Arrow.UP));
        Region copyRegion = new Region(testRegion);
        Set<Arrow> remainingOptions = new HashSet<>();
        remainingOptions.add(Arrow.DOWN);
        remainingOptions.add(Arrow.LEFT);
        remainingOptions.add(Arrow.RIGHT);
        assertEquals(remainingOptions, copyRegion.getOptions());
    }
}
