package dev.bskok.checkers.controller;

import dev.bskok.checkers.game.GameSettings;
import dev.bskok.checkers.piece.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;

public class GameStartController {
    @FXML
    private TextField boardSizeField;

    @FXML
    private TextField boardSizeField2;

    @FXML
    private TextField player1NameField;

    @FXML
    private TextField player2NameField;

    @FXML
    private ColorPicker player1ColorPicker;

    @FXML
    private ColorPicker player2ColorPicker;

    @Setter private Stage stage;

    @FXML
    public void initialize() {
        player1ColorPicker.setValue(Color.BLUE);
        player2ColorPicker.setValue(Color.RED);

        // Add input validation for board size
        boardSizeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                boardSizeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        boardSizeField2.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                boardSizeField2.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    private void handleStartGame() {
        try {
            int rows = Integer.parseInt(boardSizeField.getText());
            int cols = Integer.parseInt(boardSizeField2.getText());
            Player playerA = new Player(player1NameField.getText(), player1ColorPicker.getValue(), true);
            Player playerB = new Player(player2NameField.getText(), player2ColorPicker.getValue(), false);

            if (rows < 1 || cols < 1) {
                throw new IllegalArgumentException("Board size must be positive");
            }

            if (playerA.name().isEmpty() || playerB.name().isEmpty()) {
                throw new IllegalArgumentException("Player names cannot be empty");
            }

            // TODO(bskok): extract into a separate method
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/checkers.fxml"));
            Parent gameRoot = loader.load();
            GameController gameController = loader.getController();

            GameSettings gameSettings = new GameSettings(rows, cols, playerA, playerB);
            gameController.initializeWithGameSettings(gameSettings);

            Scene scene = new Scene(gameRoot);
            stage.setScene(scene);
            stage.show();

        } catch (NumberFormatException e) {
            System.err.println("Please enter valid numbers for board size");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
