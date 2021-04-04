package com.farah.view;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.List;
import java.util.stream.Collectors;

import static com.farah.constants.CheckersConstants.NUM_COLS;
import static com.farah.constants.CheckersConstants.NUM_ROWS;

/**
 * BoardView class which extends the GridPane layout to provide a visual representation of the checkers board.
 */
public class BoardView extends GridPane {

    public BoardView(){
        layoutBoard();
    }

    private void layoutBoard() {
        setPrefSize(PositionView.SIZE * NUM_ROWS, PositionView.SIZE * NUM_COLS);

        for(int i = 0; i < NUM_ROWS; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            //rowConstraints.setPercentHeight(496/8);
            rowConstraints.setValignment(VPos.CENTER);
            getRowConstraints().add(rowConstraints);

            ColumnConstraints columnConstraints = new ColumnConstraints();
            //columnConstraints.setPercentWidth(496/8);
            columnConstraints.setHalignment(HPos.CENTER);
            getColumnConstraints().add(columnConstraints);
        }

        setGridLinesVisible(true);
    }

    // get all the current positionView pieces. Used to help reset a positionView if its color has been changed to show a user hint
    public List<PositionView> getPieceViewList(){
        return getChildren().stream()
                .filter(node -> node instanceof PositionView)
                .map(node -> (PositionView) node)
                .collect(Collectors.toList());
    }

}
