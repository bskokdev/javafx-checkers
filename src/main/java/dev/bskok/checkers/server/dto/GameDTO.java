package dev.bskok.checkers.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String winnerName;
    private String winnerColor;
    private List<GameStatsDTO> gameStats;
}
