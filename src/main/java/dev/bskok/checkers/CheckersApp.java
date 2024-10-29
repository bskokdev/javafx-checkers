package dev.bskok.checkers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckersApp extends Application {
  private static final Logger log = LoggerFactory.getLogger(CheckersApp.class);

  @Override
  public void start(Stage primaryStage) {
    // TODO(bskok): add notifications for when we have a winner, etc.
    // TODO(bskok): add reading and writing from/to a file - save result after game ends
    // TODO(bskok): there should also be a table with number of moves per game on the right
    // TODO(bskok): interfaces seem inconsistent, check if I can extend them
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/checkers.fxml"));
      Scene scene = new Scene(fxmlLoader.load());

      primaryStage.setTitle("Checkers");
      primaryStage.setScene(scene);
      primaryStage.setResizable(false);
      primaryStage.show();

      log.info("Checkers application started successfully");
    } catch (Exception e) {
      log.error("An error occurred during application start: {}", e.getMessage(), e);
    }
  }

  public static void main(String[] args) {
    launch();
  }
}
