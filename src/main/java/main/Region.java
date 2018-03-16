package main;

import java.util.HashSet;
import java.util.Set;

public class Region {

    private Set<Cell> cells = new HashSet<>();
    private Set<Arrow> options = new HashSet<>();

    public Region() {
        options.add(Arrow.UP);
        options.add(Arrow.DOWN);
        options.add(Arrow.LEFT);
        options.add(Arrow.RIGHT);
    }

    public Region(Region region){
        for (Arrow option : region.options) {
            options.add(option);
        }
    }

    public void addCell(Cell cell) {
        this.cells.add(cell);
        cell.setRegion(this);
        options.remove(cell.getArrow());
    }

    public Set<Cell> getCells(){
        return this.cells;
    }

    public boolean checkCorrectness(){
        Cell[] tmpCells = cells.toArray(new Cell[cells.size()]);
        for(int i = 0; i < tmpCells.length; ++i)
            for (int k = i +1; k < tmpCells.length; ++k)
                if (tmpCells[i].getArrow().equals(tmpCells[k].getArrow()))
                    return false;
        return true;
    }

    public void addOption(Arrow arrow){
        options.add(arrow);
    }

    public void removeOption(Arrow arrow) {
        options.remove(arrow);
    }

    public void removeUsedOptionsFromCells() {
        for (Cell cell : cells) {
            if(!options.contains(Arrow.UP))cell.removeOption(Arrow.UP);
            if(!options.contains(Arrow.DOWN))cell.removeOption(Arrow.DOWN);
            if(!options.contains(Arrow.LEFT))cell.removeOption(Arrow.LEFT);
            if(!options.contains(Arrow.RIGHT))cell.removeOption(Arrow.RIGHT);
        }
    }

    public Cell sumRemainingOptions(){
        if(cells.size() != 4)
            return null;
        int up = 0, down = 0, left = 0, right = 0;
        for (Cell cell : cells)
            for (Arrow arrow : cell.getOptions())
                switch (arrow){
                    case UP:    up++; break;
                    case DOWN:  down++; break;
                    case LEFT:  left++; break;
                    case RIGHT: right++; break;
                }
        if(up == 1) return findSingleOptionCell(Arrow.UP);
        if(down == 1) return findSingleOptionCell(Arrow.DOWN);
        if(left == 1) return findSingleOptionCell(Arrow.LEFT);
        if(right == 1) return findSingleOptionCell(Arrow.RIGHT);
        return null;
    }

    private Cell findSingleOptionCell(Arrow direction){
        for (Cell cell : cells)
            if(cell.getOptions().contains(direction))
                return cell;
        return null;
    }
}
