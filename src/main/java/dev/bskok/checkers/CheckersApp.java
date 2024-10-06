package dev.bskok.checkers;

import dev.bskok.checkers.board.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckersApp extends Application {
  private static final Logger log = LoggerFactory.getLogger(CheckersApp.class);

  @Override
  public void start(Stage primaryStage) {
    // TODO: add reading and writing from/to a file - save result after game ends
    // TODO: there should also be a table with number of moves per game on the right
    try {
      VBox leftLayout = new VBox();
      CheckersGameBoard board =
          (CheckersGameBoard) GameBoardFactory.getGameBoard(BoardType.CHECKERS, 8, 8, 80);

      leftLayout.getChildren().addAll(board);
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
