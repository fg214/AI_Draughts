package com.farah.model;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.farah.constants.CheckersConstants.NUM_COLS;
import static com.farah.constants.CheckersConstants.NUM_ROWS;
import static java.util.Objects.*;

/**
 * BoardUtils is a utility class to aid in the execution of game logic such as the minimax algorithm and move validation.
 */
public class BoardUtils {
    // update board with a move
    // only valid moves use this function
    public static void move(Checker[][] board, Move move, PlayerType playerType){
        // check if check should be crowned by regicide, if so, the move is set to a crown move which bubbles to the UI
        if(move.isJumpMove()){
            isRegicideMove(SerializationUtils.clone(board), move);
        }

        // get rid of checkers in legal jump moves
        if (move.getRowDiff() == -2 && move.getColDiff() == 2) board[move.getOldRow() + 1][move.getOldCol() - 1] = null;

        if (move.getRowDiff() == 2 && move.getColDiff() == 2) board[move.getOldRow() - 1][move.getOldCol() - 1] = null;

        if (move.getRowDiff() == 2 && move.getColDiff() == -2) board[move.getOldRow() - 1][move.getOldCol() + 1] = null;

        if (move.getRowDiff() == -2 && move.getColDiff() == -2) board[move.getOldRow() + 1][move.getOldCol() + 1] = null;

        board[move.getNewRow()][move.getNewCol()] = board[move.getOldRow()][move.getOldCol()];
        board[move.getOldRow()][move.getOldCol()] = null;

        // check if checker should be crowned by ascension or if set to crown by regicide check
        if (shouldCrown(move.getNewRow(), playerType) || move.isCrownMove()) board[move.getNewRow()][move.getNewCol()].crown();
    }

    // if checker has reached the opposite last row, crown it or if it has jumped a piece type which is already a king
    public static boolean shouldCrown(int newRow, PlayerType playerType){
        if (newRow == 0 && playerType.equals(PlayerType.HUMAN)) return true;

        return newRow == 7 && playerType.equals(PlayerType.COMPUTER);
    }

    public static boolean isJumpMove(int oldRow, int oldCol, int newRow, int newCol){
        return Math.abs((oldRow - newRow)) == 2 && Math.abs((oldCol - newCol)) == 2;
    }

    // successor function to generate a list of moves given a current checker board. Precedence is given to jump moves when returning the list of possible moves as they are required
    public static List<Move> getAllPossibleMoves(Checker[][] checkersBoard, PlayerType playerType){
        List<Move> possibleMoves = new ArrayList<>();
        List<Move> possibleJumps = new ArrayList<>();

        // assign move direction/magnitude based on the player type to make sure the proposed moves are in the same direction as the moving player.
        final int moveDirection = playerType.getMoveDirection();
        final int jumpDirection = playerType.getJumpDirection();

        for (int row = 0; row < 8; row ++) {
            for (int col = 0; col < 8; col++) {
                if (nonNull(checkersBoard[row][col]) && checkersBoard[row][col].getPlayerType().equals(playerType)) {
                    // these if statements represent the 4 different moves a normal piece can take.
                    if (isValidMove(checkersBoard, row, col, (row + moveDirection), (col + moveDirection), playerType)) {
                        possibleMoves.add(new Move(row, col, (row + moveDirection), (col + moveDirection), false, shouldCrown((row + moveDirection), playerType)));
                    }

                    if (isValidMove(checkersBoard, row, col, (row + moveDirection), (col - moveDirection), playerType)) {
                        possibleMoves.add(new Move(row, col, (row + moveDirection), (col - moveDirection), false, shouldCrown((row + moveDirection), playerType)));
                    }

                    if (isValidJump(checkersBoard, row, col, (row + moveDirection), (col + moveDirection), (row + jumpDirection), (col + jumpDirection), playerType)){
                        possibleJumps.add(new Move(row, col, (row + jumpDirection), (col + jumpDirection), true, shouldCrown((row + jumpDirection), playerType)));
                    }

                    if (isValidJump(checkersBoard, row, col, (row + moveDirection), (col - moveDirection), (row + jumpDirection), (col - jumpDirection), playerType)){
                        possibleJumps.add(new Move(row, col, (row + jumpDirection), (col - jumpDirection), true, shouldCrown((row + jumpDirection), playerType)));
                    }

                    // kings can make all the moves normal pieces can take but can also move backwards
                    if (checkersBoard[row][col].hasCrown()){
                        if (isValidMove(checkersBoard, row, col, (row - moveDirection), (col + moveDirection), playerType)) {
                            possibleMoves.add(new Move(row, col, (row - moveDirection), (col + moveDirection), false, shouldCrown((row - moveDirection), playerType)));
                        }

                        if (isValidMove(checkersBoard, row, col, (row - moveDirection), (col - moveDirection), playerType)) {
                            possibleMoves.add(new Move(row, col, (row - moveDirection), (col - moveDirection), false, shouldCrown((row - moveDirection), playerType)));
                        }

                        if (isValidJump(checkersBoard, row, col, (row - moveDirection), (col - moveDirection), (row - jumpDirection), (col - jumpDirection), playerType)){
                            possibleJumps.add(new Move(row, col, (row - jumpDirection), (col - jumpDirection), true, shouldCrown((row - jumpDirection), playerType)));
                        }

                        if (isValidJump(checkersBoard, row, col, (row - moveDirection), (col + moveDirection), (row - jumpDirection), (col + jumpDirection), playerType)){
                            possibleJumps.add(new Move(row, col, (row - jumpDirection), (col + jumpDirection), true, shouldCrown((row - jumpDirection), playerType)));
                        }
                    }

                }

            }
        }

        // if there are possible jumps return them instead as jumping is mandatory
        return possibleJumps.isEmpty() ? possibleMoves : possibleJumps;
    }

    public static boolean isValidMove(Checker[][] board, int oldRow, int oldCol, int newRow, int newCol, PlayerType playerType){
        // needs to be within board bounds
        if (newRow < 0 || newRow > 7) return false;

        if (newCol < 0 || newCol > 7) return false;

        // the original position should have a checker
        if (isNull(board[oldRow][oldCol])) return false;

        // the new position should not have a checker
        if (nonNull(board[newRow][newCol])) return false;

        // the original position should match the current player type
        if (!board[oldRow][oldCol].getPlayerType().equals(playerType)) return false;

        // if previous checks pass and the new position is empty then it is a valid move
        return isNull(board[newRow][newCol]);
    }

    public static boolean isValidJump(Checker[][] board, int oldRow, int oldCol, int viaRow, int viaCol, int newRow, int newCol, PlayerType playerType){
        // needs to be within board bounds
        if (newRow < 0 || newRow > 7) return false;

        if (newCol < 0 || newCol > 7) return false;

        // capturing moves need to capture a piece in between
        if (isNull(board[viaRow][viaCol])) return false;

        // the captured piece has to be of the opposing type
        if (board[viaRow][viaCol].getPlayerType().equals(playerType)) return false;

        // the new position should not have a checker
        if (nonNull(board[newRow][newCol])) return false;

        // the original position should have a checker
        if (isNull(board[oldRow][oldCol])) return false;

        // lastly the original position should match the current player type
        return board[oldRow][oldCol].getPlayerType().equals(playerType);
    }

    // heuristic function to calculate score need for minimax algorithm
    // This function focuses on
    public static int getBoardStateScore(Checker[][] board, PlayerType playerType) {
        int score = 0;
        int playerPieces = 0;
        int oppPieces = 0;

        for (int row = 0; row < NUM_ROWS; row++){
            for (int col = 0; col < NUM_COLS; col++){
                if (Objects.nonNull(board[row][col])){
                    if (board[row][col].getPlayerType().equals(playerType)){
                        playerPieces++;
                        //prefer kings
                        if (board[row][col].hasCrown()) score += 5;

                        //a pawn is half a king in terms of direction ability
                        if (!board[row][col].hasCrown()) score += 3;

                        // defensive style is preferred
                        if (isAtBase(playerType, row)) score += 1;

                        // pieces on the side cannot be taken
                        if (col == 0 || col == 7) score += 1;

                        // if near opponents base it has a good chance to become king
                        if (isNearOppBase(playerType, row)) score += 1;
                    } else {
                        oppPieces++;
                    }
                }
            }
        }

        return score + (playerPieces - oppPieces);
    }

    // check if player piece is at first row/base row
    private static boolean isAtBase(PlayerType playerType, int row){
        return playerType.equals(PlayerType.COMPUTER) ? row == 0 : row == 7;
    }

    // check if player piece is near the opponents base/close to being crowned
    private static boolean isNearOppBase(PlayerType playerType, int row){
        return playerType.equals(PlayerType.COMPUTER) ? row >= 5 : row <= 3;
    }

    public static int minimax(Checker[][] board, int depth, int maxDepth, int alpha, int beta, boolean isMaxPlayer){
        // if the game is over or the depth limit has been reached, return the score of the board
        if (isGameOver(board, PlayerType.COMPUTER) || depth == maxDepth) {
            return getBoardStateScore(board, PlayerType.COMPUTER);
        }

        // convert the board array to a board state to make use of the getChildren helper function
        var currentBoardState = new BoardState(SerializationUtils.clone(board));

        //Computer is maximising player
        if(isMaxPlayer){
            int maxEval = -1000;

            // get child states for max players turn
            for(BoardState childBoardState: currentBoardState.getChildren(PlayerType.COMPUTER)) {
                var boardEval = minimax(childBoardState.getCheckersBoard(), depth + 1, maxDepth, alpha, beta, false);
                maxEval = Math.max(maxEval, boardEval);
                alpha = Math.max(alpha, maxEval);

                //pruning
                if (alpha >= beta){
                    break;
                }
            }

            // set board value after pruning takes place
            currentBoardState.setValue(maxEval);
            return maxEval;
        }

        //Human is minimising player
        int minEval = 1000;

        // get child states for min players turn
        for(BoardState childBoardState: currentBoardState.getChildren(PlayerType.HUMAN)) {
            var boardEval = minimax(childBoardState.getCheckersBoard(), depth + 1, maxDepth, alpha, beta, true);
            minEval = Math.min(minEval, boardEval);
            beta = Math.min(beta, minEval);

            //pruning
            if (beta <= alpha){
                break;
            }
        }

        // set board value after pruning takes place
        currentBoardState.setValue(minEval);
        return minEval;
    }

    // The game is over if either the current player has no pieces or cannot move
    public static boolean isGameOver(Checker[][] board, PlayerType currentPlayer){
        var currentPlayerPieces = 0;

        for (int row = 0; row < NUM_ROWS; row++){
            for (int col = 0; col < NUM_COLS; col++){
                if (Objects.nonNull(board[row][col])){
                    if (board[row][col].getPlayerType().equals(currentPlayer)){
                        currentPlayerPieces++;
                    }
                }
            }
        }

        if (currentPlayerPieces == 0){
            return true;
        } else {
            //if empty there are no moves left!
            return getAllPossibleMoves(board, currentPlayer).isEmpty();
        }
    }

    public static String getIllegalMoveReason(Checker[][] board, Move move){
        // non playable position
        if ((move.getNewRow() + move.getNewCol()) % 2 != 0) return "Please place piece on playable tile!";

        // if target contains a piece
        if (nonNull(board[move.getNewRow()][move.getNewCol()])) return "Please place piece on tile without a piece!";

        // only kings can move backwards
        if (Math.abs(move.getRowDiff()) > 2 || Math.abs(move.getColDiff()) > 2) return "You cannot move that far!";

        // only diagonal moves are allowed
        if (move.getNewRow() == move.getOldRow() || move.getNewCol() == move.getOldCol()) return "You can only move diagonally!";

        // attempted jumps should capture an opposing piece
        if((Math.abs(move.getRowDiff()) == 2 || Math.abs(move.getColDiff()) == 2) && (isNull(board[move.getViaRow()][move.getViaCol()]) || board[move.getViaRow()][move.getViaCol()].getPlayerType().equals(PlayerType.HUMAN)))  return "Jump moves must have an opposing piece to take in between!";

        // only kings can move backwards
        if (move.getNewRow() > move.getOldRow()) return "Only Kings can move backwards!";

        return "INVALID MOVE - WHAT ARE YOU DOING!?";
    }

    // helper to confirm if move is a regicide move where a king is captured and the capturing piece becomes a king
    public static void isRegicideMove(Checker[][] board, Move move){
        if (move.isJumpMove()){
            // different player types and captured piece is a king, then promote piece to king by setting move as crown move
            if (!board[move.getOldRow()][move.getOldCol()].getPlayerType().equals(board[move.getViaRow()][move.getViaCol()].getPlayerType())){
                if (board[move.getViaRow()][move.getViaCol()].hasCrown()) move.setCrownMove();
            }
        }
    }

}
