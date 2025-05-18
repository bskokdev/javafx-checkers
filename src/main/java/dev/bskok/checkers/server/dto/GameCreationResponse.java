package dev.bskok.checkers.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameCreationResponse {
    private GameDTO game;
    private Long player1Id;
    private Long player2Id;
}
