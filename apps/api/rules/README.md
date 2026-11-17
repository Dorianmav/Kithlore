# Kithlore Rules

Rules for game mechanics that can run independently of the API application.

## Dice API

Create a reproducible random stream with `DiceRandom.fromSeed(seed)` and pass it
to `DiceRoller.roll(notation, random)`. The result contains each die, the
modifier, and the total. Notation accepts `NdM`, `NdM+K`, and `NdM-K`, with 1 to
100 dice and 2 to 1,000 faces. Invalid input raises `InvalidDiceNotation`.

Use `DiceRandom.secure()` to initialize a stream from a seed drawn with
`SecureRandom`. Store the returned `seed()` alongside the game record when the
sequence needs to be replayed.

`DiceRoller.rollD20(random, mode)` supports `NORMAL`, `ADVANTAGE`, and
`DISADVANTAGE`. Its result preserves the rolled values in `rolls()` and exposes
the retained value as `kept()`.

Run the module checks from `apps/api` with `./mvnw -pl rules verify`.
