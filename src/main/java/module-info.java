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
  exports dev.bskok.checkers.exceptions;

  opens dev.bskok.checkers.board to
      javafx.fxml;

  exports dev.bskok.checkers.gameLogic;

  opens dev.bskok.checkers.gameLogic to
      javafx.fxml;

  exports dev.bskok.checkers.board;
  exports dev.bskok.checkers.DTOs;

  opens dev.bskok.checkers.DTOs to
      javafx.fxml;
}
