package dev.bskok.checkers.events;

import dev.bskok.checkers.piece.Player;
import javafx.event.Event;
import javafx.event.EventType;
import lombok.Getter;

@Getter
public class PlayerMoveEvent extends Event {
  public static final EventType<PlayerMoveEvent> PLAYER_MOVE =
      new EventType<>(Event.ANY, "PLAYER_MOVE");

  private final Player nextTurn;
  private final int playerAPieces;
  private final int playerBPieces;

  public PlayerMoveEvent(Player nextTurn, int playerAPieces, int playerBPieces) {
    super(PLAYER_MOVE);
    this.nextTurn = nextTurn;
    this.playerAPieces = playerAPieces;
    this.playerBPieces = playerBPieces;
  }
}
