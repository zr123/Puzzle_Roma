package Application;

import exception.ErroneousGridException;
import exception.MalformedGridException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Grid;

import java.awt.peer.ButtonPeer;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Roma extends Application {

    private static Grid romaGrid;
    private static GridPane romaGridPane = new GridPane();

    public static void main(String[] args) throws IOException, MalformedGridException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane rootPane = new GridPane();
        rootPane.add(romaGridPane, 0, 0);
        rootPane.add(createButtonGridPane(primaryStage), 0, 1);
        Scene scene = new Scene(rootPane);
        primaryStage.setWidth(500);
        primaryStage.setHeight(500);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Roma");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public GridPane createButtonGridPane(Stage primaryStage){
        GridPane buttonPane = new GridPane();
        buttonPane.setHgap(10);
        buttonPane.setVgap(10);
        buttonPane.setPadding(new Insets(5, 5, 5, 5));
        buttonPane.add(createFileChooserButton(primaryStage), 0, 0);
        buttonPane.add(createRomaSolveButton(), 1, 0);
        return buttonPane;
    }

    public Button createFileChooserButton(Stage stage){
        final Button fileButton = new Button("Load a Roma File ...");
        fileButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        File file = getRomaFileChooser().showOpenDialog(stage);
                        if(file != null) {
                            romaGrid = new Grid();
                            try {
                                romaGrid.readGridFile(file.getAbsolutePath());
                            } catch (IOException | MalformedGridException e) {
                                e.printStackTrace();
                            }
                            reloadRomaGridDisplay();
                        }
                    }
                });
        return fileButton;
    }

    public Button createRomaSolveButton(){
        final Button solveButton = new Button("Solve");
        solveButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if(romaGrid != null) {
                            try {
                                romaGrid.solve();
                            } catch (ErroneousGridException e) {
                                e.printStackTrace();
                            }
                        }
                        reloadRomaGridDisplay();
                    }
                });
        return solveButton;
    }

    public FileChooser getRomaFileChooser(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Roma Files", "*.roma"));
        fileChooser.setTitle("open Roma File ...");
        return fileChooser;
    }

    public void reloadRomaGridDisplay(){
        romaGridPane.getChildren().clear();
        romaGridPane.setStyle("-fx-padding: 5px;");

        for (int col = 0; col < romaGrid.getGridHeight(); col++) {
            for (int row = 0; row < romaGrid.getGridWidth(); row++) {
                StackPane cell = new StackPane();

                String borders = calculateBorderString(romaGrid.getFile(), col, row);

                cell.setStyle(
                        "-fx-background-color:              black, -fx-base ;" +
                                "-fx-padding:               3px;" +
                                "-fx-background-insets:     0px, " + borders + ";");


                String arrow = romaGrid.getCell(row, col).getArrow().toChar();
                cell.getChildren().add(createTextField(arrow));

                romaGridPane.add(cell, col, row);
            }
        }
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