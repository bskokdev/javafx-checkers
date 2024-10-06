package dev.bskok.checkers.exceptions;

public class GameBoardCreationException extends RuntimeException {
  public GameBoardCreationException(String message, Throwable cause) {
    super(message, cause);
  }
}
