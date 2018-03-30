import main.Arrow;
import main.Cell;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestCell{

    @Test
    public void testCellConstructor1(){
        Cell testCell = new Cell(Arrow.NONE);
        Set<Arrow> options = new HashSet<>();
        options.add(Arrow.UP);
        options.add(Arrow.DOWN);
        options.add(Arrow.LEFT);
        options.add(Arrow.RIGHT);
        assertEquals(Arrow.NONE, testCell.getArrow());
        assertEquals(options, testCell.getOptions());
    }

    @Test
    public void testCellConstructor2(){
        Cell testCell = new Cell(Arrow.UP);
        Set<Arrow> options = new HashSet<>();
        assertEquals(Arrow.UP, testCell.getArrow());
        assertEquals(options, testCell.getOptions());
    }

    @Test
    public void testCellCopyConstructor(){
        Cell testCell = new Cell(Arrow.NONE);
        Cell copyCell = new Cell(testCell);
        Set<Arrow> options = new HashSet<>();
        options.add(Arrow.UP);
        options.add(Arrow.DOWN);
        options.add(Arrow.LEFT);
        options.add(Arrow.RIGHT);
        assertEquals(Arrow.NONE, copyCell.getArrow());
        assertEquals(options, copyCell.getOptions());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCellImmutableConstructor(){
        Cell testCell = new Cell(Arrow.CIRCLE, true);
        Set<Arrow> options = new HashSet<>();
        assertEquals(Arrow.CIRCLE, testCell.getArrow());
        assertEquals(options, testCell.getOptions());
        testCell.setArrow(Arrow.UP);
    }
}
