package project1;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import project1.finderMap;
import javafx.stage.FileChooser;
import javafx.geometry.Pos;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;

import java.io.File;
import java.nio.file.Files;

/**
 * Project 1: finderMapGUI for solving mazes 
 *
 * @author Isabel Stec 
 * @author Eddy Perez
 * @version 1.0
 */
public class finderMapGUI extends Application {

    //setting up parts needed
    private finderMap maze;
    private GridPane mazeGrid = new GridPane();
    private Label costLabel = new Label("Total Cost: __ ");
    private Label fileLabel = new Label("No file selected.");
    private Label statusLabel = new Label("Choose a maze file.");
    private Button solveButton = new Button("Find Cheapest Route");
    
    /**
     * start sets the stage for the gui, lets users select their maze and solve it
     *@param stage the stage to be shown
     */
    @Override
    public void start(Stage stage) {

        Button loadButton = new Button("Choose File");
        solveButton.setDisable(true);

        mazeGrid.setAlignment(Pos.CENTER);
        mazeGrid.setHgap(2);
        mazeGrid.setVgap(2);

        //loadButton allows user to chose their file and displays it unsolved
        loadButton.setOnAction(evt ->{
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Maze File");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            File file = chooser.showOpenDialog(stage);

            if(file != null){
                try{
                    String mapText = Files.readString(file.toPath());
                    maze = new finderMap(mapText);

                    fileLabel.setText(file.getName());
                    costLabel.setText("Total Cost: __");
                    statusLabel.setText("Maze loaded.");
                    solveButton.setDisable(false);
                    displayMaze(false);
                } catch(Exception except){
                    maze = null;
                    solveButton.setDisable(true);
                    statusLabel.setText("enter loading file: " + except.getMessage());
                }
            }
        });
        //solveButton will actually solve the maze and display the cheapest route
        solveButton.setOnAction(evt -> {
            if (maze == null){
                return;
            }
            if (maze.solve()) {
                displayMaze(true);
                costLabel.setText("Total Cost: " + maze.getTotalCost());
                statusLabel.setText("Cheapest route found!");
            } else {
                statusLabel.setText("No valid route found.");
            }
        });
        //displaying the loadButton (h for horizontal)
        HBox fileControls = new HBox(10, loadButton, fileLabel);
        fileControls.setAlignment(Pos.CENTER_LEFT);

        //displaying the solbe button
        HBox bottomControls = new HBox(20, solveButton, costLabel);
        bottomControls.setAlignment(Pos.CENTER);

        //displaying everything
        VBox root = new VBox(15, new Label("Maze Path Finder"), fileControls, mazeGrid, bottomControls, statusLabel);

        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 640, 480);

        stage.setTitle("Maze Path Finder");
        stage.setScene(scene);
        stage.show();
    }
     /**
     * 
     * displayMaze creates squares per spots of the maze, assigning each one a color dependent on what it is
     * @param showSolution is to signify iif its go time on showing the maze solved
     * 
     */
    private void displayMaze(boolean showSolution){
        mazeGrid.getChildren().clear();
        for(int i = 0; i < maze.map.length; i++){
            for(int j = 0; j < maze.map[i].length; j++){
                String spot = maze.map[i][j];
                Rectangle rectangle = new Rectangle(45, 45);
                Color color = Color.WHITE;

                if(spot.equals("x")){
                    color = Color.DARKGRAY;
                } else if (spot.equals("#")){
                    color = Color.LIGHTCYAN;
                }else if(spot.equals("!")){
                    color = Color.LIGHTBLUE;
                } else if(showSolution && maze.cheapest.route[i][j].matches("[1-9][0-9]*")){
                    color = Color.LIGHTGREEN;
                }
                rectangle.setFill(color);
                rectangle.setStroke(Color.BLACK);

                Label text = new Label(spot);

                if(showSolution && maze.cheapest.route[i][j].matches("[1-9][0-9]*")){
                    text.setText(maze.cheapest.route[i][j]);
                }
                StackPane square = new StackPane(rectangle, text);
                mazeGrid.add(square, j, i);
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }

}
