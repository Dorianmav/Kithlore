package com.Kithlore.rules.dice;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parses and resolves standard tabletop dice notation. */
public final class DiceRoller {
  private static final Pattern NOTATION = Pattern.compile("(\\d+)[dD](\\d+)([+-]\\d+)?");
  private static final int MAX_DICE = 100;
  private static final int MAX_FACES = 1_000;

  private DiceRoller() {}

  public static DiceRoll roll(String notation, DiceRandom random) {
    if (notation == null || random == null) {
      throw new InvalidDiceNotation(String.valueOf(notation));
    }

    Matcher matcher = NOTATION.matcher(notation);
    if (!matcher.matches()) {
      throw new InvalidDiceNotation(notation);
    }

    try {
      int count = Integer.parseInt(matcher.group(1));
      int faces = Integer.parseInt(matcher.group(2));
      int modifier = matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3));
      if (count < 1 || count > MAX_DICE || faces < 2 || faces > MAX_FACES) {
        throw new InvalidDiceNotation(notation);
      }

      List<Integer> rolls = new ArrayList<>(count);
      int total = modifier;
      for (int index = 0; index < count; index++) {
        int result = random.nextInt(faces) + 1;
        rolls.add(result);
        total = Math.addExact(total, result);
      }
      return new DiceRoll(notation, rolls, modifier, total);
    } catch (NumberFormatException | ArithmeticException exception) {
      throw new InvalidDiceNotation(notation);
    }
  }

  public static D20Roll rollD20(DiceRandom random, D20RollMode mode) {
    if (random == null || mode == null) {
      throw new IllegalArgumentException("Random source and roll mode are required");
    }
    int first = random.nextInt(20) + 1;
    if (mode == D20RollMode.NORMAL) {
      return new D20Roll(List.of(first), first, mode);
    }
    int second = random.nextInt(20) + 1;
    int kept = mode == D20RollMode.ADVANTAGE ? Math.max(first, second) : Math.min(first, second);
    return new D20Roll(List.of(first, second), kept, mode);
  }
}
