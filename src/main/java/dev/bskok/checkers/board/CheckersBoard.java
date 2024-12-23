package dev.bskok.checkers.board;

import dev.bskok.checkers.game.BoardGame;
import dev.bskok.checkers.piece.*;
import java.util.*;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckersBoard extends GridPane implements Board {
  public static final Logger log = LoggerFactory.getLogger(CheckersBoard.class);

  @Getter private final int tileSize;
  @Getter private final int rows;
  @Getter private final int cols;

  private final List<List<Integer>> topPlayerDeltas;
  private final List<List<Integer>> bottomPlayerDeltas;
  private final Colorable[][] pieces;

  public CheckersBoard(int tileSize, int rows, int cols) {
    this.tileSize = tileSize;
    this.rows = rows;
    this.cols = cols;
    // since we know the dimension of the board, it's better to keep this as a 2D array
    this.pieces = new CheckersPiece[this.rows][this.cols];
    this.topPlayerDeltas =
        Arrays.asList(
            List.of(1, 1), // right forward
            List.of(1, -1), // left forward
            List.of(1, 1), // right forward jump
            List.of(1, -1), // left forward jump
            List.of(-1, -1), // left backward
            List.of(-1, 1) // right backward
            );

    this.bottomPlayerDeltas =
        Arrays.asList(
            List.of(-1, 1), // right forward
            List.of(-1, -1), // left forward
            List.of(-1, 1), // right forward jump
            List.of(-1, -1), // left forward jump
            List.of(1, -1), // left backward
            List.of(1, 1) // right backward
            );
  }

  @Override
  public void placePieceAt(Colorable piece, int row, int col) {
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
  public void removePieceAt(int row, int col) {
    getPieceAt(row, col)
        .ifPresent(
            pieceToRemove -> {
              pieces[row][col] = null;
              this.getChildren().remove(pieceToRemove);
              log.debug("Removed piece from the board at position: [{}, {}]", row, col);
            });
  }

  @Override
  public void movePieceOnBoard(Colorable piece, int toRow, int toCol) {
    int fromRow = piece.getRow();
    int fromCol = piece.getCol();
    log.debug(
        "Piece at position: [{}, {}] moved to position: [{}, {}] on the board",
        fromRow,
        fromCol,
        toRow,
        toCol);
    removePieceAt(fromRow, fromCol);
    placePieceAt(piece, toRow, toCol);
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
  public Colorable[][] getPieces() {
    return this.pieces;
  }

  @Override
  public GridPane getPane() {
    return this;
  }

  @Override
  public Optional<Colorable> getPieceAt(int row, int col) {
    return Optional.ofNullable(pieces[row][col]);
  }

  @Override
  public List<List<Integer>> getDeltasForPlayer(Player player) {
    if (player.isTop()) return topPlayerDeltas;
    return bottomPlayerDeltas;
  }
}
