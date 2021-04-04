package com.farah.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * PieceView class which provides the visual representation of a checker piece on a checkers board. This class provides a
 * helper functions visually change a piece if it has been crowned in the underlying state representation.
 */
public class PieceView extends Circle {

    private final boolean isPlayerPiece;

    private boolean isKing;

    public PieceView(boolean isPlayerPiece) {
        this.isPlayerPiece = isPlayerPiece;
        this.isKing = false;

        setFill(getFillColor());
        setRadius(40);
        setStroke(getStrokeColor());
        setStrokeWidth(3);
    }

    // Add golden stroke to pieceView to show that it is now a king
    public void crown() {
        isKing = true;
        setStroke(Color.GOLD);
    }

    public boolean isKing() {
        return isKing;
    }

    private Color getFillColor(){
        return isPlayerPiece ? Color.BROWN : Color.BISQUE;
    }

    private Color getStrokeColor(){
        return isPlayerPiece ? Color.MAROON : Color.TAN;
    }

    public boolean isPlayerPiece() {
        return isPlayerPiece;
    }

    @Override
    public String toString(){
        if (isKing){
            return isPlayerPiece ? "CHP" : "CCP";
        }
        return isPlayerPiece ? "HP" : "CP";
    }
}
