package com.Kithlore.rules.checks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.Kithlore.rules.abilities.Ability;
import com.Kithlore.rules.dice.D20RollMode;
import com.Kithlore.rules.dice.DiceRandom;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CheckResolverTest {
  private static final Skill ATHLETICS = new Skill("athletics", Ability.STRENGTH);

  @Test
  void proficientLevelOneCheckAddsTwoAndReturnsBreakdown() {
    CheckResult result = CheckResolver.resolveCheck(
        character(1, Set.of("athletics")), ATHLETICS, 1, fixedRoll(10), CheckOptions.normal());
    assertEquals(12, result.total());
    assertEquals(2, result.proficiencyBonus());
    assertEquals(0, result.abilityModifier());
    assertEquals(CheckOutcome.success, result.outcome());
    assertEquals(10, result.d20().kept());
  }

  @Test
  void naturalTwentyAndOneOverrideAnyDc() {
    CheckResult criticalSuccess = CheckResolver.resolveCheck(
        character(1, Set.of()), ATHLETICS, 100, fixedRoll(20), CheckOptions.normal());
    CheckResult criticalFailure = CheckResolver.resolveCheck(
        character(1, Set.of()), ATHLETICS, 1, fixedRoll(1), CheckOptions.normal());
    assertEquals(CheckOutcome.critSuccess, criticalSuccess.outcome());
    assertEquals(CheckOutcome.critFailure, criticalFailure.outcome());
  }

  @Test
  void totalEqualToDcSucceedsAndLowerTotalFails() {
    CheckResult success = CheckResolver.resolveCheck(
        character(1, Set.of()), ATHLETICS, 10, fixedRoll(10), CheckOptions.normal());
    CheckResult failure = CheckResolver.resolveCheck(
        character(1, Set.of()), ATHLETICS, 11, fixedRoll(10), CheckOptions.normal());
    assertEquals(CheckOutcome.success, success.outcome());
    assertEquals(CheckOutcome.failure, failure.outcome());
  }

  @Test
  void unknownSkillDoesNotGrantProficiencyAndOptionsAreApplied() {
    CheckOptions options = new CheckOptions(D20RollMode.NORMAL, 3);
    CheckResult result = CheckResolver.resolveCheck(
        character(5, Set.of("other")), ATHLETICS, 16, fixedRoll(12), options);
    assertEquals(15, result.total());
    assertEquals(0, result.proficiencyBonus());
    assertEquals(3, result.situationalBonus());
    assertEquals(CheckOutcome.failure, result.outcome());
  }

  @Test
  void rejectsMissingArgumentsInvalidDcAndIncompleteCharacter() {
    CheckCharacter character = character(1, Set.of());
    assertThrows(IllegalArgumentException.class,
        () -> CheckResolver.resolveCheck(null, ATHLETICS, 10, fixedRoll(10), CheckOptions.normal()));
    assertThrows(IllegalArgumentException.class,
        () -> CheckResolver.resolveCheck(character, ATHLETICS, 0, fixedRoll(10), CheckOptions.normal()));
    assertThrows(IllegalArgumentException.class, () -> new Skill(" ", Ability.STRENGTH));
    assertThrows(IllegalArgumentException.class, () -> new CheckOptions(null, 0));
    assertThrows(IllegalArgumentException.class, () -> new CheckCharacter(1, Map.of(), Set.of()));
  }

  private static CheckCharacter character(int level, Set<String> proficiencies) {
    Map<Ability, Integer> scores = new EnumMap<>(Ability.class);
    for (Ability ability : Ability.values()) scores.put(ability, 10);
    return new CheckCharacter(level, scores, proficiencies);
  }

  /** Supplies a chosen d20 face through the same random interface used in production. */
  private static DiceRandom fixedRoll(int face) {
    for (long seed = 0; seed < 10_000; seed++) {
      DiceRandom candidate = DiceRandom.fromSeed(seed);
      if (candidate.nextInt(20) + 1 == face) return DiceRandom.fromSeed(seed);
    }
    throw new AssertionError("No seed found for d20 face " + face);
  }
}
