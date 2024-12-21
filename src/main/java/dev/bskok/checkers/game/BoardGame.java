package dev.bskok.checkers.game;

import dev.bskok.checkers.board.Board;
import dev.bskok.checkers.piece.Player;
import java.util.Optional;

/** An interface which represents a game board's logic and handles all the necessary game steps */
public interface BoardGame {
  /**
   * Handles player action (move, selection) on the given (row, col) coordinates
   *
   * @param row row of the player action
   * @param col column fo the player action
   */
  void handlePlayerActionAt(int row, int col);

  /**
   * Marks the pieces present on the (row, col) coordinates as selected
   *
   * @param row row of the piece which should be selected
   * @param col column of the piece should be selected
   */
  void selectPieceAt(int row, int col);

  /** Marks the pieces currently selected as deselected */
  void deselectSelectedPiece();

  /**
   * Checks for the winner and returns an Optional of Player who's the possible winner. The Optional
   * will be empty if there's no winner yet
   *
   * @return Possible winner
   */
  Optional<Player> getWinner();

  /**
   * Injects the board dependency to the BoardGame implementation which carries the logic for the
   * given board
   *
   * @param board Board for which this implementation handles the logic
   */
  void setBoard(Board board);

  /**
   * Counts all the pieces for the given player present on the board. They are matched by color.
   *
   * @param player Player for count the pieces for
   * @return number of pieces for the given player
   */
  int getPiecesCount(Player player);
}
