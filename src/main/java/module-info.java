module dev.bskok.checkers {
  requires javafx.controls;
  requires javafx.fxml;
  requires static lombok;
  requires ch.qos.logback.classic;
  requires org.slf4j;
  exports dev.bskok.checkers.server.dto;
  requires ch.qos.logback.core;
    requires java.net.http;
  requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;

    opens dev.bskok.checkers to
      javafx.fxml;

  exports dev.bskok.checkers;

  opens dev.bskok.checkers.board to
      javafx.fxml;

  exports dev.bskok.checkers.board;
  exports dev.bskok.checkers.piece;

  opens dev.bskok.checkers.piece to
      javafx.fxml;

    exports dev.bskok.checkers.game;

  opens dev.bskok.checkers.game to
      javafx.fxml;
    exports dev.bskok.checkers.controller;
    opens dev.bskok.checkers.controller to javafx.fxml;
}
