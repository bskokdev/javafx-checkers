package dev.bskok.checkers.game;

import dev.bskok.checkers.board.Board;
import dev.bskok.checkers.piece.Player;

import java.util.Optional;

public interface BoardGame {
  void handlePlayerActionAt(int row, int col);

  void selectPieceAt(int row, int col);

  void deselectSelectedPiece();

  Optional<Player> getWinner();

  void setBoard(Board board);
}
