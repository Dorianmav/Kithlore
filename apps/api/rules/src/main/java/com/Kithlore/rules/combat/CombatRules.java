package com.Kithlore.rules.combat;

import com.Kithlore.rules.dice.DiceRandom;
import com.Kithlore.rules.dice.DiceRoll;
import com.Kithlore.rules.dice.DiceRoller;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Attack resolution, damage and healing rules. */
public final class CombatRules {
  private static final Pattern DAMAGE = Pattern.compile("(\\d+)[dD](\\d+)([+-]\\d+)?");

  private CombatRules() {}

  /** Resolves a d20 attack. A natural 20 doubles dice while preserving the modifier. */
  public static AttackResult resolveAttack(
      Combatant attacker, Combatant target, Weapon weapon, DiceRandom rng) {
    if (attacker == null || target == null || weapon == null || rng == null) {
      throw new IllegalArgumentException("Attacker, target, weapon and random source are required");
    }
    int die = rng.nextInt(20) + 1;
    int total = Math.addExact(die, weapon.attackBonus());
    boolean critical = die == 20;
    boolean hit = critical || (die != 1 && total >= target.armorClass());
    if (!hit || target.state() == HealthState.DOWN) {
      return new AttackResult(die, total, false, critical, target, 0, null);
    }

    String notation = critical ? doubleDice(weapon.damageNotation()) : weapon.damageNotation();
    DiceRoll roll = DiceRoller.roll(notation, rng);
    Combatant updated = applyDamage(target, roll.total());
    return new AttackResult(die, total, true, critical, updated, roll.total(), new DamageLog(roll));
  }

  /** Applies non-negative integral damage, clamped at zero hit points. */
  public static Combatant applyDamage(Combatant target, Number amount) {
    validate(target, amount);
    int damage = amount.intValue();
    return new Combatant(
        target.id(),
        Math.max(0, target.hitPoints() - damage),
        target.maxHitPoints(),
        target.armorClass());
  }

  /** Applies non-negative integral healing, clamped at maximum hit points. */
  public static Combatant applyHealing(Combatant target, Number amount) {
    validate(target, amount);
    int healing = amount.intValue();
    return new Combatant(
        target.id(),
        Math.min(target.maxHitPoints(), Math.addExact(target.hitPoints(), healing)),
        target.maxHitPoints(),
        target.armorClass());
  }

  private static void validate(Combatant target, Number amount) {
    if (target == null) throw new IllegalArgumentException("Target is required");
    if (amount == null || amount.doubleValue() < 0 || !Double.isFinite(amount.doubleValue())
        || amount.doubleValue() != amount.intValue()) {
      throw new InvalidHitPointAmount(amount);
    }
  }

  private static String doubleDice(String notation) {
    Matcher matcher = DAMAGE.matcher(notation);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid weapon damage notation: " + notation);
    }
    int dice = Integer.parseInt(matcher.group(1));
    int doubled = Math.multiplyExact(dice, 2);
    return doubled + "d" + matcher.group(2) + (matcher.group(3) == null ? "" : matcher.group(3));
  }
}
