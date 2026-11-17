package com.Kithlore.rules.dice;

/** Raised when a dice expression is malformed or outside supported bounds. */
public final class InvalidDiceNotation extends IllegalArgumentException {
  public InvalidDiceNotation(String notation) {
    super("Invalid dice notation: " + notation);
  }
}
