package main;

import java.util.HashSet;
import java.util.Set;

public class Region {

    private Set<Cell> cells = new HashSet<>();
    private Set<Arrow> options = new HashSet<>();

    public Region() {
        options = Arrow.getDefaultOptions();
    }

    public Region(Region region){
        options.addAll(region.options);
    }

    public void addCell(Cell cell) {
        this.cells.add(cell);
        cell.setRegion(this);
        options.remove(cell.getArrow());
    }

    public Set<Cell> getCells(){
        return this.cells;
    }

    /**
     * Verify that the region doesn't contain an arrow-direction twice.
     * @return True if the region is correct. Otherwise false.
     */
    public boolean checkCorrectness(){
        Cell[] tmpCells = cells.toArray(new Cell[cells.size()]);
        for(int i = 0; i < tmpCells.length; ++i)
            for (int k = i +1; k < tmpCells.length; ++k)
                if (tmpCells[i].getArrow().equals(tmpCells[k].getArrow()))
                    return false;
        return true;
    }

    public void removeOption(Arrow arrow) {
        options.remove(arrow);
    }

    public Set<Arrow> getOptions(){
        return options;
    }

    public void removeUsedOptionsFromCells() {
        for (Cell cell : cells) {
            if(!options.contains(Arrow.UP))cell.removeOption(Arrow.UP);
            if(!options.contains(Arrow.DOWN))cell.removeOption(Arrow.DOWN);
            if(!options.contains(Arrow.LEFT))cell.removeOption(Arrow.LEFT);
            if(!options.contains(Arrow.RIGHT))cell.removeOption(Arrow.RIGHT);
        }
    }
}
