package dev.bskok.checkers;

import dev.bskok.checkers.board.*;
import dev.bskok.checkers.logic.CheckersGameLogic;
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

      // Possible replace with a board factory to support multiple boards
      // This would also require creating interfaces for the same logic across different games
      // And would be overcomplicated for this example
      CheckersGameBoard board =
          new CheckersBoardBuilder()
              .initializeBoardDimensions(8, 8, 80)
              .constructGrid()
              .placePieces()
              .attachEventHandlers()
              .build();

      board.setGameLogic(new CheckersGameLogic(board));

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
