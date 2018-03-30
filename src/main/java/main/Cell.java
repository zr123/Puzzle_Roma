package main;

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
            options.add(Arrow.UP);
            options.add(Arrow.DOWN);
            options.add(Arrow.LEFT);
            options.add(Arrow.RIGHT);
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
