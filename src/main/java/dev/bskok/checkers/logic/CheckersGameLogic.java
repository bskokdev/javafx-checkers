package dev.bskok.checkers.logic;

import dev.bskok.checkers.board.Move;
import dev.bskok.checkers.board.CheckersGameBoard;
import dev.bskok.checkers.board.Piece;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO(bskok): add unit tests for the core game logic
public class CheckersGameLogic {
  public static final Logger log = LoggerFactory.getLogger(CheckersGameLogic.class);

  private final CheckersGameBoard checkersGameBoard;

  private final int[][] topPlayerDeltas;
  private final int[][] bottomPlayerDeltas;

  private Piece selectedPiece;
  private Color winner;
  private Color currentTurn;

  public CheckersGameLogic(CheckersGameBoard checkersGameBoard) {
    this.checkersGameBoard = checkersGameBoard;
    this.topPlayerDeltas = new int[][] {{1, 1}, {1, -1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
    this.bottomPlayerDeltas = new int[][] {{-1, 1}, {-1, -1}, {-1, 1}, {-1, -1}, {1, -1}, {1, 1}};
    this.currentTurn = Color.RED;
  }

  public void handlePlayerActionAt(int row, int col) {
    if (selectedPiece == null) {
      selectPieceAt(row, col);
    } else {
      Move proposedMove =
          new Move(selectedPiece.getCurrentRow(), selectedPiece.getCurrentCol(), row, col);
      handlePlayerMove(proposedMove);
    }
  }

  public void selectPieceAt(int row, int col) {
    Piece toBeSelected = checkersGameBoard.getPieceAt(row, col);
    if (toBeSelected != null && toBeSelected.getColor() == currentTurn) {
      selectedPiece = toBeSelected;
      log.trace("User selected piece at position: [{}, {}]", row, col);
    }
  }

  public boolean existsWinner() {
    boolean redHasPieces = doesPlayerHavePiecesLeft(Color.RED);
    boolean aquaHasPieces = doesPlayerHavePiecesLeft(Color.AQUA);
    boolean redCanMove = areValidMovesLeftForPlayer(Color.RED);
    boolean aquaCanMove = areValidMovesLeftForPlayer(Color.AQUA);

    if (!redHasPieces || !redCanMove) {
      winner = Color.AQUA;
      return true;
    } else if (!aquaHasPieces || !aquaCanMove) {
      winner = Color.RED;
      return true;
    }

    return false;
  }

  public boolean areValidMovesLeftForPlayer(Color playerColor) {
    for (int row = 0; row < checkersGameBoard.getRows(); row++) {
      for (int col = 0; col < checkersGameBoard.getCols(); col++) {
        Piece piece = checkersGameBoard.getPieceAt(row, col);
        if (piece == null || piece.getColor() != playerColor) {
          continue;
        }

        for (int[] direction : getDeltasForPlayer(playerColor)) {
          Move normalMove = new Move(row, col, direction[0] + row, direction[1] + col);
          Move captureMove = new Move(row, col, direction[0] * 2 + row, direction[1] * 2 + col);
          if (isMoveValid(normalMove) || isMoveValid(captureMove)) {
            log.trace("{} can still move on the board", ColorConverter.getColorName(playerColor));
            return true;
          }
        }
      }
    }
    log.debug("No valid moves are left for {}", ColorConverter.getColorName(playerColor));
    return false;
  }

  public boolean doesPlayerHavePiecesLeft(Color playerColor) {
    int rows = checkersGameBoard.getRows();
    int cols = checkersGameBoard.getCols();

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        Piece pieceAtBoard = checkersGameBoard.getPieceAt(row, col);
        if (pieceAtBoard != null && pieceAtBoard.getColor() == playerColor) {
          log.trace("{} has pieces on the board", ColorConverter.getColorName(playerColor));
          return true;
        }
      }
    }
    log.debug("No pieces left for {}", ColorConverter.getColorName(playerColor));
    return false;
  }

  public void handlePlayerMove(Move move) {
    if (!isMoveValid(move)) {
      deselectSelectedPiece();
      return;
    }

    log.info("Move is valid, moving piece to: [{}, {}]", move.toRow(), move.toCol());
    checkersGameBoard.movePiece(selectedPiece, move.toRow(), move.toCol());

    if (shouldPromoteToKing(selectedPiece, move.toRow())) {
      selectedPiece.promoteToKing();
      log.info(
          "{} piece has reached row: {} - has been promoted to king",
          ColorConverter.getColorName(selectedPiece.getColor()),
          move.toRow());
    }

    deselectSelectedPiece();
    handleCaptureMove(move);
    swapPlayerTurns();
    if (isCaptureDuringMove(move)) {
      handleCaptureMove(move);
    }

    if (existsWinner() && winner != null) {
      log.info("The winner is: {}", ColorConverter.getColorName(winner));
    }
  }

  public boolean isMoveValid(Move move) {
    Piece piece = checkersGameBoard.getPieceAt(move.fromRow(), move.fromCol());
    if (!checkersGameBoard.isPositionInBounds(move.toRow(), move.toCol())) {
      log.trace(
          "Target position [{}, {}] is out of bounds for the given board",
          move.toRow(),
          move.toCol());
      return false;
    }

    if (checkersGameBoard.getPieceAt(move.toRow(), move.toCol()) != null) {
      log.trace("Target position [{}, {}] is not empty", move.toRow(), move.toCol());
      return false;
    }

    int direction = (piece.getColor() == Color.RED) ? 1 : -1;
    if (!piece.isKing() && (move.toRow() - move.fromRow()) * direction < 0) {
      log.trace("Non-king pieces can only move forward!");
      return false;
    }

    if (Math.abs(move.fromRow() - move.toRow()) == 1
        && Math.abs(move.fromCol() - move.toCol()) == 1) {
      return true;
    }

    return isCaptureDuringMove(move);
  }

  public void handleCaptureMove(Move move) {
    if (Math.abs(move.fromRow() - move.toRow()) != 2) {
      return;
    }

    int capturedRow = (move.fromRow() + move.toRow()) / 2;
    int capturedCol = (move.fromCol() + move.toCol()) / 2;
    log.info("Piece at [{}, {}] has been captured", capturedRow, capturedCol);
    checkersGameBoard.removePieceAt(capturedRow, capturedCol);
  }

  public boolean isCaptureDuringMove(Move move) {
    Piece capturingPiece = checkersGameBoard.getPieceAt(move.fromRow(), move.fromCol());
    if (Math.abs(move.fromRow() - move.toRow()) == 2
        && Math.abs(move.fromCol() - move.toCol()) == 2) {
      int capturedRow = (move.fromRow() + move.toRow()) / 2;
      int capturedCol = (move.fromCol() + move.toCol()) / 2;
      Piece pieceAtToBeCapturedPos = checkersGameBoard.getPieceAt(capturedRow, capturedCol);
      if (pieceAtToBeCapturedPos != null) {
        if (pieceAtToBeCapturedPos.getColor() == capturingPiece.getColor()) {
          log.warn("Can't capture your own pieces");
          return false;
        }
        return true;
      }
    }
    return false;
  }

  public void deselectSelectedPiece() {
    log.trace(
        "{} has deselected the currently selected piece",
        ColorConverter.getColorName(selectedPiece.getColor()));

    selectedPiece = null;
  }

  public void swapPlayerTurns() {
    currentTurn = (currentTurn == Color.RED) ? Color.AQUA : Color.RED;
    log.info("{} has the current turn", ColorConverter.getColorName(currentTurn));
  }

  private boolean shouldPromoteToKing(Piece piece, int row) {
    return (piece.getColor() == Color.RED && row == checkersGameBoard.getRows() - 1)
        || (piece.getColor() == Color.AQUA && row == 0);
  }

  private int[][] getDeltasForPlayer(Color playerColor) {
    if (playerColor == Color.RED) {
      return topPlayerDeltas;
    } else if (playerColor == Color.AQUA) {
      return bottomPlayerDeltas;
    }

    return new int[][] {};
  }
}
