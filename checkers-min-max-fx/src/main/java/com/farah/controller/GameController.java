package com.farah.controller;

import com.farah.model.*;
import com.farah.view.GameView;
import com.farah.view.PieceView;
import com.farah.view.PositionView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.apache.commons.lang3.SerializationUtils;

import java.util.Comparator;
import java.util.Objects;

import static com.farah.constants.CheckersConstants.NUM_COLS;
import static com.farah.constants.CheckersConstants.NUM_ROWS;

/**
 * GameController class which is the gateway between the state/game logic and the visual representation of the game.
 * It heavily uses the BoardUtils utility functions to check legal moves, generate all possible legal moves, utilise the
 * minimax algorithm with alpha-beta pruning to power the AI/Computer player, amongst other things. Apart from state this
 * class has references to the visual elements so that stateful changes are seen visually such as piece captures.
 */
public class GameController {

    private final DataFormat pieceViewFormat = new DataFormat("PieceViewFormat");
    public final GameView gameView;

    // These are set to null as they change after every turn depending on the current user. Used for visually moving pieces from A-B
    private PieceView targetPieceView = null;
    private PositionView sourcePosition = null;

    // A reference to the current board state. As object based arrays cannot be copied with the .clone(), a helper function in the apache commons library is used to deep copy state
    public Checker[][] board;

    // A reference to the current player
    private PlayerType currentPlayer;

    // A reference to the current game difficulty, which is used ultimately to dictates the maximum depth of the minimax search algorithm. The higher the depth the more time the AI takes to play.
    private int maxDepth = GameMode.EASY.getMaxDepth();

    public GameController(GameView gameView) {
        this.gameView = gameView;

        // Human player always goes first
        this.currentPlayer = PlayerType.HUMAN;

        initialiseGame();
        setGameViewHandles();

        printStateBoard();
        System.out.println();
        printViewBoard();
    }

    // method to initialise game
    public void initialiseGame() {
        this.board = new Checker[NUM_ROWS][NUM_COLS];

        //set up checker pieces (state)
        for (int row = 0; row < NUM_ROWS; row++){
            for (int col = 0; col < NUM_COLS; col++){
                var isPlayablePosition = (row + col) % 2 == 0;

                var positionView = new PositionView(isPlayablePosition, row, col);

                addPositionViewHandle(positionView);

                // set up checker pieces for board state
                if (isPlayablePosition && (row < 3 || row > 4)){
                    var playerType = row < 3 ? PlayerType.COMPUTER : PlayerType.HUMAN;
                    board[row][col] = new Checker(playerType);
                }

                // set up checker pieces fpr board view
                if (isPlayablePosition && (row < 3 || row > 4)){
                    var isPlayerPiece = row > 3;
                    var pieceView = new PieceView(isPlayerPiece);

                    //only player pieces should be allowed to be moved
                    if (isPlayerPiece){
                        addPieceViewHandle(pieceView);
                    }

                    //addPieceViewHandle(pieceView);
                    positionView.setCurrentPieceView(pieceView);
                }

                //gridPane add method takes col, then row
                gameView.getBoardView().add(positionView, col, row);
            }
        }
    }

    // helper function to print the state representation of the board to the command line
    public void printStateBoard() {
        for (Checker[] x : board)
        {
            for (Checker y : x)
            {
                if (Objects.isNull(y)) {
                    System.out.print("--" + " ");
                } else {
                    System.out.print(y.toString() + " ");
                }
            }
            System.out.println();
        }
    }

    // helper function to print the visual representation of the board to the command line
    public void printViewBoard() {
        var currentIndex = 0;

        for (Node positionView : gameView.getBoardView().getChildren()){
            if (positionView instanceof PositionView){
                var pv = (PositionView) positionView;

                if (currentIndex % 8 == 0) {
                    System.out.println();
                }
                if (pv.getCurrentPieceView().isEmpty()) {
                    System.out.print("--" + " ");
                } else {
                    System.out.print(pv.getCurrentPieceView().get().toString() + " ");
                }

                currentIndex ++;
            }

        }

        System.out.println();
        System.out.println();
    }

    // helper function to add handlers to menuItems such as the difficulty selected and show hints menu.
    public void setGameViewHandles(){
        gameView.getMenuView().getHintMenuItem().setOnAction(e -> {
            if (currentPlayer.equals(PlayerType.HUMAN)){
                if (((CheckMenuItem)e.getSource()).isSelected()){
                    showPossibleUserMoves();
                } else {
                    resetPossibleUserMoves();
                }
            }
        });

        gameView.getMenuView().getEasyMode().setOnAction(e -> maxDepth = GameMode.EASY.getMaxDepth());

        gameView.getMenuView().getMediumMode().setOnAction(e -> maxDepth = GameMode.MEDIUM.getMaxDepth());

        gameView.getMenuView().getHardMode().setOnAction(e -> maxDepth = GameMode.HARD.getMaxDepth());
    }

    // adds handlers to pieceViews to allow dragging. Only human pieceViews are affected.
    public void addPieceViewHandle(PieceView pieceView) {
        pieceView.setOnDragDetected(mouseEvent -> {
            if (currentPlayer.equals(PlayerType.HUMAN)) {
                Dragboard db = pieceView.startDragAndDrop(TransferMode.MOVE);
                SnapshotParameters sp = new SnapshotParameters();
                sp.setFill(Color.TRANSPARENT);
                db.setDragView(pieceView.snapshot(sp, null));
                ClipboardContent cc = new ClipboardContent();
                cc.put(pieceViewFormat, " ");
                db.setContent(cc);
                pieceView.setOpacity(0);
                targetPieceView = pieceView;
                mouseEvent.consume();
            }
        });
    }

    // main handler for dealing with proposed user moves, if the move is valid then it is completed at both the state/visual level.
    // If not a pop up is shown, telling the user that the move is invalid with an explanation why
    public void addPositionViewHandle(PositionView targetPosition) {
        targetPosition.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(pieceViewFormat) && targetPieceView != null) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });

        targetPosition.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            sourcePosition = (PositionView) (targetPieceView.getParent());

            var move = new Move(sourcePosition.getRow(), sourcePosition.getCol(), targetPosition.getRow(), targetPosition.getCol(), BoardUtils.isJumpMove(sourcePosition.getRow(), sourcePosition.getCol(), targetPosition.getRow(), targetPosition.getCol()), BoardUtils.shouldCrown(targetPosition.getRow(), currentPlayer ));
            var possibleMoves = BoardUtils.getAllPossibleMoves(SerializationUtils.clone(board), currentPlayer);

            if (possibleMoves.contains(move)){
                if (db.hasContent(pieceViewFormat)) {
                    //change state first
                    BoardUtils.move(board, move, currentPlayer);

                    // now change view
                    completeMoveView(targetPosition, move);

                    e.setDropCompleted(true);
                } else {
                    e.setDropCompleted(false);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);

                //if there are jump moves available, alert the user, else explain why the move is illegal via the getIllegalMoveReason function
                if (possibleMoves.get(0).isJumpMove()){
                    if (possibleMoves.size() > 1){
                        alert.setContentText("Illegal Move! You must take one of the available jump moves!");
                    } else {
                        alert.setContentText("Illegal Move! You must take the available jump move!");
                    }
                } else {
                    alert.setContentText(String.format("Illegal Move! %s", BoardUtils.getIllegalMoveReason(SerializationUtils.clone(board), move)));
                }

                alert.showAndWait();
            }

            //set back to normal after dragging
            targetPieceView.setOpacity(100);

            targetPieceView = null;
            sourcePosition = null;

            printStateBoard();
            System.out.println();
            printViewBoard();

            // switch player and commence the computer move after a small delay
            if (e.isDropCompleted()){
                new Timeline(new KeyFrame(
                        Duration.millis(10),
                        ae -> {
                            switchCurrentPlayer();
                            new Timeline(
                                    new KeyFrame(
                                            Duration.millis(300),
                                            ae2 -> computerMove()
                                    )
                            ).play();
                        }
                )).play();
            }
        });
    }

    // method to complete AI/computer move after the user has had their turn. As all computer moves are generated, no move validation needs to be done
    private void computerMove(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var currentState = new BoardState(SerializationUtils.clone(board));
        var firstMoves = currentState.getChildren(currentPlayer);

        for (BoardState childState : firstMoves) {
            // AI is always max player. This gets the board evaluations for each possible state up until the max depth using pruning for efficient search
            var score = BoardUtils.minimax(childState.getCheckersBoard(), 0, maxDepth, -1000, 1000,true);
            childState.setValue(score);
        }

        //choose the move with the max score value
        var boardStateOptional = firstMoves.stream().max(Comparator.comparing(BoardState::getValue));

        //If the score value is empty (which it shouldn't be) exit...this is more for testing purposes
        if (boardStateOptional.isEmpty()){
            System.exit(1);
        }

        // get the move which corresponds to the best state
        var move = boardStateOptional.get().getMove();


        //get the source position of the moving checker
        sourcePosition = getPosition(move.getOldRow(), move.getOldCol());

        //again terminate the game if that doesn't exist. for testing!
        if (sourcePosition.getCurrentPieceView().isEmpty()){
            System.exit(1);
        }

        // get the checker currently at the source position
        targetPieceView = sourcePosition.getCurrentPieceView().get();

        //System.out.println("There are " + possibleMoves.size() + " possible moves for the AI");

        //complete the move both at the state/visual level
        BoardUtils.move(board, move, currentPlayer);
        completeMoveView(getPosition(move.getNewRow(), move.getNewCol()), move);

        // reset the targetPiece and source position variables which are used by both the user/ai for visual changes
        targetPieceView = null;
        sourcePosition = null;

        //print state/visual board to see if they match. mainly for testing
        printStateBoard();
        System.out.println();
        printViewBoard();

        switchCurrentPlayer();
    }

    // after a move has been made, if there are any subsequent jump moves, they must be taken!
    // ultimately this method is not used due to multi step moves not being implemented at the state level
    private boolean mustCapture(Checker[][] board, PlayerType playerType){
        var possibleForceCaptures = BoardUtils.getAllPossibleMoves(SerializationUtils.clone(board), currentPlayer);
        return possibleForceCaptures.stream().anyMatch(Move::isJumpMove);
    }


    // helper function to get the positionView of a checker piece
    private PositionView getPosition(int row, int col) {
        int index = ((row * 8) + col) + 1; //add 1 because the first element is a non positionView object (jfx group object)
        return (PositionView) gameView.getBoardView().getChildren().get(index);
    }

    // helper function to visually change the UI to match the new board state
    private void completeMoveView(PositionView targetPosition, Move move) {

        //first remove the visual piece at its old location, then assign it to its target location
        sourcePosition.removeCurrentPieceView();
        targetPosition.setCurrentPieceView(targetPieceView);

        // if the move is a capture move, take the piece at mid section between the source/target locations
        if (move.isJumpMove()){
            capturePieceView(move.getViaRow(), move.getViaCol());
        }

        // if the move is a crowning one, crown the piece
        if(move.isCrownMove()){
            targetPieceView.crown();
        }
    }

    //helper function which uses the pieceView.removerCurrentPieceView method to remove the captured piece located there
    private void capturePieceView(int row, int col) {
        getPosition(row, col).removeCurrentPieceView();
    }

    // helper function to switch the current player after a turn has ended
    private void switchCurrentPlayer(){
        currentPlayer = getOppPlayer(currentPlayer);

        // if hints are enabled then they should only be shown when it is the human player turn!
        if (gameView.getMenuView().getHintMenuItem().isSelected()){
            if (currentPlayer.equals(PlayerType.HUMAN)){
                showPossibleUserMoves();
            } else {
                resetPossibleUserMoves();
            }
        }

        // if the game is over, pop up alert congratulating the winner and exit the game
        if (BoardUtils.isGameOver(SerializationUtils.clone(board), currentPlayer)){
            Alert gameOverAlert = new Alert(Alert.AlertType.CONFIRMATION);
            gameOverAlert.setContentText(String.format("The game is over! \nCongrats to the %s player! \nThe game shall shortly exit!", getOppPlayer(currentPlayer)));

            gameOverAlert.setOnHidden(evt -> System.exit(1));
            gameOverAlert.show();
        }
    }

    // helper function to get the opposite player given the current player
    private PlayerType getOppPlayer(PlayerType currentPlayer){
        return currentPlayer.equals(PlayerType.HUMAN) ? PlayerType.COMPUTER : PlayerType.HUMAN;
    }

    // helper function to show possible human/user based moves. This is done by highlighting the potential positionViews in green.
    public void showPossibleUserMoves(){
        var possibleUserMoves = BoardUtils.getAllPossibleMoves(SerializationUtils.clone(board), PlayerType.HUMAN);
        possibleUserMoves.forEach(move -> {
            PositionView positionView = getPosition(move.getNewRow(), move.getNewCol());
            positionView.showHelp();
        });
    }

    // helper function to reset the highlighted positionViews which represent potential user moves
    public void resetPossibleUserMoves(){
        gameView.getBoardView().getPieceViewList().forEach(PositionView::setStyles);
    }

}
