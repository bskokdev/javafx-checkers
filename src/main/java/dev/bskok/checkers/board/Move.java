package dev.bskok.checkers.board;

// Represents a single move on the board from (row, col) to (row + deltaY, col + deltaX)
public record Move(int fromRow, int fromCol, int toRow, int toCol) {}
