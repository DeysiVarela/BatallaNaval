package com.deysi.battleship.view;

import com.deysi.battleship.model.Board;
import com.deysi.battleship.model.Cell;
import com.deysi.battleship.model.Coordinate;
import com.deysi.battleship.model.Orientation;
import com.deysi.battleship.model.Ship;
import com.deysi.battleship.model.ShipType;
import com.deysi.battleship.model.ShotStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.function.Consumer;

/**
 * Renders a board using JavaFX shapes.
 */
public final class BoardRenderer {

    private static final double CELL_SIZE = 47;
    private static final double SHIP_PADDING = 4;
    private static final Image CARRIER_NORMAL = loadImage("/ships/carrier_normal.png");
    private static final Image SUBMARINE_NORMAL = loadImage("/ships/submarine_normal.png");
    private static final Image DESTROYER_NORMAL = loadImage("/ships/destroyer_normal.png");
    private static final Image FRIGATE_NORMAL = loadImage("/ships/frigate_normal.png");

    private static final Image CARRIER_BURNING = loadImage("/ships/carrier_burning.png");
    private static final Image SUBMARINE_BURNING = loadImage("/ships/submarine_burning.png");
    private static final Image DESTROYER_BURNING = loadImage("/ships/destroyer_burning.png");
    private static final Image FRIGATE_BURNING = loadImage("/ships/frigate_burning.png");

    private BoardRenderer() {
    }

    public interface PlacementDragListener {

        void onDragEntered(Coordinate coordinate);

        void onDragExited(Coordinate coordinate);

        void onDrop(Coordinate coordinate);
    }

    public static GridPane render(Board board, boolean revealShips, boolean allowShot, boolean allowDrop, PlacementPreview preview, Consumer<Coordinate> clickConsumer, PlacementDragListener dragListener) {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("battle-board");
        gridPane.setHgap(1);
        gridPane.setVgap(1);

        for (int index = 0; index < Board.SIZE + 1; index++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));
            gridPane.getRowConstraints().add(new RowConstraints(CELL_SIZE));
        }

        gridPane.add(headerCell(""), 0, 0);
        for (int col = 0; col < Board.SIZE; col++) {
            gridPane.add(headerCell(String.valueOf((char) ('A' + col))), col + 1, 0);
        }

        for (int row = 0; row < Board.SIZE; row++) {
            gridPane.add(headerCell(String.valueOf(row + 1)), 0, row + 1);
            for (int col = 0; col < Board.SIZE; col++) {
                Coordinate coordinate = new Coordinate(row, col);
                Cell cell = board.getCell(coordinate);
                StackPane cellPane = createCellPane(cell, revealShips, preview, coordinate);
                cellPane.getStyleClass().add("battle-cell");
                Tooltip.install(cellPane, new Tooltip("Click this square"));
                if (allowShot) {
                    cellPane.setOnMouseClicked((MouseEvent event) -> clickConsumer.accept(coordinate));
                }
                if (allowShot || allowDrop) {
                    cellPane.setStyle("-fx-cursor: hand;");
                }
                if (allowDrop) {
                    cellPane.setOnDragOver(event -> {
                        if (event.getGestureSource() != cellPane && event.getDragboard().hasString()) {
                            event.acceptTransferModes(TransferMode.COPY);
                        }
                        event.consume();
                    });
                    cellPane.setOnDragEntered(event -> {
                        if (event.getDragboard().hasString()) {
                            dragListener.onDragEntered(coordinate);
                        }
                        event.consume();
                    });
                    cellPane.setOnDragExited(event -> {
                        if (event.getDragboard().hasString()) {
                            dragListener.onDragExited(coordinate);
                        }
                        event.consume();
                    });
                    cellPane.setOnDragDropped(event -> {
                        Dragboard dragboard = event.getDragboard();
                        boolean success = false;
                        if (dragboard.hasString()) {
                            dragListener.onDrop(coordinate);
                            success = true;
                        }
                        event.setDropCompleted(success);
                        event.consume();
                    });
                }
                gridPane.add(cellPane, col + 1, row + 1);
            }
        }

        return gridPane;
    }

    private static Node headerCell(String text) {
        StackPane stackPane = new StackPane(new Rectangle(CELL_SIZE, CELL_SIZE, Color.web("#ececec")), new Text(text));
        stackPane.setAlignment(Pos.CENTER);
        stackPane.setStyle("-fx-border-color: #8f8f8f; -fx-border-width: 0.5;");
        return stackPane;
    }

    private static StackPane createCellPane(Cell cell, boolean revealShips, PlacementPreview preview, Coordinate coordinate) {
        Rectangle base = new Rectangle(CELL_SIZE, CELL_SIZE);
        base.setArcHeight(12);
        base.setArcWidth(12);
        base.setFill(Color.web("#28d6ea"));
        base.setStroke(Color.web("#93f1ff"));

        StackPane stackPane = new StackPane(base);
        stackPane.setStyle("-fx-border-color: rgba(255,255,255,0.25); -fx-border-width: 0.4;");

        boolean shouldShowShip = cell.hasShip() && (revealShips || cell.getShotStatus() == ShotStatus.SUNK);
        if (shouldShowShip) {
            stackPane.getChildren().add(createShipSegmentNode(cell.getShip(), coordinate, cell.getShotStatus() == ShotStatus.SUNK));
        }

        if (cell.getShotStatus() == ShotStatus.WATER) {
            stackPane.getChildren().add(createWaterMark());
        } else if (cell.getShotStatus() == ShotStatus.HIT) {
            stackPane.getChildren().add(createHitMark());
        } else if (cell.getShotStatus() == ShotStatus.SUNK && !cell.hasShip()) {
            stackPane.getChildren().add(createSunkMark());
        }

        if (preview != null && preview.isActive() && preview.contains(coordinate)) {
            stackPane.getChildren().add(createPlacementOverlay(preview.valid()));
        }

        return stackPane;
    }

    private static Node createShipSegmentNode(Ship ship, Coordinate coordinate, boolean burning) {
        Image image = resolveShipImage(ship.type(), burning || ship.isSunk());
        if (image == null) {
            Rectangle fallback = new Rectangle(CELL_SIZE - SHIP_PADDING, CELL_SIZE - SHIP_PADDING, Color.web("#6b6b6b"));
            fallback.setArcHeight(8);
            fallback.setArcWidth(8);
            fallback.setStroke(Color.web("#2f2f2f"));
            fallback.setStrokeWidth(1.2);
            return fallback;
        }

        int segmentIndex = ship.coordinates().indexOf(coordinate);
        if (segmentIndex < 0) {
            segmentIndex = 0;
        }

        boolean sourceHorizontal = image.getWidth() >= image.getHeight();
        int shipLength = ship.type().size();

        Rectangle2D viewport;
        boolean rotateForBoardOrientation;

        if (sourceHorizontal) {
            double segmentWidth = image.getWidth() / shipLength;
            viewport = new Rectangle2D(segmentIndex * segmentWidth, 0, segmentWidth, image.getHeight());
            rotateForBoardOrientation = ship.orientation() == Orientation.VERTICAL;
        } else {
            double segmentHeight = image.getHeight() / shipLength;
            viewport = new Rectangle2D(0, segmentIndex * segmentHeight, image.getWidth(), segmentHeight);
            rotateForBoardOrientation = ship.orientation() == Orientation.HORIZONTAL;
        }

        ImageView imageView = new ImageView(image);
        imageView.setViewport(viewport);
        imageView.setFitWidth(CELL_SIZE - SHIP_PADDING);
        imageView.setFitHeight(CELL_SIZE - SHIP_PADDING);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);

        if (rotateForBoardOrientation) {
            imageView.setRotate(90);
        }

        return imageView;
    }

    private static Node createPlacementOverlay(boolean valid) {
        Rectangle overlay = new Rectangle(CELL_SIZE - 4, CELL_SIZE - 4);
        overlay.setArcHeight(10);
        overlay.setArcWidth(10);
        overlay.setFill(valid ? Color.rgb(61, 214, 117, 0.35) : Color.rgb(237, 74, 74, 0.35));
        overlay.setStroke(valid ? Color.web("#b7f7c9") : Color.web("#ffd0d0"));
        overlay.setStrokeWidth(2);
        StackPane.setMargin(overlay, new Insets(2));
        return overlay;
    }

    private static Image resolveShipImage(ShipType shipType, boolean burning) {
        return switch (shipType) {
            case CARRIER -> burning ? CARRIER_BURNING : CARRIER_NORMAL;
            case SUBMARINE -> burning ? SUBMARINE_BURNING : SUBMARINE_NORMAL;
            case DESTROYER -> burning ? DESTROYER_BURNING : DESTROYER_NORMAL;
            case FRIGATE -> burning ? FRIGATE_BURNING : FRIGATE_NORMAL;
        };
    }

    private static Image loadImage(String resourcePath) {
        try {
            if (BoardRenderer.class.getResource(resourcePath) == null) {
                return null;
            }
            return new Image(BoardRenderer.class.getResource(resourcePath).toExternalForm());
        } catch (Exception exception) {
            return null;
        }
    }

    private static Node createWaterMark() {
        Line first = new Line(4, 4, 22, 22);
        Line second = new Line(4, 22, 22, 4);
        first.setStroke(Color.RED);
        second.setStroke(Color.RED);
        first.setStrokeWidth(3.2);
        second.setStrokeWidth(3.2);

        Pane cross = new Pane(first, second);
        cross.setPrefSize(26, 26);
        cross.setMinSize(26, 26);
        cross.setMaxSize(26, 26);

        StackPane wrapper = new StackPane(cross);
        wrapper.setMouseTransparent(true);
        return wrapper;
    }

    private static Node createHitMark() {
        Circle body = new Circle(12, Color.web("#262626"));
        body.setStroke(Color.web("#000000"));
        Circle spark = new Circle(4, Color.web("#ffb347"));
        spark.setTranslateX(11);
        spark.setTranslateY(-10);
        Polygon flame = new Polygon(0.0, -18.0, 5.0, -7.0, -5.0, -7.0);
        flame.setFill(Color.web("#ff6b00"));
        flame.setTranslateX(11);
        flame.setTranslateY(-12);
        return new StackPane(body, spark, flame);
    }

    private static Node createSunkMark() {
        Polygon flame = new Polygon(
                -10.0, 12.0,
                -2.0, -4.0,
                2.0, -12.0,
                10.0, -2.0,
                14.0, 12.0,
                0.0, 16.0
        );
        flame.setFill(Color.web("#ff5200"));
        flame.setStroke(Color.web("#b91f00"));
        flame.setStrokeWidth(1.5);
        return flame;
    }
}
