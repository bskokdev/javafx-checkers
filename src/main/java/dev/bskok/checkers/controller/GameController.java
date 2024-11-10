package dev.bskok.checkers.controller;

import dev.bskok.checkers.board.Board;
import dev.bskok.checkers.board.CheckersBoardBuilder;
import dev.bskok.checkers.events.GameOverEvent;
import dev.bskok.checkers.game.BoardGame;
import dev.bskok.checkers.game.CheckersGame;
import dev.bskok.checkers.game.GameSettings;
import dev.bskok.checkers.piece.Player;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameController implements Initializable {
  private static final Logger log = LoggerFactory.getLogger(GameController.class);

  private static final int TILE_SIZE = 80;

  @FXML private Pane gameBoardContainer;

  private BoardGame game;
  private Board board;
  private GameSettings gameSettings;

  private Stage stage;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // deferred till the settings are received in the method below
  }

  public void initializeWithGameSettings(Stage stage, GameSettings gameSettings) {
    this.stage = stage;
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
    game.getWinner().ifPresent(this::showGameOverDialog);
  }

  private Alert createGameOverAlert(Player winner) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Game Over");
    alert.setHeaderText("We have a winner!");
    alert.setContentText(String.format("Player %s wins!", winner.name()));

    alert
        .getButtonTypes()
        .setAll(new ButtonType("New Game"), new ButtonType("Restart Game"), new ButtonType("Exit"));

    return alert;
  }

  private void showGameOverDialog(Player winner) {
    Alert gameOverAlert = createGameOverAlert(winner);
    Optional<ButtonType> response = gameOverAlert.showAndWait();
    try {
      handleDialogResponse(response.orElse(null));
    } catch (IOException ex) {
      log.error(
          "An exception occurred during game over dialog button response: {}", String.valueOf(ex));
    }
  }

  private void handleDialogResponse(ButtonType response) throws IOException {
    if (response == null) {
      return;
    }

    switch (response.getText()) {
      case "New Game" -> switchToStartMenuScreen();
      case "Restart Game" -> restartGame();
      case "Exit" -> exitGame();
    }
  }

  private void switchToStartMenuScreen() throws IOException {
    FXMLLoader gameStartLoader = new FXMLLoader(getClass().getResource("/gameStart.fxml"));
    Parent gameRoot = gameStartLoader.load();

    GameStartController gameStartController = gameStartLoader.getController();
    gameStartController.initializeWithExistingStage(stage);

    Scene scene = new Scene(gameRoot);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  private void restartGame() {
    board.getPane().getChildren().clear();
    initializeGame();
    gameBoardContainer.getChildren().add(board.getPane());
  }

  @FXML
  private void exitGame() {
    System.exit(0);
  }
}
