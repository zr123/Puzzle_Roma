package main;

import exception.ErroneousGridException;
import exception.MalformedGridException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Grid {
    private List<String> file;
    private final int MAX_DEPTH = 5;
    private int gridWidth = 0;
    private int gridHeight = 0;

    private List<Region> regions = new ArrayList<>();
    private Cell[][] cells; // cell[y][x]

    public Grid(){}

    /*
    public Grid(String aFileName) throws IOException, MalformedGridException {
        this. = readGridFile(aFileName);
    }
    */

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
        while(!isComplete() && recursionDepth <= MAX_DEPTH)
            trailAndError(recursionDepth++);
    }

    private void updatePossibilitiesOfAllCells(){
        removeOutwardPointingOptionFromGridEdges();
        removeOpposingPointingOptions();
        removeUsedArrowDirections();
    }

    private void trailAndError(int recursionLevel) throws ErroneousGridException {
        if(recursionLevel == 0)
            lockCellsWithOnlyOneOptionIn();
        else
            for(int y = 0; y < gridHeight; ++y)
                for(int x = 0; x < gridWidth; ++x) {
                    if (cells[y][x].getArrow().equals(Arrow.NONE)) {
                        tryTrailAndErrorOnCell(y, x, recursionLevel);
                        lockCellsWithOnlyOneOptionIn();
                    }
                }
    }

    /**
     * Locks all cells in which have only one option left.
     */
    private void lockCellsWithOnlyOneOptionIn() throws ErroneousGridException {
        for(int y = 0; y < gridHeight; ++y)
            for(int x = 0; x < gridWidth; ++x)
                if(checkCellOptions(y, x)){
                    // set Cell and start over.
                    setCellAndUpdateOptions(y, x, getLastRemainingOption(cells[y][x].getOptions()));
                    y = 0;
                    x = 0;
                }
    }

    private void tryTrailAndErrorOnCell(int y, int x, int aRecursionLevel){
        for (Arrow arrow : cells[y][x].getOptions().toArray(new Arrow[cells[y][x].getOptions().size()])) {
            Grid trailGrid = new Grid(this);
            trailGrid.setCellAndUpdateOptions(y, x, arrow);
            try{
                trailGrid.trailAndError(aRecursionLevel - 1);
                trailGrid.checkCircularPaths(y, x, gridHeight*gridWidth);
                if(trailGrid.isComplete()) {
                    setCellAndUpdateOptions(y, x, arrow);
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
     * This function checks if a cell is unsolved and has only one option remainging.
     * @param y y-Coordinate of the cell.
     * @param x x-Coordinate of the cell.
     * @return True of the Cell has only one Option remaining. Otherwise false.
     * @throws ErroneousGridException If there are no options left the current grid is erroneous.
     */
    private boolean checkCellOptions(int y, int x) throws ErroneousGridException {
        if (cells[y][x].getArrow().equals(Arrow.NONE))
            switch (cells[y][x].getOptions().size()){
                case 0: throw new ErroneousGridException("Cell y:" + y + " x:" + x + " has no options remaining");
                case 1: return true;
            }
        return false;
    }

    private void setCellAndUpdateOptions(int y, int x, Arrow aArrow){
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

    /**
     * The coordinates of a cell may not be negative or greater than the field size.
     * This function verifies this.
     * @param y y-coordinate to be checked
     * @param x x-coordinate to be checked
     * @return True if the coordinate is valid. Otherwise false.
     */
    private boolean checkBounds(int y, int x){
        return y >= 0 && y < gridHeight && x >= 0 && x < gridWidth;
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

    /**
     * Checks if the grid is correctly filled and the destination cell is reached from every cell.
     * @return True if the grid is complete. Otherwise false.
     */
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

    public static Grid readGridFile(String aFileName) throws IOException, MalformedGridException {
        Grid grid = new Grid();
        List<String> file = readAllLines(aFileName);
        grid.file = file;
        grid.gridWidth = calculateGridWidth(file);
        grid.gridHeight = calculateGridHeight(file);
        grid.initCells();
        parseAllFields(grid, file);
        calculateRegions(grid, file);
        return grid;
    }

    private static void calculateRegions(Grid grid, List<String> lines) {
        connectCellsHorizontally(grid, lines);
        connectCellsVertically(grid, lines);
        createSingleCellRegions(grid);
    }

    private static void connectCellsHorizontally(Grid grid, List<String> lines) {
        for(int y = 0; y < grid.gridHeight; ++y)
            for (int x = 0; x < grid.gridWidth - 1; ++x)
                connectRegions(grid, grid.cells[y][x], grid.cells[y][x + 1], lines.get(y * 2 + 1).charAt((x + 1) * 2));
    }

    private static void connectCellsVertically(Grid grid, List<String> lines) {
        for(int x = 0; x < grid.getGridWidth(); ++x)
            for (int y = 0; y < grid.getGridHeight() - 1; ++y)
                connectRegions(grid, grid.cells[y][x], grid.cells[y + 1][x], lines.get((y + 1) * 2).charAt(x * 2 + 1));
    }

    private static void createSingleCellRegions(Grid grid) {
        for(int y = 0; y < grid.getGridHeight(); ++y)
            for(int x = 0; x < grid.getGridWidth(); ++x)
                if(grid.cells[y][x].getRegion() == null){
                    Region newRegion = new Region();
                    newRegion.addCell(grid.cells[y][x]);
                    grid.regions.add(newRegion);
                }
    }

    private static void connectRegions(Grid grid, Cell cellA, Cell cellB, char c) {
        if(c == ' '){
            if(cellA.getRegion() == null && cellB.getRegion() == null){
                Region newRegion = new Region();
                newRegion.addCell(cellA);
                newRegion.addCell(cellB);
                grid.regions.add(newRegion);
            }else{
                if(cellA.getRegion() == null)
                    cellB.getRegion().addCell(cellA);
                if(cellB.getRegion() == null)
                    cellA.getRegion().addCell(cellB);
            }
            if(cellA.getRegion() != null && cellB.getRegion() != null)
                fuseRegions(grid, cellA.getRegion(), cellB.getRegion());
        }
    }

    private static void fuseRegions(Grid grid, Region regionA, Region regionB) {
        if(regionA != regionB) {
            for (Cell cell : regionB.getCells())
                regionA.addCell(cell);
            regionB.getCells().clear();
            grid.regions.remove(regionB);
        }
    }

    private void initCells() {
        cells = new Cell[this.gridHeight][this.gridWidth];
    }

    private static void parseAllFields(Grid grid, List<String> lines) throws MalformedGridException {
        for(int i = 0; i < lines.size()-1; i+=2)
            parseGridLine(grid, lines.get(i+1), i/2);
    }

    private static int calculateGridHeight(List<String> lines) throws MalformedGridException{
        if((lines.size() % 2) == 0)
            throw new MalformedGridException();
        return (lines.size()-1)/2;
    }

    private static List<String> readAllLines(String aFileName) throws IOException {
        ArrayList<String> stringList = new ArrayList<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(aFileName), "UTF8"));
        String line = br.readLine();
        while(line != null) {
            stringList.add(line);
            line = br.readLine();
        }
        return stringList;
    }

    private static int calculateGridWidth(List<String> line) throws MalformedGridException {
        if((line.get(0).length() % 2) == 0)
            throw new MalformedGridException();
        return (line.get(0).length()-1)/2;
    }

    private static void parseGridLine(Grid grid, String line, int aHeight) throws MalformedGridException {
        for(int i = 1; i < line.length(); i += 2)
            grid.cells[aHeight][(i-1)/2] = parseCell(line.charAt(i));
    }

    private static Cell parseCell(char aCharacter) throws MalformedGridException {
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

    public List<Region> getRegions(){
        return this.regions;
    }

    public Cell getCell(int y, int x){
        return this.cells[y][x];
    }

    public List<String> getFile(){
        return this.file;
    }
}
