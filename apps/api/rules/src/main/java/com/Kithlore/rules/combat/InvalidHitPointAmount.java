package com.Kithlore.rules.combat;

/** Typed validation error for invalid damage or healing amounts. */
public final class InvalidHitPointAmount extends IllegalArgumentException {
  public InvalidHitPointAmount(Number amount) {
    super("Hit point amount must be a non-negative integer: " + amount);
  }
}
