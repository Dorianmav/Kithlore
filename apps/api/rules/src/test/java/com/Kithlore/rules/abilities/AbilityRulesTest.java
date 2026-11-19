package com.Kithlore.rules.abilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbilityRulesTest {
  @Test
  void computesModifiersIncludingNegativeOddScores() {
    assertEquals(-1, AbilityRules.modifier(8));
    assertEquals(0, AbilityRules.modifier(10));
    assertEquals(0, AbilityRules.modifier(11));
    assertEquals(5, AbilityRules.modifier(20));
    assertEquals(-5, AbilityRules.modifier(1));
  }

  @Test
  void computesProficiencyByLevelBands() {
    assertEquals(2, AbilityRules.proficiencyBonus(1));
    assertEquals(2, AbilityRules.proficiencyBonus(4));
    assertEquals(3, AbilityRules.proficiencyBonus(5));
    assertEquals(6, AbilityRules.proficiencyBonus(17));
    assertEquals(6, AbilityRules.proficiencyBonus(20));
  }

  @Test
  void rejectsInvalidScoresAndLevels() {
    assertThrows(IllegalArgumentException.class, () -> AbilityRules.modifier(0));
    assertThrows(IllegalArgumentException.class, () -> AbilityRules.proficiencyBonus(0));
    assertThrows(IllegalArgumentException.class, () -> AbilityRules.proficiencyBonus(21));
  }
}
