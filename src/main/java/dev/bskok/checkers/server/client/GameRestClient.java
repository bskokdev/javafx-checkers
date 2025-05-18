package dev.bskok.checkers.server.client;

import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.bskok.checkers.piece.Player;
import dev.bskok.checkers.server.dto.CreateGameRequest;
import dev.bskok.checkers.server.dto.GameCreationResponse;
import dev.bskok.checkers.server.dto.GameDTO;
import dev.bskok.checkers.server.dto.GameResultRequest;

public class GameRestClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GameRestClient() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public CompletableFuture<GameCreationResponse> createGame(Player player1, Player player2) {
        CreateGameRequest request = new CreateGameRequest(
                player1.name(),
                player1.color().toString(),
                player1.isTop(),
                player2.name(),
                player2.color().toString(),
                player2.isTop()
        );

        return sendPostRequest("/games", request, GameCreationResponse.class);
    }

    public CompletableFuture<GameDTO> updateGameResult(
            Long gameId,
            Player winner,
            int winnerPieces,
            Long winnerId,
            Player loser,
            int loserPieces,
            Long loserId) {

        GameResultRequest request = new GameResultRequest();
        request.setGameId(gameId);
        request.setWinnerName(winner.name());
        request.setWinnerColor(winner.color().toString());
        request.setWinnerId(winnerId);
        request.setWinnerScore(calculateScore(winnerPieces));
        request.setWinnerPieces(winnerPieces);
        request.setLoserName(loser.name());
        request.setLoserId(loserId);
        request.setLoserScore(calculateScore(loserPieces));
        request.setLoserPieces(loserPieces);

        return sendPostRequest("/game-results", request, GameDTO.class);
    }

    public CompletableFuture<List<GameDTO>> getPlayerGames(String playerName) {
        return sendGetRequest("/game-results/player?name=" + playerName, new TypeReference<List<GameDTO>>() {});
    }

    public CompletableFuture<List<GameDTO>> getRecentGames(int limit) {
        return sendGetRequest("/game-results/recent?limit=" + limit, new TypeReference<List<GameDTO>>() {});
    }

    private int calculateScore(int piecesRemaining) {
        return piecesRemaining * 10;
    }

    private <T> CompletableFuture<T> sendPostRequest(String endpoint, Object requestBody, Class<T> responseType) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            try {
                                return objectMapper.readValue(response.body(), responseType);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to parse response", e);
                            }
                        } else {
                            throw new RuntimeException("Request failed: " + response.statusCode() + " - " + response.body());
                        }
                    });
        } catch (Exception e) {
            CompletableFuture<T> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }

    private <T> CompletableFuture<T> sendGetRequest(String endpoint, TypeReference<T> typeReference) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + endpoint))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            try {
                                return objectMapper.readValue(response.body(), typeReference);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to parse response", e);
                            }
                        } else {
                            throw new RuntimeException("Request failed: " + response.statusCode() + " - " + response.body());
                        }
                    });
        } catch (Exception e) {
            CompletableFuture<T> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }
}
