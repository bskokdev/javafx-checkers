package dev.bskok.checkers.piece;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;

@Getter
public class Piece extends Circle implements Colorable, Selectable {
  protected final Color color;
  protected int row;
  protected int col;
  protected PieceType pieceType;

  public Piece(Color color, double radius) {
    super(radius);
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
    if (isCheckersKing()) {
      setStrokeWidth(getPieceType().strokeWidth + 5);
      return;
    }
    setStroke(Color.GREEN);
    setStrokeWidth(3);
  }

  @Override
  public void deselect() {
    if (isCheckersKing()) {
      setStrokeWidth(getPieceType().strokeWidth);
      return;
    }
    setStroke(null);
    setStrokeWidth(0);
  }

  protected boolean isCheckersKing() {
    return this instanceof CheckersPiece checkersPiece && checkersPiece.isKing();
  }
}
