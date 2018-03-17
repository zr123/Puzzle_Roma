package main;

import exception.ErroneousGridException;
import exception.MalformedGridException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Grid {
    private List<String> file;
    private int gridWidth = 0;
    private int gridHeight = 0;

    private ArrayList<Region> regions = new ArrayList<>();
    //     cell[y][x]
    private Cell[][] cells;

    public Grid(){}

    public Grid(Grid grid){
        gridHeight = grid.getGridHeight();
        gridWidth = grid.getGridWidth();
        initCells();
        // add regions
        for (Region region : grid.getRegions()) {
            regions.add(new Region(region));
        }
        // add cells and set regions
        for(int y = 0; y < gridHeight; ++y)
            for (int x = 0; x < gridWidth; ++x){
                cells[y][x] = new Cell(grid.getCell(y, x));
                int regionIndex = 0;
                while(grid.getRegions().get(regionIndex) != grid.getCell(y, x).getRegion())
                    regionIndex++;
                regions.get(regionIndex).addCell(cells[y][x]);
            }
    }

    public void solve() throws ErroneousGridException {
        updatePossibilitiesOfAllCells();
        int recursionDepth = 0;
        while(!isComplete())// && recursionDepth < 4)
            trailAndError(recursionDepth++);
    }

    private void trailAndError(int recursionLevel) throws ErroneousGridException {
        if(recursionLevel == 0)
            while(checkAndAdjustAllCells());
        else
            for(int y = 0; y < gridHeight; ++y)
                for(int x = 0; x < gridWidth; ++x) {
                    if (cells[y][x].getArrow().equals(Arrow.NONE)) {
                        tryTrailAndErrorOnCell(y, x, recursionLevel);
                        while (checkAndAdjustAllCells()) ;
                    }
                }
    }

    private void tryTrailAndErrorOnCell(int y, int x, int aRecursionLevel){
        Arrow[] remainingOptions = cells[y][x].getOptions().toArray(new Arrow[cells[y][x].getOptions().size()]);
        for (Arrow arrow : remainingOptions) {

            Grid trailGrid = new Grid(this);
            trailGrid.setCell(y, x, arrow);
            try{
                trailGrid.trailAndError(aRecursionLevel - 1);
                trailGrid.checkCircularPaths(y, x, gridHeight*gridWidth);
                if(trailGrid.isComplete()) {
                    setCell(y, x, arrow);
                    return;
                }
            }catch (ErroneousGridException e){
                cells[y][x].removeOption(arrow);
            }
        }
    }

    private void checkCircularPaths(int y, int x, int maximumSteps) throws ErroneousGridException {
        if(maximumSteps < 0)
            throw new ErroneousGridException("");
        if(!checkBounds(y, x))
            return;
        switch (cells[y][x].getArrow()){
            case NONE:      return;
            case UP:        checkCircularPaths(y-1, x, maximumSteps-1); return;
            case DOWN:      checkCircularPaths(y+1, x, maximumSteps-1); return;
            case LEFT:      checkCircularPaths(y, x-1, maximumSteps-1); return;
            case RIGHT:     checkCircularPaths(y, x+1, maximumSteps-1); return;
            case CIRCLE:    return;
        }
        throw new UnsupportedOperationException("Unexpected cell state: " + cells[y][x].getArrow());
        }

    /**
     *
     */
    public void updatePossibilitiesOfAllCells(){
        removeOutwardPointingOptionFromGridEdges();
        removeOpposingPointingOptions();
        removeUsedArrowDirections();
    }

    public boolean checkAndAdjustAllCells() throws ErroneousGridException {
        boolean cellChanged = false;

        // check and adjust cells
        for(int y = 0; y < gridHeight; ++y)
            for(int x = 0; x < gridWidth; ++x)
                if(checkAndAdjustCell(y, x))
                    cellChanged = true;

        // check and adjust regions
        /*
        for (main.Region region : regions) {
            main.Cell singleOptionCell = region.sumRemainingOptions();
            if(singleOptionCell != null){
                for(int y = 0; y < gridHeight; ++y)
                    for(int x = 0; x < gridWidth; ++x)
                        if(cells[y][x] == singleOptionCell)
                            System.out.println("cell y" + y + " x" + x + " has only one option remaining");
            }
        }
        */
        return cellChanged;
    }

    /**
     * If Only one Option is remaining. This function locks that Option in.
     * This function then adjusts the options of other cells affected by this change.
     * @param y y-Coordinate of the cell.
     * @param x x-Coordinate of the cell.
     * @return True if an option has been successfully locked in. Otherwise false.
     */
    private boolean checkAndAdjustCell(int y, int x) throws ErroneousGridException {
        if (cells[y][x].getArrow().equals(Arrow.NONE)) {
            Set<Arrow> remainingOptions = cells[y][x].getOptions();
            if (remainingOptions.size() == 0)
                throw new ErroneousGridException("main.Cell y:" + y + " x:" + x + " left without any Options");
            if (remainingOptions.size() == 1) {
                setCell(y, x, getLastRemainingOption(remainingOptions));
                return true;
            }
        }
        return false;
    }

    private void setCell(int y, int x, Arrow aArrow){
        cells[y][x].setArrow(aArrow);
        cells[y][x].getRegion().removeUsedOptionsFromCells();
        removeOpposingPointingOptionFromAdjacentCell(y, x);
    }

    private Arrow getLastRemainingOption(Set<Arrow> remainingOptions) {
        Arrow[] option = new Arrow[remainingOptions.size()];
        option = remainingOptions.toArray(option);
        return option[0];
    }

    /**
     * Cells bordering the Gird may not have arrows pointing out of the grid.
     * This function removes the options from these cells.
     */
    public void removeOutwardPointingOptionFromGridEdges(){
        for(int x = 0; x < gridWidth; ++x){
            cells[0][x].removeOption(Arrow.UP);
            cells[gridHeight-1][x].removeOption(Arrow.DOWN);
        }
        for(int y = 0; y < gridHeight; ++y){
            cells[y][0].removeOption(Arrow.LEFT);
            cells[y][gridWidth-1].removeOption(Arrow.RIGHT);
        }
    }

    /**
     * Cells next to each other may not point at each other.
     * This function removes these options from cells being pointed at by an adjacent cell.
     */
    public void removeOpposingPointingOptions(){
        for(int y = 0; y < gridHeight; ++y)
            for(int x = 0; x < gridWidth; ++x)
                removeOpposingPointingOptionFromAdjacentCell(y, x);
    }

    private void removeOpposingPointingOptionFromAdjacentCell(int y, int x){
        switch (cells[y][x].getArrow()){
            case UP:    if(checkBounds(y, x))
                            cells[y-1][x].removeOption(Arrow.DOWN);
                        break;
            case DOWN:  if(checkBounds(y, x))
                            cells[y+1][x].removeOption(Arrow.UP);
                        break;
            case LEFT:  if(checkBounds(y, x))
                            cells[y][x-1].removeOption(Arrow.RIGHT);
                        break;
            case RIGHT: if(checkBounds(y, x))
                            cells[y][x+1].removeOption(Arrow.LEFT);
                        break;
        }
    }

    private boolean checkBounds(int y, int x){
        if(y < 0 || y >= gridHeight || x < 0 || x >= gridWidth)
            return false;
        return true;
    }

    /**
     * Cells sharing a region can't have the same main.Arrow-Direction.
     * This function removes already used Arrows from the options of the other cell in the region.
     */
    public void removeUsedArrowDirections(){
        for (Region region : regions) {
            region.removeUsedOptionsFromCells();
        }
    }

    public boolean isComplete(){
        return allCellsFilled() && allRegionsValid() && allCellsReachTheDestination();
    }

    public boolean allCellsFilled() {
        for (Cell[] rows : cells)
            for (Cell cell : rows)
                if (cell.getArrow().equals(Arrow.NONE))
                    return false;
        return true;
    }

    public boolean allRegionsValid() {
        for (Region region : regions)
            if (!region.checkCorrectness())
                return false;
        return true;
    }

    public boolean allCellsReachTheDestination() {
        for(int y = 0; y < gridHeight; ++y)
            for(int x = 0; x < gridHeight; ++x)
                if(!cellReachesTheDestination(y, x, gridHeight*gridWidth))
                    return false;
        return true;
    }

    private boolean cellReachesTheDestination(int y, int x, int maximumSteps) {
        if(maximumSteps < 0 || x < 0 || y < 0 || x >= gridWidth || y >= gridHeight)
            return false;
        switch (cells[y][x].getArrow()){
            case NONE:      return false;
            case UP:        return cellReachesTheDestination(y-1, x, maximumSteps-1);
            case DOWN:      return cellReachesTheDestination(y+1, x, maximumSteps-1);
            case LEFT:      return cellReachesTheDestination(y, x-1, maximumSteps-1);
            case RIGHT:     return cellReachesTheDestination(y, x+1, maximumSteps-1);
            case CIRCLE:    return true;
        }
        throw new UnsupportedOperationException("Unexpected cell state: " + cells[y][x].getArrow());
    }

    public void readGridFile(String aFileName) throws FileNotFoundException, MalformedGridException {
        file = readAllLines(aFileName);
        this.gridWidth = calculateGridWidth(file);
        this.gridHeight = calculateGridHeight(file);
        initCells();
        parseAllFields(file);
        calculateRegions(file);
    }

    private void calculateRegions(List<String> lines) {
        connectCellsHorizontally(lines);
        connectCellsVertically(lines);
        createSingleCellRegions();
    }

    private void connectCellsHorizontally(List<String> lines) {
        for(int y = 0; y < this.gridHeight; ++y)
            for (int x = 0; x < this.gridWidth - 1; ++x)
                connectRegions(cells[y][x], cells[y][x + 1], lines.get(y * 2 + 1).charAt((x + 1) * 2));
    }

    private void connectCellsVertically(List<String> lines) {
        for(int x = 0; x < this.getGridWidth(); ++x)
            for (int y = 0; y < this.getGridHeight() - 1; ++y)
                connectRegions(cells[y][x], cells[y + 1][x], lines.get((y + 1) * 2).charAt(x * 2 + 1));
    }

    private void createSingleCellRegions() {
        for(int y = 0; y < this.getGridHeight(); ++y)
            for(int x = 0; x < this.getGridWidth(); ++x)
                if(cells[y][x].getRegion() == null){
                    Region newRegion = new Region();
                    newRegion.addCell(cells[y][x]);
                    this.regions.add(newRegion);
                }
    }

    private void connectRegions(Cell cellA, Cell cellB, char c) {
        if(c == ' '){
            if(cellA.getRegion() == null && cellB.getRegion() == null){
                Region newRegion = new Region();
                newRegion.addCell(cellA);
                newRegion.addCell(cellB);
                this.regions.add(newRegion);
            }else{
                if(cellA.getRegion() == null)
                    cellB.getRegion().addCell(cellA);
                if(cellB.getRegion() == null)
                    cellA.getRegion().addCell(cellB);
            }
            if(cellA.getRegion() != null && cellB.getRegion() != null)
                fuseRegions(cellA.getRegion(), cellB.getRegion());
        }
    }

    private void fuseRegions(Region regionA, Region regionB) {
        if(regionA != regionB) {
            for (Cell cell : regionB.getCells())
                regionA.addCell(cell);
            regionB.getCells().clear();
            this.regions.remove(regionB);
        }
    }

    private void initCells() {
        cells = new Cell[this.gridHeight][this.gridWidth];
    }

    private void parseAllFields(List<String> lines) throws MalformedGridException {
        for(int i = 0; i < lines.size()-1; i+=2)
            parseGridLine(lines.get(i+1), i/2);
    }

    private int calculateGridHeight(List<String> lines) throws MalformedGridException{
        if((lines.size() % 2) == 0)
            throw new MalformedGridException();
        return (lines.size()-1)/2;
    }

    private List<String> readAllLines(String aFileName) throws FileNotFoundException{
        ArrayList<String> stringList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(aFileName), "UTF8"))) {
            String line = br.readLine();
            while(line != null) {
                stringList.add(line);
                line = br.readLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return stringList;
    }

    private int calculateGridWidth(List<String> line) throws MalformedGridException {
        if((line.get(0).length() % 2) == 0)
            throw new MalformedGridException();
        return (line.get(0).length()-1)/2;
    }

    private void parseGridLine(String line, int aHeight) throws MalformedGridException {
        for(int i = 1; i < line.length(); i += 2)
            this.cells[aHeight][(i-1)/2] = parseCell(line.charAt(i));
    }

    private Cell parseCell(char aCharacter) throws MalformedGridException {
        switch (aCharacter) {
            case '↑':
            case 'U':
                return new Cell(Arrow.UP, true);
            case '←':
            case 'L':
                return new Cell(Arrow.LEFT, true);
            case '↓':
            case 'D':
                return new Cell(Arrow.DOWN, true);
            case '→':
            case 'R':
                return new Cell(Arrow.RIGHT, true);
            case 'O':
                return new Cell(Arrow.CIRCLE, true);
            case ' ':
                return new Cell(Arrow.NONE);
        }
        throw new MalformedGridException("Unexpected character in grid: " + aCharacter);
    }

    public int getGridWidth(){
        return this.gridWidth;
    }

    public int getGridHeight(){
        return this.gridHeight;
    }

    public ArrayList<Region> getRegions(){
        return this.regions;
    }

    public Cell getCell(int y, int x){
        return this.cells[y][x];
    }

    public List<String> getFile(){
        return this.file;
    }

    /*
    @Override
    public boolean equals(Object o){
        if(!o.getClass().equals(main.Grid.class))
            return false;
        main.Grid compareGrid = (main.Grid)o;
        if(gridWidth != compareGrid.gridWidth || gridHeight != compareGrid.gridHeight || !Arrays.deepEquals(cells, compareGrid.cells))
            return false;
        if(!regions.equals(compareGrid.regions))
            return false;
        return true;
    }
    */
}
