package dev.bskok.checkers.board;

import dev.bskok.checkers.gameLogic.ColorConverter;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckersBoardBuilder implements IBoardBuilder {
  public static final Logger log = LoggerFactory.getLogger(CheckersBoardBuilder.class);
  private CheckersGameBoard board;

  @Override
  public IBoardBuilder initializeBoardDimensions(int rows, int cols, int tileSize) {
    this.board = new CheckersGameBoard(tileSize, rows, cols);
    log.debug("Set board dimensions to: [rows={}, cols={}, tileSize={}]", rows, cols, tileSize);
    return this;
  }

  @Override
  public CheckersBoardBuilder constructGrid() {
    log.info("Creating the game board grid");
    for (int row = 0; row < board.getRows(); row++) {
      for (int col = 0; col < board.getCols(); col++) {
        int tileSize = board.getTileSize();
        Rectangle cell = new Rectangle(tileSize, tileSize);
        if ((row + col) % 2 == 0) {
          cell.setFill(Color.WHITE);
        } else {
          cell.setFill(Color.BLACK);
        }

        board.add(cell, col, row);
        log.debug(
            "Placed {} rectangle at position: [{}, {}]",
            ColorConverter.getColorName((Color) cell.getFill()),
            row,
            col);
      }
    }
    log.info("Successfully created the board grid");
    return this;
  }

  @Override
  public CheckersBoardBuilder placePieces() {
    log.info("Placing pieces on the board during game bootstrapping");
    double pieceRadius = (double) board.getTileSize() / 2 - 5;
    int rows = board.getRows();
    int cols = board.getCols();
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < cols; col++) {
        if ((row + col) % 2 != 0) {
          Piece redPiece = new Piece(Color.RED, pieceRadius);
          board.placePieceAt(redPiece, row, col);
        }
      }
    }

    for (int row = rows - 3; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        if ((row + col) % 2 != 0) {
          Piece aquaPiece = new Piece(Color.AQUA, pieceRadius);
          board.placePieceAt(aquaPiece, row, col);
        }
      }
    }
    log.info("Placed all the pieces on the grid");
    return this;
  }

  @Override
  public CheckersBoardBuilder attachEventHandlers() {
    this.board.attachOnMouseClickedEventHandler();
    return this;
  }

  @Override
  public IGameBoard build() {
    return this.board;
  }
}
