package dev.bskok.checkers.piece;

import dev.bskok.checkers.board.CheckersBoard;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckersPiece extends Piece implements Promotable {
  private static final Logger log = LoggerFactory.getLogger(CheckersPiece.class);

  public CheckersPiece(Color color, double radius) {
    super(color, radius);
  }

  @Override
  public boolean shouldBePromotedTo(
      PieceType pieceType, CheckersBoard checkersBoard, int row, int col) {
    return switch (pieceType) {
      case NORMAL -> true;
      case KING ->
          (this.color == Color.RED && row == checkersBoard.getRows() - 1)
              || (this.color == Color.AQUA && row == 0);
    };
  }

  @Override
  public void promoteTo(PieceType promotedType) {
    log.info("Piece at [{}, {}] has been promoted to: {}", row, col, promotedType);
    this.pieceType = promotedType;
    switch (promotedType) {
      case KING -> {
        setStroke(Color.GOLD);
        setStrokeWidth(3);
      }
      case NORMAL -> {
        setStroke(Color.DARKGRAY);
        setStrokeWidth(1);
      }
    }
  }

  public boolean shouldBePromotedToKing(CheckersBoard board, int row, int col) {
    return shouldBePromotedTo(PieceType.KING, board, row, col);
  }

  public boolean isKing() {
    return this.pieceType == PieceType.KING;
  }
}
