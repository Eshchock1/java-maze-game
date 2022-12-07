package screens;

import design.MazeDesignerController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import publish.PublishMazeController;

import solvability.MazeSolvabilityControl;
import solvability.MazeSolvabilityResponseModel;
import java.util.List;

/**
 * The UI for designing a maze.
 */
public class MazeDesignerUI extends Application implements Screen{
    /**
     * The Css file containing visual information.
     */
    String css = this.getClass().getResource("/stylesheet.css").toExternalForm();
    private MazeDesignerController mdc;

    private PublishMazeController mpc;

    private MazeSolvabilityControl msc;

    /**
     * Instantiates a new Maze designer ui.
     *
     * @param mdc the MazeDesignerController
     * @param mpc the PublishMazeController
     * @param msc the MazeSolvabilityControl
     */
    public MazeDesignerUI(MazeDesignerController mdc, PublishMazeController mpc, MazeSolvabilityControl msc) {
        this.mdc = mdc;
        this.mpc = mpc;
        this.msc = msc;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private MazeSolvabilityResponseModel getSolvableStatus() {
        return msc.checkMazeSolvability(mdc.getDm());
    }
    private void updateSolvability(Text solvableIndicator, Button publishButton) {
        MazeSolvabilityResponseModel responseMode = getSolvableStatus();
        solvableIndicator.setText(responseMode.getMessage());
        solvableIndicator.setStyle("-fx-fill: " + responseMode.getMessageColor() + "; -fx-font-size: 14px;");
        publishButton.setDisable(!responseMode.getIsSolvable());
    }

    @Override
    public void start(Stage primaryStage){
        int mazeRows = mdc.getDm().getNumRow();
        int mazeCols = mdc.getDm().getNumCol();

        Button[][] buttonarray = new Button[mazeRows][mazeCols];
        primaryStage.setTitle("Maze Designer");
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);

        ToggleButton builder = new ToggleButton("Build");
        ToggleButton bulldozer = new ToggleButton("Bulldoze");
        ToggleButton starter = new ToggleButton("Place Start");
        ToggleButton ender = new ToggleButton("Place End");

        Button resetter = new Button("Reset");
        resetter.getStyleClass().add("reset-button");

        Button randomizer = new Button("Random");
        randomizer.getStyleClass().add("random-button");

        Button publisher = new Button("Publish");
        publisher.getStyleClass().add("publish-button");

        // log out button
        Button logOutButton = new Button("Log Out");
        logOutButton.getStyleClass().add("log-out-button");
        logOutButton.setLayoutX(100);

        TextField mazeNameField = new TextField("Maze name");
        mazeNameField.getStyleClass().add("maze-name-field");

        HBox topRow = new HBox(mazeNameField, builder, bulldozer, starter, ender, randomizer, publisher, resetter, logOutButton);

        ToggleGroup choices = new ToggleGroup();
        builder.setToggleGroup(choices);
        bulldozer.setToggleGroup(choices);
        starter.setToggleGroup(choices);
        ender.setToggleGroup(choices);
        Popup publishpopup = new Popup();
        Button close = new Button("Close");

        Text solvableIndicator = new Text("This maze is solvable");
        solvableIndicator.setStyle("-fx-fill: #86d154; -fx-font-size: 14px;");


        EventHandler<ActionEvent> eventHandler = (ActionEvent event) -> {
            int row=buttonarray.length;
            int col=buttonarray[0].length;

            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    if (event.getSource()==buttonarray[i][j]){
                        row = i;
                        col = j;
                    }
                }
            }
            if (row == 0 || col == 0 || row == buttonarray.length-1 || col == buttonarray[0].length-1){
                if(event.getSource()!=resetter && event.getSource()!=randomizer) {
                    outerWallEdit(primaryStage);
                }
            } else {
                String handler = "";
                if (builder == choices.getSelectedToggle()) {
                    handler = "build";
                } else if (bulldozer == choices.getSelectedToggle()) {
                    handler = "remove";
                } else if (starter == choices.getSelectedToggle()) {
                    handler = "start";
                } else if (ender == choices.getSelectedToggle()) {
                    handler = "end";
                }
                mdc.handleBuild(handler, row, col);
                updateMazeUI(mdc.getMazeState(), buttonarray);
                updateSolvability(solvableIndicator, publisher);
            }
        };

        EventHandler<ActionEvent> popupHandler = (ActionEvent popuphandle) -> {
            if (popuphandle.getSource()==close){
                publishpopup.hide();
                mdc.resetMaze();
                updateMazeUI(mdc.getMazeState(), buttonarray);
            }
        };

        resetter.setOnAction(actionEvent -> {
            mdc.resetMaze();
            updateSolvability(solvableIndicator, publisher);
            updateMazeUI(mdc.getMazeState(), buttonarray);
        });

        randomizer.setOnAction(actionEvent -> {
            mdc.randoMaze();
            updateMazeUI(mdc.getMazeState(), buttonarray);
        });

        logOutButton.setOnAction(actionEvent -> ScreenManager.changeScreen("login"));

        publisher.setOnAction(actionEvent -> {
            if (getSolvableStatus().getIsSolvable()) {
                List<String> mazeInfo = mpc.publishMaze(UserSingleton.getInstance().getUsername(),
                        mazeNameField.getText(),
                        mdc.getDm());
                Label label = new Label("Your maze " + mazeInfo.get(0) + " has been published!");
                GridPane publishpopuppane = new GridPane();
                publishpopuppane.addRow(0, label);
                publishpopuppane.addRow(1, close);
                publishpopup.getContent().add(publishpopuppane);
                publishpopuppane.setStyle(" -fx-background-color: #CF6679; \n -fx-border-color: black;");
                close.setStyle("-fx-background-color: #BB86FC;");

                publishpopuppane.setMinHeight(100);
                publishpopuppane.setMinWidth(234);
                publishpopuppane.setAlignment(Pos.CENTER);

                label.setMinWidth(80);
                label.setMinHeight(50);

                publishpopup.show(primaryStage);
            }
        });

        close.setOnAction(popupHandler);


        updateMazeUI(mdc.getMazeState(), buttonarray);
        for (int i = 0; i < mazeRows; i++) {
            for (int j = 0; j < mazeCols; j++) {
                buttonarray[i][j].setOnAction(eventHandler);
            }
        }

        root.setVgap(10);
        root.addRow(0, topRow);
        GridPane maze = new GridPane();
        for (int i = 0; i < buttonarray.length; i++) {
            maze.addRow(i, buttonarray[i]);
        }
        root.addRow(1, maze);
        root.addRow(2, solvableIndicator);

        Scene scene = new Scene(root, 1234, 750);
        scene.getStylesheets().add(css);

        // make it so the maze-name-field isn't selected by default
        root.requestFocus();

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }


    /**
     * Creates a popup if the user tries to edit an outer wall.
     *
     * @param primaryStage the primary stage
     */
    public void outerWallEdit(Stage primaryStage){
        Popup wallpopup = new Popup();
        Button close = new Button("Close");
        Label label = new Label("You cannot edit the outermost walls!");
        GridPane popuppane = new GridPane();
        popuppane.addRow(0, label);
        popuppane.addRow(1, close);
        wallpopup.getContent().add(popuppane);
        popuppane.setStyle(" -fx-background-color: #CF6679; \n -fx-border-color: black;");
        close.setStyle("-fx-background-color: #BB86FC;");

        popuppane.setMinHeight(100);
        popuppane.setMinWidth(234);
        popuppane.setAlignment(Pos.CENTER);

        label.setMinWidth(80);
        label.setMinHeight(50);

        EventHandler<ActionEvent> popupHandler = (ActionEvent popuphandle) -> {
            if (popuphandle.getSource() == close) {
                wallpopup.hide();
            }
        };
        close.setOnAction(popupHandler);
        wallpopup.show(primaryStage);
    }
    /**
     * Update the UI.
     *
     * @param mazeState the state of the maze
     * @param buttonarray the buttonarray
     */
    public void updateMazeUI(char[][] mazeState, Button[][] buttonarray){
        for (int i = 0; i < buttonarray.length; i++) {
            for (int j = 0; j < buttonarray[0].length; j++) {

                if (buttonarray[i][j] == null){
                    buttonarray[i][j] = new Button();
                }

                Button button = buttonarray[i][j];

                button.getStyleClass().clear();
                button.getStyleClass().add("maze-button");

                char state = mazeState[i][j];

                if (state == '#'){
                    button.getStyleClass().add("maze-wall");
                } else if (state == '.'){
                    button.getStyleClass().add("maze-empty");
                } else if (state == 'S'){
                    button.getStyleClass().add("maze-start");
                } else if (state == 'E'){
                    button.getStyleClass().add("maze-end");
                }
            }
        }
    }
}

