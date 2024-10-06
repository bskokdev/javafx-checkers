package dev.bskok.checkers.gameLogic;

import dev.bskok.checkers.DTOs.Move;
import javafx.scene.paint.Color;

public interface IGameLogic {
  void handlePlayerActionAt(int row, int col);

  void selectPieceAt(int row, int col);

  void deselectSelectedPiece();

  boolean existsWinner();

  boolean areValidMovesLeftForPlayer(Color playerColor);

  boolean doesPlayerHavePiecesLeft(Color playerColor);

  void handlePlayerMove(Move move);

  boolean isMoveValid(Move move);

  void handleCaptureMove(Move move);

  boolean isCaptureDuringMove(Move move);

  void swapPlayerTurns();
}
