package dev.bskok.checkers.piece;

/** An interface which represents a game object which can be moved to a different position */
public interface Movable {
  /**
   * Moves a Movable object to the desired coordinates
   *
   * @param toRow row to move the implementation's instance to
   * @param toCol column to move the implementation's instance to
   */
  void moveTo(int toRow, int toCol);

  /**
   * Gets row of the instance
   *
   * @return row of the instance
   */
  int getRow();

  /**
   * Gets column of the instance
   *
   * @return column of the instance
   */
  int getCol();
}
