package main;

import exception.ErroneousGridException;

import java.util.HashSet;
import java.util.Set;

public class Cell {

    private Region region;
    private Arrow arrow;
    private boolean immutable;
    private Set<Arrow> options = new HashSet<>();

    public Cell(Arrow aArrow){
        arrow = aArrow;
        if (aArrow.equals(Arrow.NONE)){
            options = Arrow.getDefaultOptions();
        }
        this.immutable = false;
    }

    public Cell(Arrow aArrow, boolean aUnmutable){
        if(aArrow.equals(Arrow.NONE) && aUnmutable)
            throw new UnsupportedOperationException("Can't assign NONE to an immutable main.Cell");
        this.arrow = aArrow;
        this.immutable = aUnmutable;
    }

    public Cell(Cell cell) {
        arrow = cell.arrow;
        immutable = cell.immutable;
        options.addAll(cell.options);
    }

    /**
     * This function checks if a cell is unsolved and has only one option remainging.
     * @return True of the Cell has only one Option remaining. Otherwise false.
     * @throws ErroneousGridException If there are no options left the current grid is erroneous.
     */
    public boolean checkCellOptions() throws ErroneousGridException {
        if (arrow.equals(Arrow.NONE))
            switch (options.size()){
                case 0: throw new ErroneousGridException("");
                case 1: return true;
            }
        return false;
    }

    public Set<Arrow> getOptions(){
        return options;
    }

    public void removeOption(Arrow direction){
        options.remove(direction);
    }

    public Region getRegion(){
        return this.region;
    }

    public void setRegion(Region aRegion){
        this.region = aRegion;
    }

    public Arrow getArrow() {
        return arrow;
    }

    public void setArrow(Arrow arrow) {
        if(immutable)
            throw new UnsupportedOperationException("Can't modify immutable cell.");
        options.remove(arrow);
        this.arrow = arrow;
        getRegion().removeOption(arrow);
    }
}
