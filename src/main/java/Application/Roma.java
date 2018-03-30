package Application;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.Grid;

import java.util.List;

public class Roma extends Application {

    private static Grid romaGrid = new Grid();

    public static void main(String[] args){
        try {
            romaGrid.readGridFile("src/main/resources/roma.txt");
            romaGrid.solve();
        } catch (Exception e) {
            e.printStackTrace();
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane board = createGridDisplac(romaGrid);
        Scene scene = new Scene(board);
        primaryStage.setTitle("Application.Roma");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public GridPane createGridDisplac(Grid grid){
        GridPane board = new GridPane();
        board.setStyle("-fx-padding: 5px;");

        for (int col = 0; col < grid.getGridHeight(); col++) {
            for (int row = 0; row < grid.getGridWidth(); row++) {
                StackPane cell = new StackPane();

                String borders = calculateBorderString(grid.getFile(), col, row);

                cell.setStyle(
                        "-fx-background-color:              black, -fx-base ;" +
                                "-fx-padding:               3px;" +
                                "-fx-background-insets:     0px, " + borders + ";");


                String arrow = grid.getCell(row, col).getArrow().toChar();
                cell.getChildren().add(createTextField(arrow));

                board.add(cell, col, row);
            }
        }
        return board;
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