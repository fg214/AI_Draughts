package com.farah;

import com.farah.controller.GameController;
import com.farah.view.BoardView;
import com.farah.view.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * JavaFX Checkers Application
 */
public class CheckersApplication extends Application {

    // initialise state/javafx visual components
    private final BoardView boardView = new BoardView();
    private final GameView gameView = new GameView(boardView);
    private final GameController gameController = new GameController(gameView);

    @Override
    public void start(Stage stage) {
        var scene = new Scene(gameView, 664, 720, Color.BLACK);

        stage.setTitle("MinMax Checkers Game");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}