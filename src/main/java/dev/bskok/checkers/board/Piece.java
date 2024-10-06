package dev.bskok.checkers.board;

import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import lombok.Getter;

@Getter
public class Piece extends Circle {
  private int currentRow;
  private int currentCol;

  private final Color color;
  private boolean isKing;

  public Piece(Color color, double radius) {
    super(radius);
    this.color = color;
    this.isKing = false;
    setFill(color);
  }

  public void promoteToKing() {
    isKing = true;
    setStroke(Color.GOLD);
    setStrokeWidth(3);
  }

  public void moveTo(int toRow, int toCol) {
    currentRow = toRow;
    currentCol = toCol;
  }
}
