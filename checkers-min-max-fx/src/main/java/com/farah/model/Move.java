package com.farah.model;

/**
 * Move class which represents a potential move that can be taken from a source position to a target position
 * This class also automatically assigns the middle row/cols for captured pieces if the move is a jumping move
 * Finally a setter/getter is provided to indicate whether the move results in a crown, happens via ascension or regicide
 */
public class Move {
    private final int oldRow;
    private final int oldCol;
    private final int newRow;
    private final int newCol;
    private final boolean isJumpMove;

    private int viaRow;
    private int viaCol;

    private boolean isCrownMove;

    public Move(int oldRow, int oldCol, int newRow, int newCol, boolean isJumpMove, boolean isCrownMove) {
        this.oldRow = oldRow;
        this.oldCol = oldCol;
        this.newRow = newRow;
        this.newCol = newCol;
        this.isJumpMove = isJumpMove;
        this.isCrownMove = isCrownMove;

        // if the move is a jump move assign the middle row/col as the mid point between source/target rows and cols.
        if (isJumpMove) {
            this.viaRow = (oldRow + newRow) / 2;
            this.viaCol = (oldCol + newCol) / 2;
        }
    }

    // getters and setters

    public int getOldRow() {
        return oldRow;
    }

    public int getOldCol() {
        return oldCol;
    }

    public int getNewRow() {
        return newRow;
    }

    public int getNewCol() {
        return newCol;
    }

    public int getViaRow() {
        return viaRow;
    }

    public int getViaCol() {
        return viaCol;
    }

    public boolean isJumpMove() {
        return isJumpMove;
    }

    public boolean isCrownMove() {
        return isCrownMove;
    }

    public int getRowDiff() {
        return oldRow - newRow;
    }

    public int getColDiff() {
        return oldCol - newCol;
    }

    public void setCrownMove(){ isCrownMove = true;}

    // The equals methods has been overridden to help check for move equality based on the move variables rather than reference location (default), when the list of possible moves is returned
    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }

        if (!(o instanceof Move)){
            return false;
        }

        Move m = (Move) o;

        return oldRow == m.getOldRow() && oldCol == m.getOldCol() && newRow == m.getNewRow() && newCol == m.getNewCol();
    }

}
