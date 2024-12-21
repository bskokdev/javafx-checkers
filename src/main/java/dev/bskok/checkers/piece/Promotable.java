package dev.bskok.checkers.piece;

import dev.bskok.checkers.board.Board;

/**
 * An interface which is specific to a type of game where a piece would be promoted to another piece
 * (normal -> king)
 */
public interface Promotable {
  /**
   * Checks if the current piece should be promoted to another pieceType based on the player and its
   * position
   *
   * @param pieceType type to which the piece should be promoted to
   * @param board Given game board the piece is present on
   * @param player Player to which the piece belongs to
   * @param row row of the piece
   * @param col column of the piece
   * @return true if we can promote the piece based on its position and desired piece type, else
   *     false
   */
  boolean shouldBePromotedTo(PieceType pieceType, Board board, Player player, int row, int col);

  /**
   * Promotes the current piece to a desired piece type
   *
   * @param promotedType Type of the piece to be promoted to (for example KING)
   */
  void promoteTo(PieceType promotedType);
}
