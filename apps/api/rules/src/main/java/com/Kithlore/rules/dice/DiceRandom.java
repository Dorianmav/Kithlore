package com.Kithlore.rules.dice;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/** Random source used by dice rolls, with a replayable seed. */
public final class DiceRandom {
  private static final String GENERATOR_NAME = "L64X128MixRandom";

  private final long seed;
  private final RandomGenerator generator;

  private DiceRandom(long seed) {
    this.seed = seed;
    this.generator = RandomGeneratorFactory.of(GENERATOR_NAME).create(seed);
  }

  /** Creates a deterministic random source for a given seed. */
  public static DiceRandom fromSeed(long seed) {
    return new DiceRandom(seed);
  }

  /** Creates a random source initialized with a cryptographically generated seed. */
  public static DiceRandom secure() {
    return new DiceRandom(new SecureRandom().nextLong());
  }

  public long seed() {
    return seed;
  }

  /** Returns a value in the half-open interval [0, bound). */
  public int nextInt(int bound) {
    return generator.nextInt(bound);
  }
}
