package com.Kithlore.rules.checks;

import com.Kithlore.rules.dice.D20RollMode;

/** Optional roll mode and situational modifier for a check. */
public record CheckOptions(D20RollMode rollMode, int bonus) {
  public CheckOptions {
    if (rollMode == null) {
      throw new IllegalArgumentException("Roll mode is required");
    }
  }

  public static CheckOptions normal() {
    return new CheckOptions(D20RollMode.NORMAL, 0);
  }
}
