package dev.bskok.checkers.board;

import dev.bskok.checkers.game.BoardGame;
import dev.bskok.checkers.piece.Piece;

import java.util.Optional;

public interface Board {
  void placePieceAt(Piece piece, int row, int col);

  void removePieceAt(int row, int col);

  Optional<Piece> getPieceAt(int row, int col);

  void movePieceOnBoard(Piece piece, int toRow, int toCol);

  void attachOnClickEventHandler(BoardGame boardGame);

  boolean isPositionInBounds(int row, int col);
}
