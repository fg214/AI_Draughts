package com.farah.view;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.Objects;
import java.util.Optional;

/**
 * PositionView class which provides the visual representation of a x, y position on a checkers board. This class provides
 * helper functions to set/remove a PieceView which may be stored there.
 */
public class PositionView extends StackPane {
    public static final double SIZE = 100;

    private final boolean isPlayablePosition;
    private final int row;
    private final int col;

    private PieceView currentPieceView = null;

    public PositionView(boolean isPlayablePosition, int row, int col) {
        this.isPlayablePosition = isPlayablePosition;
        this.row = row;
        this.col = col;
        setStyles();
        setPrefSize(SIZE, SIZE);
        //setMinSize(SIZE, SIZE);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    // set positionView colors based on if the x,y coordinates are playable ones
    public void setStyles() {
        Color color = isPlayablePosition ? Color.SADDLEBROWN : Color.TAN;
        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        setOpacity(100);
    }

    // use to highlight that this position is a potential move choice for a user
    public void showHelp() {
        setBackground(new Background(new BackgroundFill(Color.GREENYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        setOpacity(70);
    }

    // used to set the current PieceView which occupies this position
    public void setCurrentPieceView(PieceView currentPieceView) {
        this.currentPieceView = currentPieceView;

        if (!getChildren().isEmpty()){
            getChildren().remove(0);
        }

        getChildren().add(currentPieceView);
    }

    // remove current PieceView if one exists there
    public void removeCurrentPieceView() {
        if (Objects.nonNull(currentPieceView)){
            getChildren().remove(0);
        }
        currentPieceView = null;
    }

    // get current PieceView if one exists there
    public Optional<PieceView> getCurrentPieceView() {
        return Optional.ofNullable(currentPieceView);
    }
}
