package dev.bskok.checkers.controller;

import dev.bskok.checkers.board.CheckersBoard;
import dev.bskok.checkers.board.CheckersBoardBuilder;
import dev.bskok.checkers.events.GameOverEvent;
import dev.bskok.checkers.game.CheckersGame;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    private static final int BOARD_SIZE = 8;
    private static final int TILE_SIZE = 80;

    @FXML
    private Pane gameBoardContainer;

    private CheckersGame game;
    private CheckersBoard board;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeGame();
        gameBoardContainer.getChildren().add(board);
    }

    private void initializeGame() {
        game = new CheckersGame();
        board = new CheckersBoardBuilder()
              .initializeBoardDimensions(BOARD_SIZE, BOARD_SIZE, TILE_SIZE)
              .constructGrid()
              .placePieces()
              .attachEventHandlers(game)
              .build();

        board.attachOnClickEventHandler(game);
        board.addEventHandler(GameOverEvent.GAME_OVER, event -> {
            handleGameOver();
        });
        game.setBoard(board);
    }

    public void handleGameOver() {
        game.getWinner().ifPresent(winner -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("We have a winner!");
            alert.setContentText(String.format("Player %s wins!",
                    winner.color() == Color.RED ? "Red" : "Aqua"));

            ButtonType newGameButton = new ButtonType("New Game");
            alert.getButtonTypes().add(newGameButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == newGameButton) {
                    restartGame();
                }
            });
        });
    }

    @FXML
    public void restartGame() {
        board.getChildren().clear();
        initializeGame();
        gameBoardContainer.getChildren().add(board);
    }

    @FXML
    public void handleExitButton() {
        System.exit(0);
    }
}
