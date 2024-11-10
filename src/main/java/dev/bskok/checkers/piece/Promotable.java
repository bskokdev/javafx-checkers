package dev.bskok.checkers.piece;

import dev.bskok.checkers.board.Board;

public interface Promotable {
  boolean shouldBePromotedTo(PieceType pieceType, Board board, Player player, int row, int col);

  void promoteTo(PieceType promotedType);
}
