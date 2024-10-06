package dev.bskok.checkers.board;

public interface IBoardBuilder {
  IBoardBuilder initializeBoardDimensions(int rows, int cols, int tileSize);

  IBoardBuilder constructGrid();

  IBoardBuilder placePieces();

  IBoardBuilder attachEventHandlers();

  IGameBoard build();
}
