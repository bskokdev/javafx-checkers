package dev.bskok.checkers.piece;

import javafx.scene.paint.Color;

/** An interface which represents a Movable object that can also be colored */
public interface Colorable extends Movable {
  /**
   * Gets the color of the Colorable instance
   *
   * @return The color of the implementations' instance
   */
  Color getColor();
}
