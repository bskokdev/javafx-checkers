package dev.bskok.checkers.game;

import dev.bskok.checkers.piece.player.Player;
import javafx.scene.Node;

import java.util.Optional;

public interface BoardGame {
  void handlePlayerActionAt(int row, int col);

  void selectPieceAt(int row, int col);

  Optional<Player> getWinner();

  Node getBoard();
}
