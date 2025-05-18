package dev.bskok.checkers.game;

import java.time.LocalDateTime;

public record GameResult(
    String player1Name, String player2Name, int player1Pieces, int player2Pieces, LocalDateTime gameDate) {
  public GameResult(String player1Name, String player2Name, int player1Pieces, int player2Pieces) {
      this(player1Name, player2Name, player1Pieces, player2Pieces, LocalDateTime.now());
  }

  public String getCsvHeader() {
    return "Player1;Player2;Player1Pieces;Player2Pieces";
  }

  public String getCsvDataRow() {
    return String.format("%s;%s;%d;%d", player1Name, player2Name, player1Pieces, player2Pieces);
  }
}
