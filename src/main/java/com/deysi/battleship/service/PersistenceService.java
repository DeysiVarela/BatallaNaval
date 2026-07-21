package com.deysi.battleship.service;

import com.deysi.battleship.model.GameState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

/**
 * File persistence for serialized board state and plain-text metadata.
 */
public class PersistenceService {

    private final Path storageDirectory = Path.of(System.getProperty("user.home"), ".batalla-naval");
    private final Path gameFile = storageDirectory.resolve("game-state.ser");
    private final Path nicknameFile = storageDirectory.resolve("nickname.txt");
    private final Path sunkFile = storageDirectory.resolve("sunk-ships.txt");

    /**
     * Creates a persistence service using the default user-home storage folder.
     */
    public PersistenceService() {
        // Default constructor.
    }

    /**
     * Persists game state plus plain text metadata files.
     *
     * @param gameState state to persist
     */
    public void save(GameState gameState) {
        try {
            // Garantiza carpeta local de trabajo para todos los archivos de persistencia.
            Files.createDirectories(storageDirectory);
            try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(gameFile,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE))) {
                // Estado completo serializado para reanudar partida exacta.
                outputStream.writeObject(gameState);
            }
            // Metadatos planos para trazabilidad rapida sin deserializar.
            Files.writeString(nicknameFile, gameState.getPlayerNickname(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            Files.writeString(sunkFile,
                    "playerSunkShips=" + gameState.getPlayerSunkShips() + System.lineSeparator()
                            + "enemySunkShips=" + gameState.getEnemySunkShips(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save the game.", exception);
        }
    }

    /**
     * Loads the latest serialized game when available.
     *
     * @return optional game state
     */
    public Optional<GameState> loadLatestGame() {
        if (!Files.exists(gameFile)) {
            return Optional.empty();
        }
        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(gameFile))) {
            // Rehidrata el objeto completo del ultimo guardado.
            Object object = inputStream.readObject();
            if (object instanceof GameState gameState) {
                return Optional.of(gameState);
            }
            return Optional.empty();
        } catch (IOException | ClassNotFoundException exception) {
            throw new IllegalStateException("Unable to load the last saved game.", exception);
        }
    }

    /**
     * Returns the path of the serialized game file if it exists.
     *
     * @return optional path to latest save file
     */
    public Optional<Path> getLatestGameFile() {
        return Files.exists(gameFile) ? Optional.of(gameFile) : Optional.empty();
    }

    /**
     * Returns the path to the plain-text nickname file.
     *
     * @return path to plain text nickname file
     */
    public Path getNicknameFile() {
        return nicknameFile;
    }

    /**
     * Returns the path to the plain-text sunk-ships stats file.
     *
     * @return path to plain text sunk-ships stats file
     */
    public Path getSunkFile() {
        return sunkFile;
    }
}
