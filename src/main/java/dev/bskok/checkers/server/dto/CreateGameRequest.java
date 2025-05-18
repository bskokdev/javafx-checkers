package dev.bskok.checkers.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateGameRequest {
    private String player1Name;
    private String player1Color;
    private boolean player1IsTop;
    private String player2Name;
    private String player2Color;
    private boolean player2IsTop;
}
