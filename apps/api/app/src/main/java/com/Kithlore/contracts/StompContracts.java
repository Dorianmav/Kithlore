package com.Kithlore.contracts;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Java source of truth for the STOMP payloads documented in the product contract. */
public final class StompContracts {

  private StompContracts() {}

  public record RoomJoin(String type, String roomId) {}

  public record ActionSubmit(String type, String clientActionId, String roundId, String text) {}

  public record RoundForceResolve(String type, String roundId) {}

  public record StateResync(String type, long sinceSeq) {}

  public record StateSnapshot(String type, long seq, JsonNode state) {}

  public record StatePatch(String type, long seq, List<PatchOperation> ops) {}

  public record PatchOperation(
      String op,
      Optional<String> characterId,
      Optional<String> itemId,
      Optional<Integer> qty,
      Optional<String> abilityId,
      Optional<String> rollId,
      Optional<Integer> delta,
      Optional<String> key,
      Optional<JsonNode> value,
      Optional<String> name,
      Optional<String> description,
      Optional<String> attitude,
      Optional<String> locationId) {}

  public record PresenceUpdate(String type, String playerId, boolean connected) {}

  public record RoundPhase(
      String type, String roundId, String phase, Optional<Instant> deadlineAt) {}

  public record RollResult(
      String type,
      String rollId,
      String playerId,
      String formula,
      List<Integer> dice,
      int total,
      int dc,
      String outcome) {}

  public record NarrationChunk(String type, String roundId, String text) {}

  public record NarrationDone(String type, String roundId, List<String> choices) {}

  public record RoundDegraded(String type, String roundId, String reason) {}

  public record ErrorMessage(String type, String code, String message) {}
}
