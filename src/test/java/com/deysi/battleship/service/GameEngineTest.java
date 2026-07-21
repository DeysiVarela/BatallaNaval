package com.deysi.battleship.service;

import com.deysi.battleship.exception.InvalidPlacementException;
import com.deysi.battleship.model.Coordinate;
import com.deysi.battleship.model.GamePhase;
import com.deysi.battleship.model.GameState;
import com.deysi.battleship.model.Orientation;
import com.deysi.battleship.model.Ship;
import com.deysi.battleship.model.ShipType;
import com.deysi.battleship.model.ShotStatus;
import com.deysi.battleship.model.Turn;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameEngineTest {

    @Test
    void playerShotShouldKeepTurnAfterHit() throws Exception {
        GameEngine engine = new GameEngine(new ShipFactory(), board -> new Coordinate(0, 0));
        GameState state = new GameState("Tester", new com.deysi.battleship.model.Board(), new com.deysi.battleship.model.Board());
        state.getEnemyBoard().placeShip(new Ship(ShipType.DESTROYER, new Coordinate(1, 1), Orientation.HORIZONTAL));
        state.setGamePhase(GamePhase.PLAYING);
        state.setCurrentTurn(Turn.PLAYER);

        assertEquals(ShotStatus.HIT, engine.playerShoot(state, new Coordinate(1, 1)).status());
        assertEquals(Turn.PLAYER, state.getCurrentTurn());
    }

    @Test
    void machineTurnShouldThrowWhenGameHasNotStarted() {
        GameEngine engine = new GameEngine(new ShipFactory(), board -> new Coordinate(0, 0));
        GameState state = engine.newGame("Tester");

        assertThrows(IllegalStateException.class, () -> engine.performMachineTurn(state));
    }

    @Test
    void playerShotShouldSwitchTurnAfterWater() {
        GameEngine engine = new GameEngine(new ShipFactory(), board -> new Coordinate(0, 0));
        GameState state = new GameState("Tester", new com.deysi.battleship.model.Board(), new com.deysi.battleship.model.Board());
        state.setGamePhase(GamePhase.PLAYING);
        state.setCurrentTurn(Turn.PLAYER);

        assertEquals(ShotStatus.WATER, engine.playerShoot(state, new Coordinate(0, 0)).status());
        assertEquals(Turn.MACHINE, state.getCurrentTurn());
    }

    @Test
    void playerShotShouldFinishGameWhenLastEnemyShipIsSunk() throws InvalidPlacementException {
        GameEngine engine = new GameEngine(new ShipFactory(), board -> new Coordinate(0, 0));
        GameState state = new GameState("Tester", new com.deysi.battleship.model.Board(), new com.deysi.battleship.model.Board());
        state.getEnemyBoard().placeShip(new Ship(ShipType.FRIGATE, new Coordinate(2, 2), Orientation.HORIZONTAL));
        state.setGamePhase(GamePhase.PLAYING);
        state.setCurrentTurn(Turn.PLAYER);

        assertEquals(ShotStatus.SUNK, engine.playerShoot(state, new Coordinate(2, 2)).status());
        assertTrue(state.isFinished());
        assertTrue(state.isPlayerWinner());
        assertEquals(1, state.getPlayerSunkShips());
    }

    @Test
    void machineTurnShouldReturnTurnToPlayerAfterWaterShot() {
        GameEngine engine = new GameEngine(new ShipFactory(), board -> new Coordinate(4, 4));
        GameState state = new GameState("Tester", new com.deysi.battleship.model.Board(), new com.deysi.battleship.model.Board());
        state.setGamePhase(GamePhase.PLAYING);
        state.setCurrentTurn(Turn.MACHINE);

        assertEquals(ShotStatus.WATER, engine.performMachineTurn(state).status());
        assertEquals(Turn.PLAYER, state.getCurrentTurn());
        assertFalse(state.isFinished());
    }

    @Test
    void machineTurnShouldFinishGameWhenSinkingLastPlayerShip() throws InvalidPlacementException {
        GameEngine engine = new GameEngine(new ShipFactory(), board -> new Coordinate(5, 5));
        GameState state = new GameState("Tester", new com.deysi.battleship.model.Board(), new com.deysi.battleship.model.Board());
        state.getPlayerBoard().placeShip(new Ship(ShipType.FRIGATE, new Coordinate(5, 5), Orientation.HORIZONTAL));
        state.setGamePhase(GamePhase.PLAYING);
        state.setCurrentTurn(Turn.MACHINE);

        assertEquals(ShotStatus.SUNK, engine.performMachineTurn(state).status());
        assertTrue(state.isFinished());
        assertFalse(state.isPlayerWinner());
        assertEquals(1, state.getEnemySunkShips());
    }
}
