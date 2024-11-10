package dev.bskok.checkers.game;

import dev.bskok.checkers.piece.Player;

public record GameSettings(int rows, int cols, Player playerA, Player playerB) {}
