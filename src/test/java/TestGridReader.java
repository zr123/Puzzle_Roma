import exception.MalformedGridException;
import main.Grid;
import main.GridReader;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestGridReader {

    private final String filePath = "src/test/resources/";

    @Test
    public void testReadGridFile_Rome1() throws IOException, MalformedGridException {
        Grid testGrid = GridReader.readGridFile(filePath + "rome1.txt");
        assertEquals(4, testGrid.getGridWidth());
        assertEquals(4, testGrid.getGridHeight());
        assertEquals(6, testGrid.getRegions().size());
    }

    @Test
    public void testReadGridFile_Rome1Solved() throws IOException, MalformedGridException {
        Grid testGrid = GridReader.readGridFile(filePath + "rome1_solved.txt");
        assertEquals(4, testGrid.getGridWidth());
        assertEquals(4, testGrid.getGridHeight());
        assertEquals(6, testGrid.getRegions().size());
    }

    @Test
    public void testReadGridFile_Rome2() throws IOException, MalformedGridException {
        Grid testGrid = GridReader.readGridFile(filePath + "rome2.txt");
        assertEquals(8, testGrid.getGridWidth());
        assertEquals(8, testGrid.getGridHeight());
        assertEquals(33, testGrid.getRegions().size());
    }

    @Test
    public void testReadGridFile_Rome3() throws IOException, MalformedGridException {
        Grid testGrid = GridReader.readGridFile(filePath + "rome3.txt");
        assertEquals(8, testGrid.getGridWidth());
        assertEquals(8, testGrid.getGridHeight());
        assertEquals(21, testGrid.getRegions().size());
    }
}
