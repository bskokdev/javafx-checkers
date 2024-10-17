package dev.bskok.checkers;

import dev.bskok.checkers.game.BoardGame;
import dev.bskok.checkers.game.BoardGameFactory;
import dev.bskok.checkers.game.BoardGameType;
import dev.bskok.checkers.game.CheckersGame;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Game could
// which would contain the board and the state of the game

public class CheckersApp extends Application {
  private static final Logger log = LoggerFactory.getLogger(CheckersApp.class);

  @Override
  public void start(Stage primaryStage) {
    // TODO(bskok): add notifications for when we have a winner, etc.
    // TODO(bskok): add reading and writing from/to a file - save result after game ends
    // TODO(bskok): there should also be a table with number of moves per game on the right
    try {
      VBox leftLayout = new VBox();
      BoardGame checkersGame = BoardGameFactory.getBoardGame(BoardGameType.CHECKERS);
      leftLayout.getChildren().addAll(checkersGame.getBoard());

      Scene scene = new Scene(leftLayout);

      primaryStage.setTitle("Checkers");
      primaryStage.setScene(scene);
      primaryStage.setResizable(false);
      primaryStage.show();
    } catch (Exception e) {
      log.error("An error occurred during application start: {}", e.getMessage());
    }
  }

  public static void main(String[] args) {
    launch();
  }
}
