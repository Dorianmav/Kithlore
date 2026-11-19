package com.Kithlore.rules.checks;

import com.Kithlore.rules.abilities.Ability;
import java.util.Map;
import java.util.Set;

/** The character data needed to resolve an ability or skill check. */
public record CheckCharacter(int level, Map<Ability, Integer> abilityScores, Set<String> proficientSkills) {
  public CheckCharacter {
    if (level < 1 || level > 20 || abilityScores == null || proficientSkills == null) {
      throw new IllegalArgumentException("Valid level, ability scores and proficiencies are required");
    }
    abilityScores = Map.copyOf(abilityScores);
    proficientSkills = Set.copyOf(proficientSkills);
    if (abilityScores.size() != Ability.values().length
        || abilityScores.values().stream().anyMatch(score -> score == null || score < 1)
        || proficientSkills.stream().anyMatch(skill -> skill == null || skill.isBlank())) {
      throw new IllegalArgumentException("All six positive ability scores and valid skills are required");
    }
  }
}
