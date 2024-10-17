package dev.bskok.checkers.game;

import dev.bskok.checkers.board.CheckersBoard;
import dev.bskok.checkers.board.CheckersBoardBuilder;
import dev.bskok.checkers.board.Board;

public class BoardGameFactory {
  public static BoardGame getBoardGame(BoardGameType gameType) {
    return switch (gameType) {
      case CHECKERS -> {
        CheckersGame checkersGame = new CheckersGame();
        CheckersBoard checkersBoard = (CheckersBoard) getBoard(checkersGame, gameType);
        checkersGame.setBoard(checkersBoard);
        yield checkersGame;
      }
    };
  }

  private static Board getBoard(BoardGame game, BoardGameType gameType) {
    return switch (gameType) {
      case CHECKERS ->
          new CheckersBoardBuilder()
              .initializeBoardDimensions(8, 8, 80)
              .constructGrid()
              .placePieces()
              .attachEventHandlers(game)
              .build();
    };
  }
}
