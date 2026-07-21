package com.deysi.battleship.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Serializable aggregate for the full game state.
 */
public class GameState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Display name of the human player. */
    private final String playerNickname;
    /** Board controlled by the human player. */
    private final Board playerBoard;
    /** Board controlled by the machine. */
    private final Board enemyBoard;
    /** Current lifecycle phase of the match. */
    private GamePhase gamePhase;
    /** Current turn owner. */
    private Turn currentTurn;
    /** Count of enemy ships sunk by player shots. */
    private int playerSunkShips;
    /** Count of player ships sunk by machine shots. */
    private int enemySunkShips;
    /** Winner flag set when match ends. */
    private boolean playerWinner;

    /**
     * Creates a new game aggregate with initial placing phase.
     *
     * @param playerNickname player display name
     * @param playerBoard player board
     * @param enemyBoard enemy board
     */
    public GameState(String playerNickname, Board playerBoard, Board enemyBoard) {
        this.playerNickname = playerNickname;
        this.playerBoard = playerBoard;
        this.enemyBoard = enemyBoard;
        this.gamePhase = GamePhase.PLACING;
        this.currentTurn = Turn.PLAYER;
    }

    /**
     * Returns the player nickname associated with this game.
     *
     * @return player nickname
     */
    public String getPlayerNickname() {
        return playerNickname;
    }

    /**
     * Returns the player's own board.
     *
     * @return player board
     */
    public Board getPlayerBoard() {
        return playerBoard;
    }

    /**
     * Returns the enemy board.
     *
     * @return enemy board
     */
    public Board getEnemyBoard() {
        return enemyBoard;
    }

    /**
     * Returns the current game lifecycle phase.
     *
     * @return current phase
     */
    public GamePhase getGamePhase() {
        return gamePhase;
    }

    /**
     * Updates the game phase.
     *
     * @param gamePhase new phase
     */
    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
    }

    /**
     * Returns whose turn is active.
     *
     * @return active turn
     */
    public Turn getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Updates the active turn.
     *
     * @param currentTurn new turn owner
     */
    public void setCurrentTurn(Turn currentTurn) {
        this.currentTurn = currentTurn;
    }

    /**
     * Returns how many enemy ships were sunk by player shots.
     *
     * @return number of enemy ships sunk by player
     */
    public int getPlayerSunkShips() {
        return playerSunkShips;
    }

    /**
     * Increments player sunk counter by one.
     */
    public void incrementPlayerSunkShips() {
        this.playerSunkShips++;
    }

    /**
     * Returns how many player ships were sunk by machine shots.
     *
     * @return number of player ships sunk by machine
     */
    public int getEnemySunkShips() {
        return enemySunkShips;
    }

    /**
     * Increments machine sunk counter by one.
     */
    public void incrementEnemySunkShips() {
        this.enemySunkShips++;
    }

    /**
     * Returns whether the player is currently marked as winner.
     *
     * @return true when player won the match
     */
    public boolean isPlayerWinner() {
        return playerWinner;
    }

    /**
     * Sets winner flag after game completion.
     *
     * @param playerWinner true when player is winner
     */
    public void setPlayerWinner(boolean playerWinner) {
        this.playerWinner = playerWinner;
    }

    /**
     * Returns whether the game has reached finished phase.
     *
     * @return true when phase is finished
     */
    public boolean isFinished() {
        return gamePhase == GamePhase.FINISHED;
    }
}
