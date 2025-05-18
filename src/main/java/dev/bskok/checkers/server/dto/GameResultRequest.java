package dev.bskok.checkers.server.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GameResultRequest {
    private Long gameId;
    private String winnerName;
    private String winnerColor;
    private Long winnerId;
    private int winnerScore;
    private int winnerPieces;
    private String loserName;
    private Long loserId;
    private int loserScore;
    private int loserPieces;
}
