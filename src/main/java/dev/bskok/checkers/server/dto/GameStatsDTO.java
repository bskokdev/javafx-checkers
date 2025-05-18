package dev.bskok.checkers.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameStatsDTO {
    private Long id;
    private int score;
    private int piecesRemaining;
    private PlayerDTO player;
}
