package dev.bskok.checkers.controller;

import dev.bskok.checkers.board.Board;
import dev.bskok.checkers.board.CheckersBoardBuilder;
import dev.bskok.checkers.events.GameOverEvent;
import dev.bskok.checkers.events.PlayerMoveEvent;
import dev.bskok.checkers.game.BoardGame;
import dev.bskok.checkers.game.CheckersGame;
import dev.bskok.checkers.game.GameResult;
import dev.bskok.checkers.game.GameSettings;
import dev.bskok.checkers.piece.Player;
import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import dev.bskok.checkers.server.client.GameRestClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameController implements Initializable {
  private static final Logger log = LoggerFactory.getLogger(GameController.class);
  private static final int TILE_SIZE = 80;
  private final String GAME_START_FXML_PATH = "/gameStart.fxml";
  private final String GAME_CSS_PATH = "/styles/game.css";
  private final String START_CSS_PATH = "/styles/start.css";
  private final String DIALOG_CSS_PATH = "/styles/dialog.css";
  private final String RESULTS_LOCATION = "data/results.csv";
  @FXML public Text player1Name;
  @FXML public Text player2Name;

  @FXML public Text currentTurn;

  @FXML public Text player1Pieces;
  @FXML public Text player2Pieces;

  @FXML public Circle player1Color;
  @FXML public Circle player2Color;
  @FXML public Circle currentTurnColor;

  @FXML private Pane gameBoardContainer;

  private BoardGame game;
  private Board board;
  private GameSettings gameSettings;

  private Stage stage;

  private final GameRestClient restClient = new GameRestClient();
  private Long currentGameId;
  private Long player1Id;
  private Long player2Id;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // deferred till the settings are received in the method below
  }

  void initializeWithGameSettings(Stage stage, GameSettings gameSettings) {
    this.stage = stage;
    this.gameSettings = gameSettings;

    restClient.createGame(gameSettings.playerA(), gameSettings.playerB())
            .thenAccept(response -> {
              this.player1Id = response.getPlayer1Id();
              this.player2Id = response.getPlayer2Id();
              this.currentGameId = response.getGame().getId();

              Platform.runLater(() -> {
                initializeGame();
                gameBoardContainer.getChildren().add(board.getPane());
              });
            })
            .exceptionally(ex -> {
              Platform.runLater(() -> showErrorDialog("Game Creation Failed", ex));
              return null;
            });
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

    // TODO(bskok): below events could be moved to the "attachEventHandlers" method in the builder
    board.addEventHandler(GameOverEvent.GAME_OVER, event -> handleGameOver());
    board.addEventHandler(
        PlayerMoveEvent.PLAYER_MOVE,
        event ->
            updateGameState(
                event.getNextTurn(), event.getPlayerAPieces(), event.getPlayerBPieces()));
    game.setBoard(board);
    initializePlayersState();
  }

  private void showErrorDialog(String title, Throwable ex) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Network Error");
    alert.setHeaderText(title);
    alert.setContentText(ex.getMessage());

    alert.showAndWait();
  }

  private void initializePlayersState() {
    player1Name.setText(gameSettings.playerA().name());
    player2Name.setText(gameSettings.playerB().name());

    player1Pieces.setText(Integer.toString(game.getPiecesCount(gameSettings.playerA())));
    player2Pieces.setText(Integer.toString(game.getPiecesCount(gameSettings.playerB())));
    currentTurn.setText(player1Name.getText());

    player1Color.setFill(gameSettings.playerA().color());
    player2Color.setFill(gameSettings.playerB().color());
    currentTurnColor.setFill(gameSettings.playerA().color());
    log.info(
        "Initialized player state\n player1Name={}, player2Name={}, currentTurn={}, player1Color={}, player2Color={}, currentTurnColor={}",
        player1Name.getText(),
        player2Name.getText(),
        currentTurn.getText(),
        player1Color.toString(),
        player2Color.toString(),
        currentTurnColor.toString());
  }

  public void handleGameOver() {
    game.getWinner().ifPresent(this::onGameOverAction);
  }

  private void onGameOverAction(Player winner) {
    Player loser = winner == gameSettings.playerA() ? gameSettings.playerB() : gameSettings.playerB();
    int winnerPieces = game.getPiecesCount(winner);
    int loserPieces = game.getPiecesCount(loser);

    Long winnerId = winner == gameSettings.playerA() ? player1Id : player2Id;
    Long loserId = loser == gameSettings.playerA() ? player1Id : player2Id;

    GameResult localResult = new GameResult(
            gameSettings.playerA().name(),
            gameSettings.playerB().name(),
            game.getPiecesCount(gameSettings.playerA()),
            game.getPiecesCount(gameSettings.playerB())
    );

    restClient.updateGameResult(
                    currentGameId,
                    winner,
                    winnerPieces,
                    winnerId,
                    loser,
                    loserPieces,
                    loserId)
            .thenAccept(gameDto -> Platform.runLater(() -> {
              writeGameResultAsCSV(localResult);
              showGameOverDialog(winner);
            }))
            .exceptionally(ex -> {
              Platform.runLater(() -> {
                writeGameResultAsCSV(localResult);
                showErrorDialog("Server Save Failed", ex);
                showGameOverDialog(winner);
              });
              return null;
            });
  }

  private void writeGameResultAsCSV(GameResult result) {
    File csvResultsFile = new File(RESULTS_LOCATION);
    log.debug("Attempting to write to file: {}", csvResultsFile.getAbsolutePath());

    if (csvResultsFile.getParentFile() != null && !csvResultsFile.getParentFile().exists()) {
      log.debug("Creating directory: {}", csvResultsFile.getParentFile().getAbsolutePath());
      boolean dirCreated = csvResultsFile.getParentFile().mkdirs();
      if (!dirCreated) {
        log.error("Failed to create directory structure for {}", csvResultsFile.getAbsolutePath());
        return;
      }
    }

    try (FileWriter fw = new FileWriter(csvResultsFile, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw)) {

      // Create header if file is empty
      if (csvResultsFile.length() == 0) {
        pw.println(result.getCsvHeader());
      }

      pw.println(result.getCsvDataRow());
      log.info("Saved a new game result to the results file - {}", result);
    } catch (IOException e) {
      log.error("Error writing to file {}: {}", "data/results.csv", e.getMessage());
      showFileErrorDialog();
    }
  }

  private void showFileErrorDialog() {
    createFileErrorAlert().showAndWait();
  }

  private Alert createFileErrorAlert() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("File Error");
    alert.setHeaderText("Unable to Save Game Results");
    alert.setContentText(
        "There was an error saving the game results to the file. Please check if the application has write permissions.");

    DialogPane dialogPane = alert.getDialogPane();
    dialogPane
        .getStylesheets()
        .add(Objects.requireNonNull(getClass().getResource(DIALOG_CSS_PATH)).toExternalForm());
    dialogPane.getStyleClass().add("alert");

    ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
    alert.getButtonTypes().setAll(okButton);

    alert.setOnShowing(
        e -> {
          Node okBtn = alert.getDialogPane().lookupButton(okButton);
          okBtn.getStyleClass().add("ok-button");
        });

    return alert;
  }

  private Alert createGameOverAlert(Player winner) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Game Over");
    alert.setHeaderText("We have a winner!");
    alert.setContentText(String.format("Player %s wins!", winner.name()));

    DialogPane dialogPane = alert.getDialogPane();
    dialogPane
        .getStylesheets()
        .add(Objects.requireNonNull(getClass().getResource(DIALOG_CSS_PATH)).toExternalForm());
    dialogPane.getStyleClass().add("alert");

    ButtonType newGameBtn = new ButtonType("New Game", ButtonBar.ButtonData.LEFT);
    ButtonType restartGameBtn = new ButtonType("Restart Game", ButtonBar.ButtonData.OTHER);
    ButtonType exitBtn = new ButtonType("Exit", ButtonBar.ButtonData.RIGHT);
    alert.getButtonTypes().setAll(newGameBtn, restartGameBtn, exitBtn);

    alert.setOnShowing(
        e -> {
          Node newGameButton = alert.getDialogPane().lookupButton(newGameBtn);
          Node restartGameButton = alert.getDialogPane().lookupButton(restartGameBtn);
          Node exitButton = alert.getDialogPane().lookupButton(exitBtn);

          newGameButton.getStyleClass().add("new-game-button");
          restartGameButton.getStyleClass().add("restart-game-button");
          exitButton.getStyleClass().add("exit-button");
        });

    return alert;
  }

  private void showGameOverDialog(Player winner) {
    Alert gameOverAlert = createGameOverAlert(winner);
    Optional<ButtonType> response = gameOverAlert.showAndWait();
    log.trace("Showing game over dialog");
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
      case "New Game" -> createNewGame();
      case "Restart Game" -> restartGame();
      case "Exit" -> exitGame();
    }
  }

  private void switchToStartMenuScreen() throws IOException {
    log.info("Switching to the start menu screen");
    FXMLLoader gameStartLoader = new FXMLLoader(getClass().getResource(GAME_START_FXML_PATH));
    Parent gameRoot = gameStartLoader.load();

    GameStartController gameStartController = gameStartLoader.getController();
    gameStartController.initializeWithExistingStage(stage);

    Scene scene = new Scene(gameRoot);
    scene
        .getStylesheets()
        .add(Objects.requireNonNull(getClass().getResource(START_CSS_PATH)).toExternalForm());
    stage.setScene(scene);
    stage.show();
  }

  public void updateGameState(Player newCurrentTurn, int piecesA, int piecesB) {
    currentTurnColor.setFill(newCurrentTurn.color());
    currentTurn.setText(newCurrentTurn.name());
    player1Pieces.setText(Integer.toString(piecesA));
    player2Pieces.setText(Integer.toString(piecesB));

    log.trace(
        "Updated player state\n currentTurn={}, player1Pieces={}, player2Pieces={}, currentTurnColor={}",
        currentTurn.getText(),
        player1Pieces.getText(),
        player2Pieces.getText(),
        currentTurnColor.toString());
  }

  @FXML
  private void createNewGame() {
    try {
      switchToStartMenuScreen();
    } catch (IOException e) {
      log.error("Unable to switch to the start menu scene: {}", String.valueOf(e));
    }
  }

  @FXML
  private void restartGame() {
    board.getPane().getChildren().clear();
    initializeGame();
    gameBoardContainer.getChildren().add(board.getPane());
    player1Pieces.setText(Integer.toString(0));
    player2Pieces.setText(Integer.toString(0));
    log.info("Current game has been restarted");
  }

  @FXML
  private void exitGame() {
    log.warn("Exiting the program");
    System.exit(0);
  }
}
