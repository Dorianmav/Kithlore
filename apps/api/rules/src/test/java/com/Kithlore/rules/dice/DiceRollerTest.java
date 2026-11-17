package com.Kithlore.rules.dice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DiceRollerTest {
  @Test
  void sameSeedReplaysTheSameSequence() {
    DiceRandom first = DiceRandom.fromSeed(42L);
    DiceRandom replay = DiceRandom.fromSeed(42L);

    List<DiceRoll> firstSequence = new ArrayList<>();
    List<DiceRoll> replaySequence = new ArrayList<>();
    for (int index = 0; index < 20; index++) {
      firstSequence.add(DiceRoller.roll("2d6+3", first));
      replaySequence.add(DiceRoller.roll("2d6+3", replay));
    }

    assertEquals(firstSequence, replaySequence);
  }

  @Test
  void d20DistributionIsWithinExpectedBounds() {
    DiceRandom random = DiceRandom.fromSeed(2026L);
    int[] counts = new int[20];
    int sampleSize = 100_000;

    for (int index = 0; index < sampleSize; index++) {
      counts[random.nextInt(20)]++;
    }

    for (int count : counts) {
      double percentage = count * 100.0 / sampleSize;
      org.junit.jupiter.api.Assertions.assertTrue(
          percentage >= 4.0 && percentage <= 6.0, "Face frequency out of range: " + percentage);
    }
  }

  @Test
  void parsesDiceAndModifier() {
    DiceRoll roll = DiceRoller.roll("2d6+3", DiceRandom.fromSeed(7L));

    assertEquals(2, roll.rolls().size());
    assertEquals(3, roll.modifier());
    assertEquals(roll.rolls().stream().mapToInt(Integer::intValue).sum() + 3, roll.total());
  }

  @Test
  void rejectsInvalidNotationWithTypedError() {
    for (String notation : List.of("2d6+", "0d6", "1d0", "101d6")) {
      assertThrows(
          InvalidDiceNotation.class,
          () -> DiceRoller.roll(notation, DiceRandom.fromSeed(1L)),
          notation);
    }
  }

  @Test
  void advantageKeepsTheHigherOfBothDice() {
    D20Roll roll = DiceRoller.rollD20(DiceRandom.fromSeed(3L), D20RollMode.ADVANTAGE);

    assertEquals(2, roll.rolls().size());
    assertEquals(
        roll.rolls().stream().mapToInt(Integer::intValue).max().orElseThrow(), roll.kept());
  }

  @Test
  void disadvantageKeepsTheLowerOfBothDice() {
    D20Roll roll = DiceRoller.rollD20(DiceRandom.fromSeed(3L), D20RollMode.DISADVANTAGE);

    assertEquals(2, roll.rolls().size());
    assertEquals(
        roll.rolls().stream().mapToInt(Integer::intValue).min().orElseThrow(), roll.kept());
  }
}
