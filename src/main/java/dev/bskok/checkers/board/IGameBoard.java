package dev.bskok.checkers.board;

import dev.bskok.checkers.gameLogic.IGameLogic;
import javafx.scene.input.MouseEvent;

public interface IGameBoard {
  void setGameLogic(IGameLogic gameLogic);

  void placePieceAt(Piece piece, int row, int col);

  void removePieceAt(int row, int col);

  void movePiece(Piece piece, int toRow, int toCol);

  void attachOnMouseClickedEventHandler();

  void handleMouseClick(MouseEvent event);

  boolean isPositionInBounds(int row, int col);

  Piece getPieceAt(int row, int col);

  int getRows();

  int getCols();
}
