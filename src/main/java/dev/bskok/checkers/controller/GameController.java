package dev.bskok.checkers.controller;

import dev.bskok.checkers.board.Board;
import dev.bskok.checkers.board.CheckersBoardBuilder;
import dev.bskok.checkers.events.GameOverEvent;
import dev.bskok.checkers.game.BoardGame;
import dev.bskok.checkers.game.CheckersGame;
import dev.bskok.checkers.game.GameSettings;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;

public class GameController implements Initializable {

  private static final int TILE_SIZE = 80;

  @FXML private Pane gameBoardContainer;

  private BoardGame game;
  private Board board;
  private GameSettings gameSettings;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // deferred till the settings are received in the method below
  }

  public void initializeWithGameSettings(GameSettings gameSettings) {
    this.gameSettings = gameSettings;
    initializeGame();
    gameBoardContainer.getChildren().add(board.getPane());
  }

  private void initializeGame() {
    game = new CheckersGame(gameSettings.playerA(), gameSettings.playerB());
    board =
        new CheckersBoardBuilder()
            .initializeBoardDimensions(gameSettings.rows(), gameSettings.cols(), TILE_SIZE)
            .constructGrid()
            .placePieces(gameSettings.playerA(), gameSettings.playerB())
            .attachEventHandlers(game)
            .build();

    board.attachOnClickEventHandler(game);
    board.addEventHandler(GameOverEvent.GAME_OVER, event -> handleGameOver());
    game.setBoard(board);
  }

  public void handleGameOver() {
    game.getWinner()
        .ifPresent(
            winner -> {
              Alert alert = new Alert(Alert.AlertType.INFORMATION);
              alert.setTitle("Game Over");
              alert.setHeaderText("We have a winner!");
              alert.setContentText(
                  String.format(
                      "Player %s wins!", winner.name())
                  );
              ButtonType newGameButton = new ButtonType("New Game");
              ButtonType exitButton = new ButtonType("Exit");
              alert.getButtonTypes().setAll(newGameButton, exitButton);

              alert
                  .showAndWait()
                  .ifPresent(
                      response -> {
                        if (response == newGameButton) {
                          restartGame();
                        } else if (response == exitButton) {
                          handleExitButton();
                        }
                      });
            });
  }

  @FXML
  public void restartGame() {
    board.getPane().getChildren().clear();
    initializeGame();
    gameBoardContainer.getChildren().add(board.getPane());
  }

  @FXML
  public void handleExitButton() {
    System.exit(0);
  }
}
