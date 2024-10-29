package dev.bskok.checkers.events;

import dev.bskok.checkers.piece.player.Player;
import javafx.event.Event;
import javafx.event.EventType;

public class GameOverEvent extends Event {
  public static final EventType<GameOverEvent> GAME_OVER = new EventType<>(Event.ANY, "GAME_OVER");

  private final Player winner;

  public GameOverEvent(Player winner) {
    super(GAME_OVER);
    this.winner = winner;
  }

  public Player getWinner() {
    return winner;
  }
}
