package dev.bskok.checkers.piece;

/** An interface which allows objects to be selected */
public interface Selectable {
  /** Marks the instance as selected */
  void select();

  /** deselects the instance */
  void deselect();
}
