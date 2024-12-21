package dev.bskok.checkers.game;

import dev.bskok.checkers.board.Board;
import dev.bskok.checkers.board.Move;
import dev.bskok.checkers.events.GameOverEvent;
import dev.bskok.checkers.events.PlayerMoveEvent;
import dev.bskok.checkers.piece.*;
import dev.bskok.checkers.piece.Player;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckersGame implements BoardGame {
  public static final Logger log = LoggerFactory.getLogger(CheckersGame.class);
  private final Player playerA;
  private final Player playerB;
  @Getter @Setter private Board board;
  private Piece selectedPiece;
  private Player playerWithCurrentTurn;

  public CheckersGame(Player playerA, Player playerB) {
    this.playerA = playerA;
    this.playerB = playerB;
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

  @Override
  public void selectPieceAt(int row, int col) {
    board
        .getPieceAt(row, col)
        .map(piece -> (Piece) piece)
        .filter(piece -> piece.getColor() == playerWithCurrentTurn.color())
        .ifPresent(
            piece -> {
              selectedPiece = piece;
              selectedPiece.select();
              log.trace("User selected piece at position: [{}, {}]", row, col);
            });
  }

  public Optional<Player> getWinner() {
    boolean hasPlayerAPieces = doesPlayerHavePiecesLeft(playerA);
    boolean hasPlayerBPieces = doesPlayerHavePiecesLeft(playerB);

    boolean canPlayerAMove = areValidMovesLeftForPlayer(playerA);
    boolean canPlayerBMove = areValidMovesLeftForPlayer(playerB);

    if (!hasPlayerAPieces || !canPlayerAMove) {
      return Optional.of(playerB);
    } else if (!hasPlayerBPieces || !canPlayerBMove) {
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
    board.movePieceOnBoard(selectedPiece, move.toRow(), move.toCol());

    if (selectedPiece instanceof CheckersPiece selectedCheckersPiece) {
      if (selectedCheckersPiece.shouldBePromotedTo(
          PieceType.KING, board, playerWithCurrentTurn, move.toRow(), move.toCol())) {
        selectedCheckersPiece.promoteTo(PieceType.KING);
      }
    }

    deselectSelectedPiece();
    swapPlayerTurns();
    handleCaptureMove(move);

    board.fireEvent(
        new PlayerMoveEvent(
            playerWithCurrentTurn, getPiecesCount(playerA), getPiecesCount(playerB)));
    getWinner()
        .ifPresent(
            winner -> {
              log.info("The winner is: {}", ColorConverter.getColorName(winner.color()));
              board.fireEvent(new GameOverEvent(winner));
            });
  }

  public int getPiecesCount(Player player) {
    Colorable[][] pieces = board.getPieces();
    // not every piece is non-null, because not every square has a piece
    return (int)
        Arrays.stream(pieces)
            .flatMap(Arrays::stream)
            .filter(piece -> piece != null && piece.getColor() == player.color())
            .count();
  }

  private boolean areValidMovesLeftForPlayer(Player player) {
    Color playerColor = player.color();
    for (int row = 0; row < board.getRows(); row++) {
      for (int col = 0; col < board.getCols(); col++) {
        if (board
            .getPieceAt(row, col)
            .map(piece -> (Piece) piece)
            .filter(piece -> piece.getColor() == playerColor)
            .isEmpty()) {
          continue;
        }

        for (List<Integer> direction : board.getDeltasForPlayer(player)) {
          Move normalMove = new Move(row, col, direction.get(0) + row, direction.get(1) + col);
          Move captureMove =
              new Move(row, col, direction.get(0) * 2 + row, direction.get(1) * 2 + col);
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
            .getPieceAt(row, col)
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

    if (board.isPositionOutOfBounds(toRow, toCol)
        || board.isPositionOutOfBounds(fromRow, fromCol)) {
      log.trace(
          "Invalid move: from position [{}, {}] or to position [{}, {}] is out of bounds",
          fromRow,
          fromCol,
          toRow,
          toCol);
      return false;
    }

    if (board.getPieceAt(toRow, toCol).isPresent()) {
      log.trace("Invalid move: target position [{}, {}] is not empty", toRow, toCol);
      return false;
    }

    return board
        .getPieceAt(fromRow, fromCol)
        .filter(piece -> piece instanceof CheckersPiece)
        .map(piece -> (CheckersPiece) piece)
        .map(checkersPiece -> isValidCheckersMove(checkersPiece, move))
        .orElse(false);
  }

  private boolean isValidCheckersMove(CheckersPiece piece, Move move) {
    int direction = (piece.getColor() == playerA.color()) ? 1 : -1;
    if (!piece.isKing() && (move.toRow() - move.fromRow()) * direction < 0) {
      log.trace("Non-king pieces can only move forward!");
      return false;
    }

    int rowDiff = Math.abs(move.fromRow() - move.toRow());
    int colDiff = Math.abs(move.fromCol() - move.toCol());
    log.trace("row diff: {}, col diff: {}", rowDiff, colDiff);

    return (rowDiff == 1 && colDiff == 1) || isCaptureDuringMove(move);
  }

  private void handleCaptureMove(Move move) {
    if (Math.abs(move.fromRow() - move.toRow()) != 2) {
      return;
    }

    int capturedRow = (move.fromRow() + move.toRow()) / 2;
    int capturedCol = (move.fromCol() + move.toCol()) / 2;
    log.info("Piece at [{}, {}] has been captured", capturedRow, capturedCol);
    board.removePieceAt(capturedRow, capturedCol);
  }

  private boolean isCaptureDuringMove(Move move) {
    if (Math.abs(move.fromRow() - move.toRow()) != 2
        || Math.abs(move.fromCol() - move.toCol()) != 2) {
      return false;
    }

    int capturedRow = (move.fromRow() + move.toRow()) / 2;
    int capturedCol = (move.fromCol() + move.toCol()) / 2;

    Optional<Colorable> attackingPiece = board.getPieceAt(move.fromRow(), move.fromCol());
    Optional<Colorable> targetPiece = board.getPieceAt(capturedRow, capturedCol);

    return attackingPiece
        .flatMap(
            capturing ->
                targetPiece.map(
                    target -> {
                      if (capturing instanceof Piece && target instanceof Piece) {
                        return capturing.getColor() != target.getColor();
                      }
                      throw new IllegalStateException("One or both Movables are not Pieces");
                    }))
        .orElse(false);
  }

  @Override
  public void deselectSelectedPiece() {
    log.trace(
        "{} has deselected the currently selected piece",
        ColorConverter.getColorName(selectedPiece.getColor()));

    selectedPiece.deselect();
    selectedPiece = null;
  }

  private void swapPlayerTurns() {
    playerWithCurrentTurn = (playerWithCurrentTurn == playerA) ? playerB : playerA;
    log.info("{} has the current turn", ColorConverter.getColorName(playerWithCurrentTurn.color()));
  }
}
