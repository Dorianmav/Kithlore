package com.Kithlore.rules.checks;

import com.Kithlore.rules.abilities.AbilityRules;
import com.Kithlore.rules.dice.DiceRandom;
import com.Kithlore.rules.dice.DiceRoller;

/** Resolves deterministic d20 ability and skill checks. */
public final class CheckResolver {
  private CheckResolver() {}

  public static CheckResult resolveCheck(
      CheckCharacter character, Skill skill, int dc, DiceRandom rng, CheckOptions options) {
    if (character == null || skill == null || rng == null || options == null) {
      throw new IllegalArgumentException("Character, skill, random source and options are required");
    }
    if (dc < 1) {
      throw new IllegalArgumentException("DC must be positive");
    }
    Integer score = character.abilityScores().get(skill.ability());
    if (score == null) {
      throw new IllegalArgumentException("Character is missing ability score: " + skill.ability());
    }
    int abilityModifier = AbilityRules.modifier(score);
    int proficiency = character.proficientSkills().contains(skill.id())
        ? AbilityRules.proficiencyBonus(character.level()) : 0;
    var d20 = DiceRoller.rollD20(rng, options.rollMode());
    int total = Math.addExact(Math.addExact(Math.addExact(d20.kept(), abilityModifier), proficiency), options.bonus());
    CheckOutcome outcome;
    if (d20.kept() == 20) outcome = CheckOutcome.critSuccess;
    else if (d20.kept() == 1) outcome = CheckOutcome.critFailure;
    else outcome = total >= dc ? CheckOutcome.success : CheckOutcome.failure;
    return new CheckResult(total, outcome, dc, abilityModifier, proficiency, options.bonus(), d20);
  }
}
