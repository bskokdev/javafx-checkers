package dev.bskok.checkers.piece;

import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import lombok.Getter;

@Getter
public class Piece extends Circle implements Movable, Selectable {
  private boolean isSelected;

  protected int row;
  protected int col;

  protected final Color color;
  protected PieceType pieceType;

  public Piece(Color color, double radius) {
    super(radius);
    this.isSelected = false;
    this.color = color;
    this.pieceType = PieceType.NORMAL;
    setFill(color);
  }

  @Override
  public void moveTo(int toRow, int toCol) {
    row = toRow;
    col = toCol;
  }

  @Override
  public void select() {
    this.isSelected = true;
  }

  @Override
  public void unselect() {
    this.isSelected = false;
  }
}
