package com.Kithlore.rules.combat;

/** Outcome of an attack, including updated target health and damage roll log. */
public record AttackResult(
    int attackRoll, int attackTotal, boolean hit, boolean critical,
    Combatant target, int damage, DamageLog damageLog) {}
