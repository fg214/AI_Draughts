package com.farah.model;

import java.io.Serializable;

/**
 * Checker class is the state representation of a checker piece. The class stores the playerType and has a help function
 * indicate whether the piece has been crowned or not. This class also implements Serializable to allow for deep copying
 * of board states which are 2D Checker arrays.
 */
public class Checker implements Serializable {
    private boolean hasCrown;
    private final PlayerType playerType;

    public Checker(PlayerType playerType) {
        this.playerType = playerType;
        this.hasCrown = false;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public boolean hasCrown() {
        return hasCrown;
    }

    public void crown() {
        hasCrown = true;
    }

    @Override
    public String toString() {
        if (hasCrown){
            return playerType.equals(PlayerType.HUMAN) ? "CHP" : "CCP";
        }
        return playerType.equals(PlayerType.HUMAN) ? "HP" : "CP";
    }
}
