package dev.bskok.checkers.game;

import dev.bskok.checkers.board.CheckersBoard;
import dev.bskok.checkers.board.Move;
import dev.bskok.checkers.piece.*;
import dev.bskok.checkers.piece.player.Player;
import java.util.Optional;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO(bskok): add unit tests for the core game logic
public class CheckersGame implements BoardGame {
  public static final Logger log = LoggerFactory.getLogger(CheckersGame.class);

  @Getter @Setter private CheckersBoard board;

  private final Player playerA; // RED
  private final Player playerB; // AQUA

  private Piece selectedPiece;
  private Player playerWithCurrentTurn;

  public CheckersGame() {
    this.playerA = new Player(Color.RED);
    this.playerB = new Player(Color.AQUA);
    this.playerWithCurrentTurn = playerA;
  }

  public void handlePlayerActionAt(int row, int col) {
    if (selectedPiece == null) {
      selectPieceAt(row, col);
    } else {
      Move proposedMove = new Move(selectedPiece.getRow(), selectedPiece.getCol(), row, col);
      handlePlayerMove(proposedMove);
    }
  }

  public void selectPieceAt(int row, int col) {
    board
        .getMovableAt(row, col)
        .map(piece -> (Piece) piece)
        .filter(piece -> piece.getColor() == playerWithCurrentTurn.color())
        .ifPresent(
            piece -> {
              selectedPiece = piece;
              log.trace("User selected piece at position: [{}, {}]", row, col);
            });
  }

  public Optional<Player> getWinner() {
    boolean redHasPieces = doesPlayerHavePiecesLeft(playerA);
    boolean aquaHasPieces = doesPlayerHavePiecesLeft(playerB);

    boolean redCanMove = areValidMovesLeftForPlayer(playerA);
    boolean aquaCanMove = areValidMovesLeftForPlayer(playerB);

    if (!redHasPieces || !redCanMove) {
      return Optional.of(playerB);
    } else if (!aquaHasPieces || !aquaCanMove) {
      return Optional.of(playerA);
    }

    return Optional.empty();
  }

  public void handlePlayerMove(Move move) {
    if (!isMoveValid(move)) {
      log.info(
          "Invalid move from: [{}, {}] to: [{}, {}]",
          move.fromRow(),
          move.fromCol(),
          move.toRow(),
          move.toCol());
      deselectSelectedPiece();
      return;
    }

    log.info("Move is valid, moving piece to: [{}, {}]", move.toRow(), move.toCol());
    board.moveMovableOnBoard(selectedPiece, move.toRow(), move.toCol());

    if (selectedPiece instanceof CheckersPiece selectedCheckersPiece) {
      if (selectedCheckersPiece.shouldBePromotedToKing(board, move.toRow(), move.toCol())) {
        selectedCheckersPiece.promoteTo(PieceType.KING);
      }
    }

    deselectSelectedPiece();
    swapPlayerTurns();
    handleCaptureMove(move);

    // TODO(bskok): display dialog window if winner present and stop the game
    getWinner()
        .ifPresent(
            winner -> log.info("The winner is: {}", ColorConverter.getColorName(winner.color())));
  }

  private boolean areValidMovesLeftForPlayer(Player player) {
    Color playerColor = player.color();
    for (int row = 0; row < board.getRows(); row++) {
      for (int col = 0; col < board.getCols(); col++) {
        if (board
            .getMovableAt(row, col)
            .map(piece -> (Piece) piece)
            .filter(piece -> piece.getColor() == playerColor)
            .isEmpty()) {
          continue;
        }

        for (int[] direction : board.getDeltasForPlayer(playerColor)) {
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

  private boolean doesPlayerHavePiecesLeft(Player player) {
    int rows = board.getRows();
    int cols = board.getCols();
    Color playerColor = player.color();

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        if (board
            .getMovableAt(row, col)
            .map(piece -> (Piece) piece)
            .filter(piece -> piece.getColor() == playerColor)
            .isPresent()) {
          log.trace("{} has pieces on the board", ColorConverter.getColorName(playerColor));
          return true;
        }
      }
    }
    log.debug("No pieces left for {}", ColorConverter.getColorName(playerColor));
    return false;
  }

  private boolean isMoveValid(Move move) {
    int fromRow = move.fromRow();
    int fromCol = move.fromCol();
    int toRow = move.toRow();
    int toCol = move.toCol();

    if (board.isPositionOutOfBounds(toRow, toCol) || board.isPositionOutOfBounds(fromRow, fromCol)) {
      log.trace(
          "Invalid move: from position [{}, {}] or to position [{}, {}] is out of bounds",
          fromRow,
          fromCol,
          toRow,
          toCol);
      return false;
    }

    if (board.getMovableAt(toRow, toCol).isPresent()) {
      log.trace("Invalid move: target position [{}, {}] is not empty", toRow, toCol);
      return false;
    }

    return board
        .getMovableAt(fromRow, fromCol)
        .filter(piece -> piece instanceof CheckersPiece)
        .map(piece -> (CheckersPiece) piece)
        .map(checkersPiece -> isValidCheckersMove(checkersPiece, move))
        .orElse(false);
  }

  private boolean isValidCheckersMove(CheckersPiece piece, Move move) {
    int direction = (piece.getColor() == Color.RED) ? 1 : -1;
    if (!piece.isKing() && (move.toRow() - move.fromRow()) * direction < 0) {
      log.trace("Non-king pieces can only move forward!");
      return false;
    }

    log.trace(
        "row diff: {}, col diff: {}",
        Math.abs(move.fromRow() - move.toRow()),
        Math.abs(move.fromCol() - move.toCol()));

    return (Math.abs(move.fromRow() - move.toRow()) == 1
            && Math.abs(move.fromCol() - move.toCol()) == 1)
        || isCaptureDuringMove(move);
  }

  private void handleCaptureMove(Move move) {
    if (Math.abs(move.fromRow() - move.toRow()) != 2) {
      return;
    }

    int capturedRow = (move.fromRow() + move.toRow()) / 2;
    int capturedCol = (move.fromCol() + move.toCol()) / 2;
    log.info("Piece at [{}, {}] has been captured", capturedRow, capturedCol);
    board.removeMovableAt(capturedRow, capturedCol);
  }

  private boolean isCaptureDuringMove(Move move) {
    if (Math.abs(move.fromRow() - move.toRow()) != 2
        || Math.abs(move.fromCol() - move.toCol()) != 2) {
      return false;
    }

    int capturedRow = (move.fromRow() + move.toRow()) / 2;
    int capturedCol = (move.fromCol() + move.toCol()) / 2;

    Optional<Movable> attackingPiece = board.getMovableAt(move.fromRow(), move.fromCol());
    Optional<Movable> targetPiece = board.getMovableAt(capturedRow, capturedCol);

    return attackingPiece
        .flatMap(
            capturing ->
                targetPiece.map(
                    target -> {
                      if (capturing instanceof Piece && target instanceof Piece) {
                        return ((Piece) capturing).getColor() != ((Piece) target).getColor();
                      }
                      throw new IllegalStateException("One or both Movables are not Pieces");
                    }))
        .orElse(false);
  }

  private void deselectSelectedPiece() {
    log.trace(
        "{} has deselected the currently selected piece",
        ColorConverter.getColorName(selectedPiece.getColor()));

    selectedPiece = null;
  }

  private void swapPlayerTurns() {
    playerWithCurrentTurn = (playerWithCurrentTurn == playerA) ? playerB : playerA;
    log.info("{} has the current turn", ColorConverter.getColorName(playerWithCurrentTurn.color()));
  }
}
