package dev.bskok.checkers.board;

import dev.bskok.checkers.gameLogic.ColorConverter;
import dev.bskok.checkers.gameLogic.IGameLogic;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckersGameBoard extends GridPane implements IGameBoard {
  public static final Logger log = LoggerFactory.getLogger(CheckersGameBoard.class);

  @Getter private final int tileSize;
  @Getter private final int rows;
  @Getter private final int cols;

  @Setter private IGameLogic gameLogic;

  private final Piece[][] pieces;

  public CheckersGameBoard(int tileSize, int rows, int cols) {
    this.tileSize = tileSize;
    this.rows = rows;
    this.cols = cols;
    this.pieces = new Piece[this.rows][this.cols];
  }

  @Override
  public void placePieceAt(Piece piece, int row, int col) {
    pieces[row][col] = piece;
    piece.moveTo(row, col);
    this.add(piece, col, row);

    // center the piece within the rectangle cell
    GridPane.setHalignment(piece, HPos.CENTER);
    GridPane.setValignment(piece, VPos.CENTER);

    log.debug(
        "Placed {} piece at position on the board: [{}, {}]",
        ColorConverter.getColorName(piece.getColor()),
        row,
        col);
  }

  @Override
  public void removePieceAt(int row, int col) {
    Piece piece = pieces[row][col];
    if (piece != null) {
      pieces[row][col] = null;
      this.getChildren().remove(piece);
      log.debug("Removed piece from the board at position: [{}, {}]", row, col);
    }
  }

  @Override
  public void movePiece(Piece piece, int toRow, int toCol) {
    int fromRow = piece.getCurrentRow();
    int fromCol = piece.getCurrentCol();
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
  public void attachOnMouseClickedEventHandler() {
    this.setOnMouseClicked(this::handleMouseClick);
    log.info("Attached click event listener to the grid pane");
  }

  @Override
  public void handleMouseClick(MouseEvent event) {
    int row = (int) (event.getY() / tileSize);
    int col = (int) (event.getX() / tileSize);
    log.debug("User clicked at board cell at position: [{}, {}] ", row, col);
    gameLogic.handlePlayerActionAt(row, col);
  }

  @Override
  public boolean isPositionInBounds(int row, int col) {
    return row >= 0 && row < rows && col >= 0 && col < cols;
  }

  @Override
  public Piece getPieceAt(int row, int col) {
    return pieces[row][col];
  }
}
