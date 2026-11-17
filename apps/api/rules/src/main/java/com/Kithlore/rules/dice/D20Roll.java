package com.Kithlore.rules.dice;

import java.util.List;

/** A d20 check, preserving both die results when advantage or disadvantage applies. */
public record D20Roll(List<Integer> rolls, int kept, D20RollMode mode) {
  public D20Roll {
    rolls = List.copyOf(rolls);
  }
}
