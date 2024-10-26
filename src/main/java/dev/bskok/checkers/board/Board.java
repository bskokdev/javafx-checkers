package dev.bskok.checkers.board;

import dev.bskok.checkers.game.BoardGame;
import dev.bskok.checkers.piece.Movable;

import java.util.Optional;

public interface Board {
  void placeMovableAt(Movable piece, int row, int col);

  void removeMovableAt(int row, int col);

  Optional<Movable> getMovableAt(int row, int col);

  void moveMovableOnBoard(Movable piece, int toRow, int toCol);

  void attachOnClickEventHandler(BoardGame boardGame);

  boolean isPositionOutOfBounds(int row, int col);
}
