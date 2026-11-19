package com.Kithlore.rules.combat;

import com.Kithlore.rules.dice.DiceRoll;

/** Auditable damage dice roll, including every die and the applied modifier. */
public record DamageLog(DiceRoll roll) {
  public DamageLog {
    if (roll == null) {
      throw new IllegalArgumentException("Damage roll is required");
    }
  }
}
