package com.deysi.battleship.controller;

import com.deysi.battleship.exception.GameStateException;
import com.deysi.battleship.exception.InvalidPlacementException;
import com.deysi.battleship.model.Board;
import com.deysi.battleship.model.Coordinate;
import com.deysi.battleship.model.GamePhase;
import com.deysi.battleship.model.GameState;
import com.deysi.battleship.model.Orientation;
import com.deysi.battleship.model.ShipType;
import com.deysi.battleship.model.ShotResult;
import com.deysi.battleship.model.ShotStatus;
import com.deysi.battleship.service.AutoSaveService;
import com.deysi.battleship.service.GameEngine;
import com.deysi.battleship.service.PersistenceService;
import com.deysi.battleship.service.ShipFactory;
import com.deysi.battleship.view.BoardRenderer;
import com.deysi.battleship.view.PlacementPreview;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main UI controller for the Battleship game.
 */
public class BattleNavalController {

    @FXML
    private GridPane playerBoardContainer;

    @FXML
    private GridPane enemyBoardContainer;

    @FXML
    private ListView<String> fleetListView;

    @FXML
    private Label statusLabel;

    @FXML
    private Label phaseLabel;

    @FXML
    private Label nicknameLabel;

    @FXML
    private Label sunkLabel;

    @FXML
    private ChoiceBox<Orientation> orientationChoiceBox;

    @FXML
    private Button startBattleButton;

    @FXML
    private Button autoPlaceButton;

    @FXML
    private Button newGameButton;

    @FXML
    private Button continueButton;

    @FXML
    private Button toggleEnemyRevealButton;

    @FXML
    private ProgressBar playerProgressBar;

    @FXML
    private Label placementHintLabel;

    private final GameEngine gameEngine = new GameEngine(new ShipFactory());
    private final PersistenceService persistenceService = new PersistenceService();
    private final AutoSaveService autoSaveService = new AutoSaveService(persistenceService);
    private final ExecutorService machineExecutor = Executors.newSingleThreadExecutor();
    private final ObservableList<String> fleetItems = FXCollections.observableArrayList();

    private GameState gameState;
    private ShipType selectedShipType;
    private boolean revealEnemyBoard;
    private boolean verificationModeEnabled;
    private PlacementPreview placementPreview = PlacementPreview.empty();

    /**
     * Creates the main controller for the game scene.
     */
    public BattleNavalController() {
        // Default constructor required by FXMLLoader.
    }

    /**
     * Initializes JavaFX controls and starts autosave tracking.
     */
    @FXML
    public void initialize() {
        orientationChoiceBox.setItems(FXCollections.observableArrayList(Orientation.values()));
        orientationChoiceBox.getSelectionModel().select(Orientation.HORIZONTAL);
        fleetListView.setItems(fleetItems);
        fleetListView.setCellFactory(listView -> createFleetCell());
        fleetListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedShipType = newValue == null ? null : ShipType.fromDisplayText(newValue);
            if (newValue != null) {
                updateStatus("Selected ship: " + newValue);
                updatePlacementHint("Selected: " + newValue + " | Orientation: " + orientationChoiceBox.getValue());
            } else {
                updatePlacementHint("Select a ship to see placement help.");
            }
        });

        newGame();
        autoSaveService.start(this::getCurrentState);
    }

    @FXML
    private void onNewGame() {
        newGame();
    }

    @FXML
    private void onContinueGame() {
        Optional<GameState> restored = persistenceService.loadLatestGame();
        if (restored.isEmpty()) {
            showInformation("No saved game", "There is no saved game to continue.");
            return;
        }
        gameState = restored.get();
        revealEnemyBoard = false;
        verificationModeEnabled = false;
        refreshUi();
        updateStatus("Loaded saved game.");
        updatePlacementHintFromState();
    }

    @FXML
    private void onAutoPlace() {
        requirePlacementPhase();
        gameEngine.autoPlacePlayerFleet(gameState);
        refreshUi();
        updateStatus("Player fleet auto-placed.");
        updatePlacementHint("Fleet auto-placed. You can start the battle.");
    }

    @FXML
    private void onStartBattle() {
        requirePlacementPhase();
        if (!gameState.getPlayerBoard().isFleetComplete()) {
            showWarning("Incomplete fleet", "Place all ships before starting the battle.");
            return;
        }
        gameEngine.startBattle(gameState);
        refreshUi();
        updateStatus("Battle started. Your turn.");
    }

    @FXML
    private void onToggleEnemyReveal() {
        if (!verificationModeEnabled) {
            showInformation("Verification mode", "Enable verification mode with Ctrl+Shift+V to reveal enemy ships.");
            return;
        }
        revealEnemyBoard = !revealEnemyBoard;
        refreshUi();
    }

    @FXML
    private void onOrientationChanged() {
        updateStatus("Orientation: " + orientationChoiceBox.getValue());
        updatePlacementHintFromState();
    }

    @FXML
    private void onPlayerBoardKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.R) {
            orientationChoiceBox.setValue(orientationChoiceBox.getValue().opposite());
            updateStatus("Orientation changed to " + orientationChoiceBox.getValue());
        }
    }

    private void newGame() {
        String nickname = requestNickname().orElse("Player");
        gameState = gameEngine.newGame(nickname);
        revealEnemyBoard = false;
        verificationModeEnabled = false;
        selectedShipType = null;
        placementPreview = PlacementPreview.empty();
        refreshUi();
        updateStatus("New game created for " + nickname + ". Select a ship to place it.");
        updatePlacementHint("Select a ship to see placement help.");
    }

    private Optional<String> requestNickname() {
        TextInputDialog dialog = new TextInputDialog("Player");
        dialog.setTitle("Nickname");
        dialog.setHeaderText("Enter a nickname for the current game");
        dialog.setContentText("Nickname:");
        return dialog.showAndWait().map(text -> text.isBlank() ? "Player" : text.trim());
    }

    private void refreshUi() {
        if (gameState == null) {
            return;
        }

        nicknameLabel.setText("Player: " + gameState.getPlayerNickname());
        sunkLabel.setText("Ships sunk: " + gameState.getPlayerSunkShips() + " / " + gameState.getEnemySunkShips());
        phaseLabel.setText("Phase: " + gameState.getGamePhase().name());
        playerProgressBar.setProgress(gameState.getPlayerBoard().getFleetCompletionRatio());

        playerBoardContainer.getChildren().setAll(renderBoard(gameState.getPlayerBoard(), true, false, true));
        enemyBoardContainer.getChildren().setAll(renderBoard(gameState.getEnemyBoard(), revealEnemyBoard, true));
        fleetItems.setAll(gameEngine.getRemainingFleetText(gameState));
        updatePlacementHintFromState();

        startBattleButton.setDisable(gameState.getGamePhase() != GamePhase.PLACING || !gameState.getPlayerBoard().isFleetComplete());
        autoPlaceButton.setDisable(gameState.getGamePhase() != GamePhase.PLACING);
        toggleEnemyRevealButton.setDisable(!verificationModeEnabled);
        toggleEnemyRevealButton.setText(verificationModeEnabled ? "Toggle Enemy Reveal" : "Reveal Locked");
        continueButton.setDisable(persistenceService.getLatestGameFile().isEmpty());
    }

    private Node renderBoard(Board board, boolean revealShips, boolean allowShot) {
        return renderBoard(board, revealShips, allowShot, false);
    }

    private Node renderBoard(Board board, boolean revealShips, boolean allowShot, boolean allowDrop) {
        boolean dropEnabled = allowDrop && board == gameState.getPlayerBoard() && gameState.getGamePhase() == GamePhase.PLACING;
        PlacementPreview preview = board == gameState.getPlayerBoard() ? placementPreview : PlacementPreview.empty();
        return BoardRenderer.render(
                board,
                revealShips,
                allowShot,
                dropEnabled,
                preview,
                coordinate -> onBoardCellClicked(board, coordinate),
                new BoardPlacementDragAdapter() {
                    @Override
                    public void onDragEntered(Coordinate coordinate) {
                        handlePlacementDragEntered(coordinate);
                    }

                    @Override
                    public void onDragExited(Coordinate coordinate) {
                        handlePlacementDragExited(coordinate);
                    }

                    @Override
                    public void onDrop(Coordinate coordinate) {
                        handlePlacementDrop(coordinate);
                    }
                }
        );
    }

    private void refreshPlayerBoard() {
        if (gameState == null) {
            return;
        }
        playerBoardContainer.getChildren().setAll(renderBoard(gameState.getPlayerBoard(), true, false, true));
    }

    private void onBoardCellClicked(Board board, Coordinate coordinate) {
        if (gameState == null) {
            return;
        }

        try {
            if (gameState.getGamePhase() == GamePhase.PLACING && board == gameState.getPlayerBoard()) {
                if (selectedShipType == null) {
                    showWarning("Select a ship", "Choose a ship from the list before placing it.");
                    return;
                }
                gameEngine.placePlayerShip(gameState, selectedShipType, coordinate, orientationChoiceBox.getValue());
                selectedShipType = null;
                fleetListView.getSelectionModel().clearSelection();
                refreshUi();
                saveCurrentState();
                return;
            }

            if (gameState.getGamePhase() == GamePhase.PLAYING && board == gameState.getEnemyBoard()) {
                handlePlayerShot(coordinate);
            }
        } catch (InvalidPlacementException | GameStateException ex) {
            showWarning("Action rejected", ex.getMessage());
        }
    }

    private void handlePlacementDragEntered(Coordinate coordinate) {
        if (gameState == null || gameState.getGamePhase() != GamePhase.PLACING || selectedShipType == null) {
            return;
        }

        PlacementPreview preview = buildPlacementPreview(coordinate);
        if (!preview.equals(placementPreview)) {
            placementPreview = preview;
            refreshPlayerBoard();
        }

        updateStatus(preview.valid()
                ? "Drop to place " + selectedShipType.displayName() + "."
                : "This location is not valid for " + selectedShipType.displayName() + ".");
        updatePlacementHint(preview.valid()
            ? "Valid drop at " + formatCoordinate(coordinate) + "."
            : "Invalid drop at " + formatCoordinate(coordinate) + ".");
    }

    private void handlePlacementDragExited(Coordinate coordinate) {
        if (gameState == null || gameState.getGamePhase() != GamePhase.PLACING) {
            return;
        }

        placementPreview = PlacementPreview.empty();
        refreshPlayerBoard();
        updatePlacementHintFromState();
    }

    private void handlePlacementDrop(Coordinate coordinate) {
        if (gameState == null || gameState.getGamePhase() != GamePhase.PLACING || selectedShipType == null) {
            return;
        }

        PlacementPreview preview = buildPlacementPreview(coordinate);
        placementPreview = preview;
        refreshPlayerBoard();

        if (preview.valid()) {
            try {
                gameEngine.placePlayerShip(gameState, selectedShipType, coordinate, orientationChoiceBox.getValue());
                selectedShipType = null;
                fleetListView.getSelectionModel().clearSelection();
                placementPreview = PlacementPreview.empty();
                refreshUi();
                saveCurrentState();
                updatePlacementHint("Ship placed successfully.");
            } catch (InvalidPlacementException exception) {
                showWarning("Action rejected", exception.getMessage());
            }
            return;
        }

        showWarning("Invalid placement", "The ship cannot be placed on the highlighted cells.");
    }

    private PlacementPreview buildPlacementPreview(Coordinate origin) {
        if (selectedShipType == null) {
            return PlacementPreview.empty();
        }

        Set<Coordinate> previewCoordinates = new HashSet<>();
        int shipSize = selectedShipType.size();
        Orientation orientation = orientationChoiceBox.getValue();

        for (int index = 0; index < shipSize; index++) {
            int row = orientation == Orientation.HORIZONTAL ? origin.row() : origin.row() + index;
            int col = orientation == Orientation.HORIZONTAL ? origin.col() + index : origin.col();
            Coordinate coordinate = new Coordinate(row, col);
            previewCoordinates.add(coordinate);
        }

        boolean valid = true;
        for (Coordinate coordinate : previewCoordinates) {
            if (!coordinate.isInsideBoard() || gameState.getPlayerBoard().getCell(coordinate).hasShip()) {
                valid = false;
                break;
            }
        }

        return new PlacementPreview(previewCoordinates, valid);
    }

    private void handlePlayerShot(Coordinate coordinate) {
        ShotResult playerShot = gameEngine.playerShoot(gameState, coordinate);
        updateStatus("Player shot: " + playerShot.status().name());
        refreshUi();
        saveCurrentState();

        if (gameState.isFinished()) {
            onGameFinished();
            return;
        }

        if (playerShot.status() == ShotStatus.WATER) {
            triggerMachineTurn();
        }
    }

    private void triggerMachineTurn() {
        machineExecutor.submit(() -> {
            try {
                Thread.sleep(700L);
                ShotResult shotResult = gameEngine.performMachineTurn(gameState);
                Platform.runLater(() -> {
                    updateStatus("Machine shot: " + shotResult.status().name());
                    refreshUi();
                    saveCurrentState();
                    if (gameState.isFinished()) {
                        onGameFinished();
                    }
                });
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void onGameFinished() {
        showInformation("Game over", gameState.isPlayerWinner() ? "You won the battle." : "The machine won the battle.");
        gameState.setGamePhase(GamePhase.FINISHED);
        refreshUi();
        saveCurrentState();
    }

    private void saveCurrentState() {
        if (gameState != null) {
            persistenceService.save(gameState);
        }
    }

    private GameState getCurrentState() {
        return gameState;
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void requirePlacementPhase() {
        if (gameState.getGamePhase() != GamePhase.PLACING) {
            throw new GameStateException("This action is only allowed while placing ships.");
        }
    }

    @FXML
    private void onSceneReady() {
        Scene scene = playerBoardContainer.getScene();
        if (scene != null) {
            scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleGlobalKeyPressed);
        }
    }

    private void handleGlobalKeyPressed(KeyEvent event) {
        if (event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.V) {
            verificationModeEnabled = !verificationModeEnabled;
            if (!verificationModeEnabled) {
                revealEnemyBoard = false;
            }
            refreshUi();
            updateStatus(verificationModeEnabled
                    ? "Verification mode enabled. Enemy reveal unlocked."
                    : "Verification mode disabled. Enemy reveal locked.");
        }
        if (event.isControlDown() && event.getCode() == KeyCode.S) {
            saveCurrentState();
            updateStatus("Game saved.");
        }
        if (event.isControlDown() && event.getCode() == KeyCode.L) {
            onContinueGame();
        }
    }

    private void updatePlacementHintFromState() {
        if (gameState == null) {
            return;
        }

        if (gameState.getGamePhase() != GamePhase.PLACING) {
            updatePlacementHint("Battle in progress. Use the enemy board to shoot.");
            return;
        }

        if (selectedShipType == null) {
            updatePlacementHint("Select a ship to see placement help.");
            return;
        }

        updatePlacementHint("Selected: " + selectedShipType.displayName() + " | Orientation: " + orientationChoiceBox.getValue());
    }

    private void updatePlacementHint(String message) {
        if (placementHintLabel != null) {
            placementHintLabel.setText(message);
        }
    }

    private String formatCoordinate(Coordinate coordinate) {
        return String.valueOf((char) ('A' + coordinate.col())) + (coordinate.row() + 1);
    }

    private ListCell<String> createFleetCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }

            {
                setOnDragDetected(event -> {
                    String item = getItem();
                    if (item == null || item.isBlank() || gameState == null || gameState.getGamePhase() != GamePhase.PLACING) {
                        return;
                    }

                    selectedShipType = ShipType.fromDisplayText(item);
                    Dragboard dragboard = startDragAndDrop(TransferMode.COPY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(item);
                    dragboard.setContent(content);
                    updateStatus("Dragging ship: " + item);
                    event.consume();
                });

                setOnDragDone(event -> {
                    if (event.isDropCompleted()) {
                        fleetListView.getSelectionModel().clearSelection();
                    }
                    event.consume();
                });
            }
        };
    }
}
