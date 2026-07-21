package com.deysi.battleship.service;

import com.deysi.battleship.model.Board;
import com.deysi.battleship.model.GameState;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceServiceTest {

    @Test
    void saveAndLoadShouldRestoreGameState() {
        PersistenceService persistenceService = new PersistenceService();
        GameState original = new GameState("Tester", new Board(), new Board());

        persistenceService.save(original);

        GameState restored = persistenceService.loadLatestGame().orElseThrow();

        assertEquals("Tester", restored.getPlayerNickname());
    }

    @Test
    void saveShouldCreatePlainTextFiles() throws Exception {
        PersistenceService persistenceService = new PersistenceService();
        GameState state = new GameState("Tester", new Board(), new Board());

        persistenceService.save(state);

        Path nicknameFile = persistenceService.getNicknameFile();
        Path sunkFile = persistenceService.getSunkFile();

        assertTrue(Files.exists(nicknameFile));
        assertTrue(Files.exists(sunkFile));
    }
}
