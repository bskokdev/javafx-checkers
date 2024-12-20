package dev.bskok.checkers;

import dev.bskok.checkers.controller.GameStartController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CheckersApp extends Application {
  private static final Logger log = LoggerFactory.getLogger(CheckersApp.class);

  @Override
  public void start(Stage primaryStage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameStart.fxml"));
      Parent root = loader.load();

      GameStartController startController = loader.getController();
      startController.setStage(primaryStage);

      Scene scene = new Scene(root);
      scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/start.css")).toExternalForm());

      primaryStage.setScene(scene);
      primaryStage.setResizable(false);
      primaryStage.setTitle("CheckersFX");
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
