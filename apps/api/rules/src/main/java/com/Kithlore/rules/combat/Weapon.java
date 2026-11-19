package com.Kithlore.rules.combat;

/** Attack bonus and damage expression for a weapon. */
public record Weapon(String name, int attackBonus, String damageNotation) {
  public Weapon {
    if (name == null || name.isBlank() || damageNotation == null || damageNotation.isBlank()) {
      throw new IllegalArgumentException("Weapon name and damage notation are required");
    }
  }
}
