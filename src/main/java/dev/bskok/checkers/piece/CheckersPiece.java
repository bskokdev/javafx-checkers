package dev.bskok.checkers.piece;

import dev.bskok.checkers.board.Board;
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
      PieceType pieceType, Board checkersBoard, Player player, int row, int col) {
    return switch (pieceType) {
      case NORMAL -> true;
      case KING ->
          (player.isTop() && row == checkersBoard.getRows() - 1) || (!player.isTop() && row == 0);
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

  public boolean isKing() {
    return this.pieceType == PieceType.KING;
  }
}
