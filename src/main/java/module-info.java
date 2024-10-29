module dev.bskok.checkers {
  requires javafx.controls;
  requires javafx.fxml;
  requires static lombok;
  requires ch.qos.logback.classic;
  requires org.slf4j;
  requires ch.qos.logback.core;

  opens dev.bskok.checkers to
      javafx.fxml;

  exports dev.bskok.checkers;

  opens dev.bskok.checkers.board to
      javafx.fxml;

  exports dev.bskok.checkers.board;
  exports dev.bskok.checkers.piece;

  opens dev.bskok.checkers.piece to
      javafx.fxml;

  exports dev.bskok.checkers.piece.player;
  exports dev.bskok.checkers.game;

  opens dev.bskok.checkers.game to
      javafx.fxml;
    exports dev.bskok.checkers.controller;
    opens dev.bskok.checkers.controller to javafx.fxml;
}
