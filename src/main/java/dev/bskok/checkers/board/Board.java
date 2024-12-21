package dev.bskok.checkers.board;

import dev.bskok.checkers.game.BoardGame;
import dev.bskok.checkers.piece.Colorable;
import dev.bskok.checkers.piece.Player;
import java.util.List;
import java.util.Optional;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.layout.GridPane;

/** An interface which represents a game board which contains pieces and additional inner state */
public interface Board {
  /**
   * Gets the JavaFX GridPane object of the Board implementation
   *
   * @return the GridPane of the board
   */
  GridPane getPane();

  /**
   * Attaches an event handler to the Board implementation
   *
   * @param eventType specific EventType of type T
   * @param handler specific event handler
   * @param <T> type of the event
   */
  <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<? super T> handler);

  /**
   * Fires a new event, for example when game ends
   *
   * @param event an event to be sent
   */
  void fireEvent(Event event);

  /**
   * Places a Colorable piece on the given Board implementation
   *
   * @param piece Colorable piece to be placed
   * @param row row to place the piece at
   * @param col column to place the pice at
   */
  void placePieceAt(Colorable piece, int row, int col);

  /**
   * Retrieves the Optional of a Colorable pieces on the Board implementation, since the pieces
   * aren't present on every cell in the board grid
   *
   * @param row row of the piece
   * @param col column of the piece
   * @return Optional of Colorable piece
   */
  Optional<Colorable> getPieceAt(int row, int col);

  /**
   * Removes a piece on the given coordinates from the Board implementation's grid state
   *
   * @param row row to remove the piece from
   * @param col column to remove the piece from
   */
  void removePieceAt(int row, int col);

  /**
   * Moves a Colorable piece within the board's state to the desired coordinates
   *
   * @param piece Piece to be moved
   * @param toRow row to move the piece to
   * @param toCol column to mvoe the piece to
   */
  void movePieceOnBoard(Colorable piece, int toRow, int toCol);

  /**
   * Attaches an onClick event handler to the Board implementation
   *
   * @param boardGame Game logic which is attached to the given implementation
   */
  void attachOnClickEventHandler(BoardGame boardGame);

  /**
   * Checks whether the given coordinates are out of bounds for the given board's grid
   *
   * @param row row to be checked
   * @param col column to be checked
   * @return true is the position is out of bounds, false if it's valid
   */
  boolean isPositionOutOfBounds(int row, int col);

  /**
   * Retrieves all the pieces from the board's state
   *
   * @return 2D grid of Colorable pieces which represent the pieces on the board
   */
  Colorable[][] getPieces();

  /**
   * Retrieves the number of rows in the board's grid
   *
   * @return number of rows in the grid
   */
  int getRows();

  /**
   * Retrieves the number of columns in the board's grid
   *
   * @return number of columns in the grid
   */
  int getCols();

  /**
   * Returns a list of lists of the possible moves for the player. Top player can only move
   * downwards and bottom player can only move up with their non-king pieces.
   *
   * @param player Player to get the delta coordinates for
   * @return List of possible delta coordinates for the given player
   */
  List<List<Integer>> getDeltasForPlayer(Player player);
}
