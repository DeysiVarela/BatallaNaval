# Batalla Naval

JavaFX mini project for a Battleship game against the computer.

## Features

- JavaFX + FXML user interface
- MVC-oriented structure
- Manual ship placement with horizontal and vertical orientation
- Random enemy fleet deployment
- Human and machine turns with automatic machine shots
- Autosave after each move
- Serializable game state plus plain-text player metadata
- Unit tests for core board, engine, and persistence logic

## Run

```bash
mvn clean test
mvn javafx:run
```

## Generate Javadoc

```bash
mvn javadoc:javadoc
```

Generated HTML documentation is available under `target/site/apidocs/index.html`.

## Rubric Coverage (Implemented in Code)

- JavaFX GUI with FXML and layout containers
- Event handling with keyboard, mouse, inner classes, interfaces, and adapter class
- MVC-oriented structure with low coupling between model, controller, and renderer
- Multiple data structures (`List`, `Set`, `EnumMap`, `record` types)
- Concurrency with machine-turn executor thread and autosave scheduler thread
- Custom checked/unchecked exceptions
- Unit testing with 3 test classes
- Design patterns: Strategy (`ShotStrategy`) and Factory (`ShipFactory`)
- 2D visuals for board states (water, hit, sunk) and ship icons
- Flat files + serialized state persistence

## Persistence files

The game writes data under the user's home directory in `.batalla-naval/`.
