package dev.bskok.checkers.controller;

import dev.bskok.checkers.game.GameResult;
import dev.bskok.checkers.game.GameSettings;
import dev.bskok.checkers.piece.Player;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameStartController {
  private static final Logger log = LoggerFactory.getLogger(GameStartController.class);

  private final String GAME_FXML_PATH = "/checkers.fxml";
  private final String START_CSS_PATH = "/styles/start.css";
  private final String GAME_CSS_PATH = "/styles/game.css";

  @FXML private TableView resultsTableView;

  @FXML private Label loadedFile;

  @FXML private TextField boardSizeField;

  @FXML private TextField boardSizeField2;

  @FXML private TextField player1NameField;

  @FXML private TextField player2NameField;

  @FXML private ColorPicker player1ColorPicker;

  @FXML private ColorPicker player2ColorPicker;

  @Setter private Stage stage;

  private FileChooser previousResultsLoader;

  @FXML
  public void initialize() {
    log.debug("Initializing game start screen");
    initializePlayerInputs();
    initializeResultsTableHeaders();
    initializeFileLoaders();
    initializePreviousScoreIfPresent();
  }

  public void initializeWithExistingStage(Stage stage) {
    this.stage = stage;
    initialize();
  }

  private void initializePlayerInputs() {
    player1ColorPicker.setValue(Color.BLUE);
    player2ColorPicker.setValue(Color.RED);

    // Add input validation for board size
    boardSizeField
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue.matches("\\d*")) {
                boardSizeField.setText(newValue.replaceAll("[^\\d]", ""));
              }
            });

    boardSizeField2
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue.matches("\\d*")) {
                boardSizeField2.setText(newValue.replaceAll("[^\\d]", ""));
              }
            });
  }

  private void initializeFileLoaders() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select a file with previous results");
    fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

    FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV files", "*.csv");
    fileChooser.getExtensionFilters().add(csvFilter);
    previousResultsLoader = fileChooser;
  }

  private void initializePreviousScoreIfPresent() {
    boolean isResultFilePresent = new File("data", "results.csv").exists();
    if (isResultFilePresent) {
      log.debug(
          "Results data file already present in the \"data\\results.csv\", populating table view with scores");
      File previousResults = new File("data/results.csv");
      loadedFile.setText("Loaded results from: " + previousResults.getPath());
      populateResultsTableView(readDataFromCSV(previousResults));
    }
  }

  @FXML
  private void handlePreviousResultsLoad() {
    log.info("Load results pressed");
    File resultsDataFile = getFileFromFileLoader();
    if (resultsDataFile != null) {
      log.info("Loaded file: {}", resultsDataFile.getPath());
      loadedFile.setText("Loaded results from: " + resultsDataFile.getPath());
      List<GameResult> gameResults = readDataFromCSV(resultsDataFile);
      populateResultsTableView(gameResults);
    }
  }

  private void populateResultsTableView(List<GameResult> gameResults) {
    resultsTableView.getItems().clear();
    initializeResultsTableHeaders();
    resultsTableView.getItems().addAll(gameResults);
  }

  private void initializeResultsTableHeaders() {
    if (!resultsTableView.getColumns().isEmpty()) {
      return;
    }

    TableColumn<GameResult, String> player1Col = new TableColumn<>("Player 1");
    player1Col.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().player1Name()));

    TableColumn<GameResult, String> player2Col = new TableColumn<>("Player 2");
    player2Col.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().player2Name()));

    TableColumn<GameResult, Number> player1PiecesCol = new TableColumn<>("Player 1 Pieces");
    player1PiecesCol.setCellValueFactory(
        cellData -> new SimpleIntegerProperty(cellData.getValue().player1Pieces()));

    TableColumn<GameResult, Number> player2PiecesCol = new TableColumn<>("Player 2 Pieces");
    player2PiecesCol.setCellValueFactory(
        cellData -> new SimpleIntegerProperty(cellData.getValue().player2Pieces()));

    // Make columns equal width
    player1Col.prefWidthProperty().bind(resultsTableView.widthProperty().divide(4));
    player2Col.prefWidthProperty().bind(resultsTableView.widthProperty().divide(4));
    player1PiecesCol.prefWidthProperty().bind(resultsTableView.widthProperty().divide(4));
    player2PiecesCol.prefWidthProperty().bind(resultsTableView.widthProperty().divide(4));

    resultsTableView
        .getColumns()
        .addAll(player1Col, player2Col, player1PiecesCol, player2PiecesCol);
  }

  private List<GameResult> readDataFromCSV(File file) {
    List<GameResult> results = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line = br.readLine(); // skip header
      while ((line = br.readLine()) != null) {
        String[] values = line.split(";");
        if (values.length != 4) {
          continue;
        }

        GameResult result =
            new GameResult(
                values[0], values[1], Integer.parseInt(values[2]), Integer.parseInt(values[3]));
        results.add(result);
      }
      return results;

    } catch (IOException e) {
      log.error("Error reading CSV file: {}", e.getMessage());
    }
    return Collections.emptyList();
  }

  private File getFileFromFileLoader() {
    return previousResultsLoader.showOpenDialog(stage);
  }

  @FXML
  private void handleStartGame() {
    try {
      int rows = Integer.parseInt(boardSizeField.getText());
      GameSettings gameSettings = getGameSettings(rows);
      switchToGameScene(gameSettings);
    } catch (NumberFormatException e) {
      log.error("Please enter valid numbers for board size");
    } catch (IllegalArgumentException e) {
      log.error(e.getMessage());
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
    scene
        .getStylesheets()
        .add(Objects.requireNonNull(getClass().getResource(GAME_CSS_PATH)).toExternalForm());
    log.debug("Created game scene ... switching to it");
    stage.setScene(scene);
    stage.show();
  }
}
