package dev.bskok.checkers.piece;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PieceType {
  NORMAL(null, 0),
  KING(Color.GOLD, 3);

  final Paint stroke;
  final int strokeWidth;
}
