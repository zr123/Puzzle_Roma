package Application;

import exception.ErroneousGridException;
import exception.MalformedGridException;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.Grid;

import java.io.FileNotFoundException;
import java.util.List;

// https://stackoverflow.com/questions/34218434/sudoku-gui-grid-lines#34225599
// https://www.janko.at/Raetsel/Nikoli/index-2.htm
// http://indi.s58.xrea.com/roma/
// http://www.cross-plus-a.com/de/puzzles.htm#Roma

public class Roma extends Application {

    private static Grid romaGrid = new Grid();

    public static void main(String[] args){
        try {
            romaGrid.readGridFile("Sample Fields/rome3.txt");
            romaGrid.solve();
        } catch (Exception e) {
            e.printStackTrace();
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        GridPane board = new GridPane();
        board.setStyle("-fx-padding: 5px;");

        for (int col = 0; col < romaGrid.getGridHeight(); col++) {
            for (int row = 0; row < romaGrid.getGridWidth(); row++) {
                StackPane cell = new StackPane();

                String borders = calculateBorderString(romaGrid.getFile(), col, row);

                cell.setStyle(
                        "-fx-background-color:      black, -fx-base ;" +
                        "-fx-padding:               3px;" +
                        "-fx-background-insets:     0, " + borders + ";");


                String arrow = romaGrid.getCell(row, col).getArrow().toChar();
                cell.getChildren().add(createTextField(arrow));

                board.add(cell, col, row);
            }
        }


        Scene scene = new Scene(board);
        primaryStage.setTitle("Application.Roma");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TextField createTextField(String text) {
        // ➡ ⬅ ⬆ ⬇
        TextField textField = new TextField(text);
        textField.setAlignment(Pos.CENTER);
        textField.setFont(new Font(12));
        textField.setStyle(
                "-fx-font-weight:       bold;" +
                "-fx-pref-width:        3em;" +
                "-fx-pref-height:       3em;");

        textField.setEditable(false);
        return textField ;
    }

    private String calculateBorderString(List<String> romaLines, int col, int row) {
        String borderString = "";
        // border above
        borderString += getBorder(romaLines.get(row*2).charAt(col*2 +1));
        // border right
        borderString += getBorder(romaLines.get(row*2 +1).charAt(col*2 + 2));
        // border below
        borderString += getBorder(romaLines.get(row*2 + 2).charAt(col*2 +1));
        // border left
        borderString += getBorder(romaLines.get(row*2 +1).charAt(col*2));
        return borderString;
    }

    private String getBorder(char borderchar){
        if(borderchar != ' ')
            return "1 ";
        else
            return "0 ";
    }
}