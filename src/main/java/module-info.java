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

  exports dev.bskok.checkers.logic;

  opens dev.bskok.checkers.logic to
      javafx.fxml;

  exports dev.bskok.checkers.board;
}
