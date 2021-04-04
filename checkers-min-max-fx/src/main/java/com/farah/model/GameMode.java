package com.farah.model;

/**
 * GameMode enum, which signifies the current difficulty of the game,
 * There are three modes; each with a corresponding maxDepth to be used in the minimax algorithm
 * (with the exception if easy which is just used to pick a random move)
 */
public enum GameMode {
    EASY("Easy", 2),
    MEDIUM("Medium", 5),
    HARD("Hard", 8);

    private final String gameMode;
    private final int maxDepth;

    GameMode(String gameMode, int maxDepth){
        this.gameMode = gameMode;
        this.maxDepth = maxDepth;
    }

    public String getGameMode(){
        return gameMode;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
