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

public interface Board {
  GridPane getPane();

  <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<? super T> handler);

  void fireEvent(Event event);

  void placePieceAt(Colorable piece, int row, int col);

  Optional<Colorable> getPieceAt(int row, int col);

  void removePieceAt(int row, int col);

  void movePieceOnBoard(Colorable piece, int toRow, int toCol);

  void attachOnClickEventHandler(BoardGame boardGame);

  boolean isPositionOutOfBounds(int row, int col);

  Colorable[][] getPieces();

  int getRows();

  int getCols();

  List<List<Integer>> getDeltasForPlayer(Player player);
}
