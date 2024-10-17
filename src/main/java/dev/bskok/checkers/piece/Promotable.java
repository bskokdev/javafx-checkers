package dev.bskok.checkers.piece;

import dev.bskok.checkers.board.CheckersBoard;

public interface Promotable {
  boolean shouldBePromotedTo(PieceType pieceType, CheckersBoard board, int row, int col);

  void promoteTo(PieceType promotedType);
}
