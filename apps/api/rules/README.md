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

## Combat API

Create `Combatant` values with current hit points, maximum hit points, and armor
class, and describe a weapon with its attack bonus and damage notation. Call
`CombatRules.resolveAttack(attacker, target, weapon, rng)` to roll the d20 attack
and damage. A natural 20 doubles the damage dice and preserves the modifier;
`AttackResult.damageLog()` records the damage notation, individual dice, and
modifier. `CombatRules.applyDamage` and `applyHealing` clamp hit points to
`0..maxHitPoints`; `Combatant.state()` reports `DOWN` at zero and `ALIVE`
otherwise. Negative, fractional, or non-finite hit point amounts raise the typed
`InvalidHitPointAmount` exception.

Run the module checks from `apps/api` with `./mvnw -pl rules verify`.
