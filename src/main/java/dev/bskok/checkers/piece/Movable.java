package dev.bskok.checkers.piece;

public interface Movable {
  void moveTo(int toRow, int toCol);

  int getRow();

  int getCol();
}
