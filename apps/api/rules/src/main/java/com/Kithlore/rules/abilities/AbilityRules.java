package com.Kithlore.rules.abilities;

/** Ability score and character-level progression rules. */
public final class AbilityRules {
  private AbilityRules() {}

  public static int modifier(int score) {
    if (score < 1) {
      throw new IllegalArgumentException("Ability score must be positive");
    }
    return Math.floorDiv(score - 10, 2);
  }

  /** Standard proficiency progression: +2 at level 1, increasing every four levels. */
  public static int proficiencyBonus(int level) {
    if (level < 1 || level > 20) {
      throw new IllegalArgumentException("Character level must be between 1 and 20");
    }
    return 2 + (level - 1) / 4;
  }
}
