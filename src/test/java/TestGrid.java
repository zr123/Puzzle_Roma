import exception.ErroneousGridException;
import exception.MalformedGridException;
import main.Arrow;
import main.Grid;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.*;

public class TestGrid extends Grid{

    private final String filePath = "src/test/resources/";

    @Test
    public void testReadGridFile_Rome1() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1.txt");
        assertEquals(4, testGrid.getGridWidth());
        assertEquals(4, testGrid.getGridHeight());
        assertEquals(6, testGrid.getRegions().size());
    }

    @Test
    public void testReadGridFile_Rome1Solved() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1_solved.txt");
        assertEquals(4, testGrid.getGridWidth());
        assertEquals(4, testGrid.getGridHeight());
        assertEquals(6, testGrid.getRegions().size());
    }

    @Test
    public void testReadGridFile_Rome2() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome2.txt");
        assertEquals(8, testGrid.getGridWidth());
        assertEquals(8, testGrid.getGridHeight());
        assertEquals(33, testGrid.getRegions().size());
    }

    @Test
    public void testReadGridFile_Rome3() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome3.txt");
        assertEquals(8, testGrid.getGridWidth());
        assertEquals(8, testGrid.getGridHeight());
        assertEquals(21, testGrid.getRegions().size());
    }

    @Test
    public void testCopyConstructor() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome3.txt");
        Grid copiedGrid = new Grid(testGrid);
        assertEquals(8, copiedGrid.getGridWidth());
        assertEquals(8, copiedGrid.getGridHeight());
        assertEquals(21, copiedGrid.getRegions().size());

    }

    @Test
    public void testRemoveOutwardPointingOptionFromGridEdges() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1.txt");
        testGrid.removeOutwardPointingOptionFromGridEdges();
        Set<Arrow> options = testGrid.getCell(0, 0).getOptions();
        assertFalse(options.contains(Arrow.UP));
        assertTrue(options.contains(Arrow.RIGHT));
        assertFalse(options.contains(Arrow.LEFT));
        assertTrue(options.contains(Arrow.DOWN));
        options = testGrid.getCell(3, 3).getOptions();
        assertTrue(options.contains(Arrow.UP));
        assertFalse(options.contains(Arrow.RIGHT));
        assertTrue(options.contains(Arrow.LEFT));
        assertFalse(options.contains(Arrow.DOWN));
    }

    @Test
    public void testRemoveOpposingPointingOptions() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1.txt");
        testGrid.removeOpposingPointingOptions();
        Set<Arrow> options = testGrid.getCell(0, 0).getOptions();
        assertFalse(options.contains(Arrow.DOWN));
        options = testGrid.getCell(1, 2).getOptions();
        assertFalse(options.contains(Arrow.UP));
        options = testGrid.getCell(1, 2).getOptions();
        assertFalse(options.contains(Arrow.UP));
        options = testGrid.getCell(2, 1).getOptions();
        assertFalse(options.contains(Arrow.UP));
        options = testGrid.getCell(3, 0).getOptions();
        assertFalse(options.contains(Arrow.RIGHT));
    }

    @Test
    public void testRemoveUsedArrowDirections() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1.txt");
        testGrid.removeUsedArrowDirections();
        Set<Arrow> options = testGrid.getCell(0, 0).getOptions();
        assertFalse(options.contains(Arrow.UP));
        assertTrue(options.contains(Arrow.RIGHT));
        options = testGrid.getCell(0, 1).getOptions();
        assertFalse(options.contains(Arrow.UP));
        options = testGrid.getCell(0, 3).getOptions();
        assertFalse(options.contains(Arrow.DOWN));
        options = testGrid.getCell(1, 3).getOptions();
        assertFalse(options.contains(Arrow.DOWN));
        options = testGrid.getCell(1, 2).getOptions();
        assertFalse(options.contains(Arrow.DOWN));
        options = testGrid.getCell(2, 1).getOptions();
        assertFalse(options.contains(Arrow.DOWN));
        options = testGrid.getCell(2, 0).getOptions();
        assertFalse(options.contains(Arrow.LEFT));
        options = testGrid.getCell(3, 0).getOptions();
        assertFalse(options.contains(Arrow.LEFT));
        options = testGrid.getCell(3, 2).getOptions();
        assertFalse(options.contains(Arrow.LEFT));
        options = testGrid.getCell(3, 3).getOptions();
        assertFalse(options.contains(Arrow.LEFT));
    }

    @Test
    public void testAllCellsFilled_True() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1_solved.txt");
        assertTrue(testGrid.allCellsFilled());
    }

    @Test
    public void testAllCellsFilled_False() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1.txt");
        assertFalse(testGrid.allCellsFilled());
    }

    @Test
    public void testAllRegionsValid_True() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1_solved.txt");
        assertTrue(testGrid.allRegionsValid());
    }

    @Test
    public void testAllRegionsValid_False() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1_error.txt");
        assertFalse(testGrid.allRegionsValid());
    }

    @Test
    public void testAllCellsReachTheDestination_True() throws IOException, MalformedGridException {
        Grid testGrid = new Grid();
        testGrid.readGridFile(filePath + "rome1_solved.txt");
        assertTrue(testGrid.allCellsReachTheDestination());
    }

    @Test
    public void testAllCellsReachTheDestination_FalsePositive() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1_error.txt");
        assertTrue(testGrid.allCellsReachTheDestination());
    }

    @Test
    public void testAllCellsReachTheDestination_CircularFalse() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1_circular.txt");
        assertFalse(testGrid.allCellsReachTheDestination());
    }

    @Test
    public void testAllCellsReachTheDestination_False() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1.txt");
        assertFalse(testGrid.allCellsReachTheDestination());
    }

    @Test
    public void testIsComplete_True() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1_solved.txt");
        assertTrue(testGrid.isComplete());
    }

    @Test
    public void testIsComplete_False1() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1.txt");
        assertFalse(testGrid.isComplete());
    }

    @Test
    public void testIsComplete_False2() throws IOException, MalformedGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1_error.txt");
        assertFalse(testGrid.isComplete());
    }

    @Test
    public void testSolve_Rome1() throws IOException, MalformedGridException, ErroneousGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome1.txt");
        testGrid.solve();
        assertTrue(testGrid.isComplete());
    }

    @Test
    public void testSolve_Rome2() throws IOException, MalformedGridException, ErroneousGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome2.txt");
        testGrid.solve();
        assertTrue(testGrid.isComplete());
    }

    @Test
    public void testSolve_Rome3() throws IOException, MalformedGridException, ErroneousGridException {
        Grid testGrid = Grid.readGridFile(filePath + "rome3.txt");
        testGrid.solve();
        assertTrue(testGrid.isComplete());
    }

    @Test
    public void testSolve_Roma() throws IOException, MalformedGridException, ErroneousGridException {
        Grid testGrid = Grid.readGridFile(filePath + "roma.txt");
        testGrid.solve();
        assertTrue(testGrid.isComplete());
    }
}
