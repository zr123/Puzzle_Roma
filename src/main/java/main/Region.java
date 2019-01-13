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
        Set<Arrow> checkedOptions = new HashSet<>();
        for (Cell cell : cells){
            if(checkedOptions.contains(cell.getArrow())){
                return false;
            }
            checkedOptions.add(cell.getArrow());
        }
        return true;
    }

    public void removeOption(Arrow arrow) {
        options.remove(arrow);
    }

    public Set<Arrow> getOptions(){
        return options;
    }

    public void removeUsedOptionsFromCells() {
        for (Cell cell : cells)
            for(Arrow arrow : Arrow.getDefaultOptions())
                if(!options.contains(arrow))
                    cell.removeOption(arrow);
    }
}
