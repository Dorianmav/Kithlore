package com.Kithlore.rules.checks;

import com.Kithlore.rules.abilities.Ability;

/** A skill and the ability score used for its check. */
public record Skill(String id, Ability ability) {
  public Skill {
    if (id == null || id.isBlank() || ability == null) {
      throw new IllegalArgumentException("Skill id and ability are required");
    }
  }
}
