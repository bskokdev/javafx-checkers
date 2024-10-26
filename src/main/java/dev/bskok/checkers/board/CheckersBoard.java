package dev.bskok.checkers.board;

import dev.bskok.checkers.game.BoardGame;
import dev.bskok.checkers.piece.CheckersPiece;
import dev.bskok.checkers.piece.ColorConverter;
import dev.bskok.checkers.piece.Movable;
import dev.bskok.checkers.piece.Piece;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CheckersBoard extends GridPane implements Board {
  public static final Logger log = LoggerFactory.getLogger(CheckersBoard.class);

  @Getter private final int tileSize;
  @Getter private final int rows;
  @Getter private final int cols;

  private final int[][] topPlayerDeltas;
  private final int[][] bottomPlayerDeltas;
  private final Movable[][] pieces;

  public CheckersBoard(int tileSize, int rows, int cols) {
    this.tileSize = tileSize;
    this.rows = rows;
    this.cols = cols;
    this.pieces = new CheckersPiece[this.rows][this.cols];
    this.topPlayerDeltas = new int[][] {{1, 1}, {1, -1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
    this.bottomPlayerDeltas = new int[][] {{-1, 1}, {-1, -1}, {-1, 1}, {-1, -1}, {1, -1}, {1, 1}};
  }

  @Override
  public void placeMovableAt(Movable piece, int row, int col) {
    pieces[row][col] = piece;
    piece.moveTo(row, col);
    if (piece instanceof Piece pieceNode) {
      this.add(pieceNode, col, row);
      // center the piece within the rectangle cell
      GridPane.setHalignment(pieceNode, HPos.CENTER);
      GridPane.setValignment(pieceNode, VPos.CENTER);
      log.trace(
              "Placed {} piece at position on the board: [{}, {}]",
              ColorConverter.getColorName(pieceNode.getColor()),
              row,
              col);
    }
  }

  @Override
  public void removeMovableAt(int row, int col) {
    getMovableAt(row, col)
        .ifPresent(
            pieceToRemove -> {
              pieces[row][col] = null;
              this.getChildren().remove(pieceToRemove);
              log.debug("Removed piece from the board at position: [{}, {}]", row, col);
            });
  }

  @Override
  public void moveMovableOnBoard(Movable piece, int toRow, int toCol) {
    int fromRow = piece.getRow();
    int fromCol = piece.getCol();
    log.debug(
        "Piece at position: [{}, {}] moved to position: [{}, {}] on the board",
        fromRow,
        fromCol,
        toRow,
        toCol);
    removeMovableAt(fromRow, fromCol);
    placeMovableAt(piece, toRow, toCol);
  }

  @Override
  public void attachOnClickEventHandler(BoardGame boardGame) {
    this.setOnMouseClicked((e -> handleGameMouseClick(e, boardGame)));
    log.info("Attached on click event listener for game to the board");
  }

  private void handleGameMouseClick(MouseEvent event, BoardGame boardGame) {
    int row = (int) (event.getY() / tileSize);
    int col = (int) (event.getX() / tileSize);
    log.debug("User clicked at board cell at position: [{}, {}] ", row, col);
    boardGame.handlePlayerActionAt(row, col);
  }

  @Override
  public boolean isPositionOutOfBounds(int row, int col) {
    return row < 0 || row >= rows || col < 0 || col >= cols;
  }

  @Override
  public Optional<Movable> getMovableAt(int row, int col) {
    return Optional.ofNullable(pieces[row][col]);
  }

  public int[][] getDeltasForPlayer(Color playerColor) {
    if (playerColor == Color.RED) {
      return topPlayerDeltas;
    } else if (playerColor == Color.AQUA) {
      return bottomPlayerDeltas;
    }

    return new int[][] {};
  }
}
