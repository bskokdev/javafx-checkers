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
import java.util.Objects;

public class GameStartController {

    private final String GAME_FXML_PATH = "/checkers.fxml";
    private final String START_CSS_PATH = "/styles/start.css";
    private final String GAME_CSS_PATH = "/styles/game.css";

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

    public void initializeWithExistingStage(Stage stage) {
        this.stage = stage;
        initialize();
    }

    @FXML
    private void handleStartGame() {
        try {
            int rows = Integer.parseInt(boardSizeField.getText());
            GameSettings gameSettings = getGameSettings(rows);
            switchToGameScene(gameSettings);
        } catch (NumberFormatException e) {
            System.err.println("Please enter valid numbers for board size");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private GameSettings getGameSettings(int rows) throws IllegalArgumentException {
        int cols = Integer.parseInt(boardSizeField2.getText());
        Player playerA = new Player(player1NameField.getText(), player1ColorPicker.getValue(), true);
        Player playerB = new Player(player2NameField.getText(), player2ColorPicker.getValue(), false);

        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException("Board size must be positive");
        }

        if (playerA.name().isEmpty() || playerB.name().isEmpty()) {
            throw new IllegalArgumentException("Player names cannot be empty");
        }

        return new GameSettings(rows, cols, playerA, playerB);
    }

    private void switchToGameScene(GameSettings settings) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(GAME_FXML_PATH));
        Parent gameRoot = loader.load();
        GameController gameController = loader.getController();

        gameController.initializeWithGameSettings(stage, settings);

        Scene scene = new Scene(gameRoot);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(GAME_CSS_PATH)).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
