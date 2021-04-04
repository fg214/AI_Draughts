package com.farah.model;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * BoardState is a helper class which represents the current state of a board.
 * It contains helper functions used by the minimax algorithm to evaluate potential candidate moves
 */
public class BoardState {
    private final Checker[][] checkersBoard;
    private Move move = null;
    private int value;

    //should be a deep copy of the board coming in
    public BoardState(Checker[][] checkersBoard) {
        this.checkersBoard = checkersBoard;
    }

    // stores the move which is used to generate the current state. Needed to complete a move after the minimax algorithm has been run
    public void setMove(Move move) {
        this.move = move;
    }

    // used to set the heuristic value of the board which is derived by the minimax algorithm;
    public void setValue(int value) {
        this.value = value;
    }

    public Move getMove() {
        return move;
    }

    public int getValue() {
        return value;
    }

    public Checker[][] getCheckersBoard() {
        return this.checkersBoard;
    }

    // main helper function used to derive all the legal possible child board states given the current board state
    public List<BoardState> getChildren(PlayerType playerType) {
        final Checker[][] currentBoard = SerializationUtils.clone(checkersBoard);
        List<BoardState> possibleStates = new ArrayList<>();

        // get list of possible moves via successor function
        List<Move> possibleMoves = new ArrayList<>(BoardUtils.getAllPossibleMoves(currentBoard, playerType));

        // for each of those moves, apply it to a copy of the current board state and add to child board state list
        for (Move move : possibleMoves) {
            var childBoard = SerializationUtils.clone(currentBoard);
            BoardUtils.move(childBoard, move, playerType);
            var childState = new BoardState(childBoard);
            childState.setMove(move);
            possibleStates.add(childState);
        }

        return possibleStates;
    }

    // failed attempt at recursive jumping for moves
//    private List<BoardState> recurseJumps(BoardState originalJumpMoveState, PlayerType playerType) {
//        final Checker[][] board = SerializationUtils.clone(originalJumpMoveState.getCheckersBoard());
//
//        List<Move> possibleMoves = new ArrayList<>(BoardUtils.getAllPossibleMoves(board, playerType));
//
//        var areJumpMoves = possibleMoves.get(0).isJumpMove();
//
//        if (areJumpMoves)
//    }
}
