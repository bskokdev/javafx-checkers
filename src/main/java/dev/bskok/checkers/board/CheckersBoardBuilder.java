package dev.bskok.checkers.board;

import dev.bskok.checkers.game.BoardGame;
import dev.bskok.checkers.piece.CheckersPiece;
import dev.bskok.checkers.piece.ColorConverter;
import dev.bskok.checkers.piece.Piece;
import dev.bskok.checkers.piece.Player;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckersBoardBuilder {
  public static final Logger log = LoggerFactory.getLogger(CheckersBoardBuilder.class);
  private CheckersBoard board;

  public CheckersBoardBuilder initializeBoardDimensions(int rows, int cols, int tileSize) {
    this.board = new CheckersBoard(tileSize, rows, cols);
    log.debug("Set board dimensions to: [rows={}, cols={}, tileSize={}]", rows, cols, tileSize);
    return this;
  }

  public CheckersBoardBuilder constructGrid() {
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
        log.trace(
            "Placed {} rectangle at position: [{}, {}]",
            ColorConverter.getColorName((Color) cell.getFill()),
            row,
            col);
      }
    }
    log.info("Successfully created the board grid");
    return this;
  }

  public CheckersBoardBuilder placePieces(Player playerA, Player playerB) {
    double pieceRadius = (double) board.getTileSize() / 2 - 5;
    int rows = board.getRows();
    int cols = board.getCols();
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < cols; col++) {
        if ((row + col) % 2 != 0) {
          Piece playerAPiece = new CheckersPiece(playerA.color(), pieceRadius);
          board.placePieceAt(playerAPiece, row, col);
        }
      }
    }

    for (int row = rows - 3; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        if ((row + col) % 2 != 0) {
          Piece playerBPiece = new CheckersPiece(playerB.color(), pieceRadius);
          board.placePieceAt(playerBPiece, row, col);
        }
      }
    }
    log.info("Placed all the pieces on the grid");
    return this;
  }

  public CheckersBoardBuilder attachEventHandlers(BoardGame boardGame) {
    board.attachOnClickEventHandler(boardGame);
    return this;
  }

  public CheckersBoard build() {
    return this.board;
  }
}
