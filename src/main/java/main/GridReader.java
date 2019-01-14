package main;

import exception.MalformedGridException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GridReader {

    public static Grid readGridFile(String aFileName) throws IOException, MalformedGridException {
        List<String> file = readAllLines(aFileName);
        Grid grid = new Grid(calculateGridWidth(file), calculateGridHeight(file));
        grid.file = file;
        grid.initCells();
        parseAllFields(grid, file);
        calculateRegions(grid, file);
        return grid;
    }

    private static int calculateGridWidth(List<String> line) throws MalformedGridException {
        if((line.get(0).length() % 2) == 0)
            throw new MalformedGridException();
        return (line.get(0).length()-1)/2;
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

    private static void calculateRegions(Grid grid, List<String> lines) {
        connectCellsHorizontally(grid, lines);
        connectCellsVertically(grid, lines);
        createSingleCellRegions(grid);
    }

    private static void connectCellsHorizontally(Grid grid, List<String> lines) {
        for(int y = 0; y < grid.getGridHeight(); ++y)
            for (int x = 0; x < grid.getGridWidth() - 1; ++x)
                connectRegions(grid, grid.getCell(y, x), grid.getCell(y, x + 1), lines.get(y * 2 + 1).charAt((x + 1) * 2));
    }

    private static void connectCellsVertically(Grid grid, List<String> lines) {
        for(int x = 0; x < grid.getGridWidth(); ++x)
            for (int y = 0; y < grid.getGridHeight() - 1; ++y)
                connectRegions(grid, grid.getCell(y, x), grid.getCell(y + 1, x), lines.get((y + 1) * 2).charAt(x * 2 + 1));
    }

    private static void createSingleCellRegions(Grid grid) {
        for(Cell[] row : grid.getCells())
            for(Cell cell : row)
                if(cell.getRegion() == null){
                    Region newRegion = new Region();
                    newRegion.addCell(cell);
                    grid.getRegions().add(newRegion);
                }
    }

    private static void connectRegions(Grid grid, Cell cellA, Cell cellB, char c) {
        if(c == ' '){
            if(cellA.getRegion() == null && cellB.getRegion() == null){
                Region newRegion = new Region();
                newRegion.addCell(cellA);
                newRegion.addCell(cellB);
                grid.getRegions().add(newRegion);
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
            grid.getRegions().remove(regionB);
        }
    }

    private static void parseAllFields(Grid grid, List<String> lines) throws MalformedGridException {
        for(int i = 0; i < lines.size()-1; i+=2)
            parseGridLine(grid, lines.get(i+1), i/2);
    }

    private static void parseGridLine(Grid grid, String line, int aHeight) throws MalformedGridException {
        for(int i = 1; i < line.length(); i += 2)
            grid.setCell(aHeight, (i-1)/2, parseCell(line.charAt(i)));
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
}
