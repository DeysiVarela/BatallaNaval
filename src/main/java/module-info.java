/**
 * Main module for the Batalla Naval JavaFX application.
 */
module com.deysi.battleship {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.deysi.battleship to javafx.fxml;
    opens com.deysi.battleship.controller to javafx.fxml;

    exports com.deysi.battleship;
    exports com.deysi.battleship.controller;
    exports com.deysi.battleship.exception;
    exports com.deysi.battleship.model;
    exports com.deysi.battleship.service;
}
