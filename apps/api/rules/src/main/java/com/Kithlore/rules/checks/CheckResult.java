package com.Kithlore.rules.checks;

import com.Kithlore.rules.dice.D20Roll;

/** Complete, replayable breakdown of a resolved check. */
public record CheckResult(
    int total, CheckOutcome outcome, int dc, int abilityModifier, int proficiencyBonus,
    int situationalBonus, D20Roll d20) {}
