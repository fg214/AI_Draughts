package com.farah.model;

/**
 * PlayerType enum for different player types.
 * This enum contains helper variables which are used in the boardUtils class to help with move generation/validation
 */
public enum PlayerType {
    HUMAN(-1, -2), COMPUTER(1, 2);

    // as the human player always starts in the bottom half of the grid, its movements are negative in direction
    private final int moveDirection;
    private final int jumpDirection;

    PlayerType(int moveDirection, int jumpDirection) {
        this.moveDirection = moveDirection;
        this.jumpDirection = jumpDirection;
    }

    public int getMoveDirection() {
        return moveDirection;
    }

    public int getJumpDirection() {
        return jumpDirection;
    }
}
