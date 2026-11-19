package com.Kithlore.rules.combat;

/** Combatant health and defensive values needed for attack resolution. */
public record Combatant(String id, int hitPoints, int maxHitPoints, int armorClass) {
  public Combatant {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("Combatant id is required");
    }
    if (maxHitPoints < 1 || hitPoints < 0 || hitPoints > maxHitPoints) {
      throw new IllegalArgumentException("Hit points must be within 0..maxHitPoints");
    }
    if (armorClass < 0) {
      throw new IllegalArgumentException("Armor class cannot be negative");
    }
  }

  public HealthState state() {
    return hitPoints == 0 ? HealthState.DOWN : HealthState.ALIVE;
  }
}
