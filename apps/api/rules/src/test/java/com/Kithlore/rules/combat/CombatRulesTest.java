package com.Kithlore.rules.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.Kithlore.rules.dice.DiceRandom;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class CombatRulesTest {
  @Test
  void damageCannotReduceHitPointsBelowZeroAndZeroMeansDown() {
    Combatant result = CombatRules.applyDamage(new Combatant("target", 5, 20, 12), 9);

    assertEquals(0, result.hitPoints());
    assertEquals(HealthState.DOWN, result.state());
  }

  @Test
  void healingCannotExceedMaximumHitPoints() {
    Combatant result = CombatRules.applyHealing(new Combatant("target", 18, 20, 12), 10);

    assertEquals(20, result.hitPoints());
    assertEquals(HealthState.ALIVE, result.state());
  }

  @Test
  void criticalDoublesDamageDiceAndPreservesModifierInTheDamageLog() {
    Combatant attacker = new Combatant("attacker", 10, 10, 10);
    Combatant target = new Combatant("target", 20, 20, 10);
    long criticalSeed = IntStream.range(0, 10_000)
        .filter(seed -> DiceRandom.fromSeed(seed).nextInt(20) == 19)
        .findFirst()
        .orElseThrow();
    AttackResult result = CombatRules.resolveAttack(
        attacker, target, new Weapon("sword", 3, "1d8+2"), DiceRandom.fromSeed(criticalSeed));

    assertTrue(result.critical());
    assertTrue(result.hit());
    assertEquals("2d8+2", result.damageLog().roll().notation());
    assertEquals(2, result.damageLog().roll().rolls().size());
    assertEquals(2, result.damageLog().roll().modifier());
    assertEquals(result.damageLog().roll().total(), result.damage());
  }

  @Test
  void missesDoNotRollOrApplyDamage() {
    AttackResult result = CombatRules.resolveAttack(
        new Combatant("attacker", 10, 10, 10), new Combatant("target", 20, 20, 30),
        new Weapon("sword", 0, "1d8+2"), DiceRandom.fromSeed(1));

    assertFalse(result.hit());
    assertEquals(20, result.target().hitPoints());
    assertEquals(null, result.damageLog());
  }

  @Test
  void rejectsNegativeAndFractionalAmountsWithTypedError() {
    Combatant target = new Combatant("target", 10, 20, 10);

    assertThrows(InvalidHitPointAmount.class, () -> CombatRules.applyDamage(target, -1));
    assertThrows(InvalidHitPointAmount.class, () -> CombatRules.applyDamage(target, 1.5));
    assertThrows(InvalidHitPointAmount.class, () -> CombatRules.applyHealing(target, -0.5));
  }
}
