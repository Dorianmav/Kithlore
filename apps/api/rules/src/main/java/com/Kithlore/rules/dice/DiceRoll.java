package com.Kithlore.rules.dice;

import java.util.List;

/** The individual die results and computed total for a dice expression. */
public record DiceRoll(String notation, List<Integer> rolls, int modifier, int total) {
  public DiceRoll {
    rolls = List.copyOf(rolls);
  }
}
