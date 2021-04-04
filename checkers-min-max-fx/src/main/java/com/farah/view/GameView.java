package com.farah.view;

import javafx.scene.layout.BorderPane;

/**
 * GameView class which extends the BorderPane layout to provide the following features in the relevant locations.
 *
 * Top Position - MenuView - For different menu drop downs
 * Center Position - GameView - Holds the checkers game board
 */
public class GameView extends BorderPane {
    private final BoardView boardView;
    private final MenuView menuView;

    public GameView(BoardView boardView) {
        this.boardView = boardView;

        this.menuView = new MenuView();

        setTop(menuView);
        setCenter(boardView);
    }

    public BoardView getBoardView() {
        return boardView;
    }

    public MenuView getMenuView() {
        return menuView;
    }

}
