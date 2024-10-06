package dev.bskok.checkers.board;

import dev.bskok.checkers.exceptions.GameBoardCreationException;
import dev.bskok.checkers.gameLogic.CheckersGameLogic;
import dev.bskok.checkers.gameLogic.IGameLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameBoardFactory {
  public static final Logger log = LoggerFactory.getLogger(GameBoardFactory.class);

  public static IGameBoard getGameBoard(BoardType boardType, int rows, int cols, int tileSize)
      throws GameBoardCreationException {
    try {
      IBoardBuilder boardBuilder = getBoardBuilder(boardType);
      IGameBoard board =
          boardBuilder
              .initializeBoardDimensions(rows, cols, tileSize)
              .constructGrid()
              .placePieces()
              .attachEventHandlers()
              .build();

      IGameLogic gameLogic = getGameLogicForBoardType(boardType, board);
      board.setGameLogic(gameLogic);

      log.info("Factory created {} game board", boardType);
      return board;
    } catch (Exception e) {
      log.error("Failed to create game board: {}", e.getMessage());
      throw new GameBoardCreationException("Failed to create game board for " + boardType, e);
    }
  }

  private static IGameLogic getGameLogicForBoardType(BoardType boardType, IGameBoard board)
      throws UnsupportedOperationException {
    return switch (boardType) {
      case CHECKERS -> new CheckersGameLogic(board);
      case CHESS, TIC_TAC_TOE -> {
        log.error("Game logic not implemented for {}", boardType);
        throw new UnsupportedOperationException("Game logic not implemented for: " + boardType);
      }
    };
  }

  private static IBoardBuilder getBoardBuilder(BoardType boardType)
      throws UnsupportedOperationException {
    return switch (boardType) {
      case CHECKERS -> new CheckersBoardBuilder();
      case CHESS, TIC_TAC_TOE -> {
        log.error("No builder available for board type: {}", boardType);
        throw new UnsupportedOperationException("Board type not supported: " + boardType);
      }
    };
  }
}
