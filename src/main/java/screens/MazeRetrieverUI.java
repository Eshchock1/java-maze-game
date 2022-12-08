package screens;

import display.MazeDisplayController;
import display.MazeDisplayResponseModel;
import entities.PublishedMaze;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import retrieval.MazeRetrieverController;
import retrieval.MazeRetrieverResponseModel;

import java.util.ArrayList;

/**
 * The view for the maze retriever use case.
 *
 * @author Oscar Tuvey
 */
public class MazeRetrieverUI extends Application implements Screen {
    String css = this.getClass().getResource("/stylesheet.css").toExternalForm();

    private final MazeRetrieverController retrieverController;
    private final MazeDisplayController displayController;

    /**
     * The constructor for the MazeRetrieverView class.
     *
     * @param retrieverController the controller for the maze retriever use case.
     * @param displayController the controller for the maze display use case.
     */
    public MazeRetrieverUI(MazeRetrieverController retrieverController, MazeDisplayController displayController) {
        this.retrieverController = retrieverController;
        this.displayController = displayController;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Menu");
        UserSingleton singleton = UserSingleton.getInstance();


        MazeRetrieverResponseModel retrieverRespModel = retrieverController.create(singleton.getUsername());
        ArrayList<PublishedMaze> played;
        ArrayList<PublishedMaze> notPlayed;
        try {
            played = (ArrayList<PublishedMaze>) retrieverRespModel.getPlayed();
        } catch (ClassCastException e) {
            played = new ArrayList<>();
        }
        try {
            notPlayed = (ArrayList<PublishedMaze>) retrieverRespModel.getNotPlayed();
        } catch (ClassCastException e) {
            notPlayed = new ArrayList<>();
        }

        HBox playedHBox = new HBox();
        HBox notPlayedHBox = new HBox();

        Button[] buttonsPlayed;
        try {
            buttonsPlayed = new Button[played.size()];
        } catch (NullPointerException e){
            buttonsPlayed = new Button[0];
        }
        Button[] buttonsNotPlayed = new Button[notPlayed.size()];

        for (int i = 0; i < played.size(); i++) {
            PublishedMaze publishedMaze = played.get(i);
            String buttonText = publishedMaze.getId() + ", " + publishedMaze.getName() + ", " +
                    publishedMaze.getAuthor();
            buttonsPlayed[i] = new Button(buttonText);
            buttonsPlayed[i].setText(buttonText);
            buttonsPlayed[i].setOnAction(event -> {
                Button button = (Button) event.getSource();
                String[] buttonString = button.getText().split(",");
                String mazeId = buttonString[0].trim();
                MazeDisplayResponseModel respModel =
                        displayController.create(singleton.getUsername(), Integer.parseInt(mazeId));
                MazeSingleton maze = MazeSingleton.getInstance();
                maze.setMaze(respModel.getMaze());
                ScreenManager.changeScreen("game");
            });
            playedHBox.getChildren().add(buttonsPlayed[i]);
        }

        // We decided to keep this warning as it aided with the readability of the code
        for (int i = 0; i < notPlayed.size(); i++) {
            PublishedMaze publishedMaze = notPlayed.get(i);
            String buttonText = publishedMaze.getId() + ", " + publishedMaze.getName() + ", " +
                    publishedMaze.getAuthor();
            buttonsNotPlayed[i] = new Button(buttonText);
            buttonsNotPlayed[i].setText(buttonText);
            buttonsNotPlayed[i].setOnAction(event -> {
                Button button = (Button) event.getSource();
                String[] buttonString = button.getText().split(",");
                String mazeId = buttonString[0].trim();
                System.out.println(mazeId);
                MazeDisplayResponseModel respModel =
                        displayController.create(singleton.getUsername(), Integer.parseInt(mazeId));
                MazeSingleton maze = MazeSingleton.getInstance();
                maze.setMaze(respModel.getMaze());
                ScreenManager.changeScreen("game");

            });
            notPlayedHBox.getChildren().add(buttonsNotPlayed[i]);
        }


        GridPane root = new GridPane();

        root.setAlignment(Pos.CENTER);

        root.setVgap(10);

        root.addRow(0, playedHBox);

        root.addRow(1, notPlayedHBox);

        Scene scene = new Scene(root, 1234, 750);

        primaryStage.setScene(scene);
        scene.getStylesheets().add(css);
        primaryStage.setMaximized(true);
        primaryStage.show();


    }
}
