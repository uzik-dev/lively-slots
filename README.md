# Lively Slots

Small client-side animations for inventory slots in Minecraft (Fabric).

- **Pop-in** — an item that lands in an empty slot grows into place with a little spring.
- **Pulse** — a stack that gets bigger (furnace output, picking up more of the same) gives a short pulse.
- **Fly** — on shift-click the item glides from the slot it left to the slot it landed in.

Works in every container screen that uses vanilla slots: player inventory, chests, furnaces, crafting tables,
modded machines. The creative item list is left alone by default because it changes on every scroll.

Client-side only: install it on your game, servers do not need it. No Fabric API required.

## Configuration

`config/lively_slots.properties` is created on first launch. Durations are in milliseconds.

| Key | Default | What it does |
|---|---|---|
| `enabled` | `true` | Turns every animation on or off |
| `popIn` | `true` | Pop-in when an item appears in a slot |
| `popDurationMs` | `180` | Length of the pop-in |
| `popStartScale` | `0.5` | Size the item starts from |
| `pulseOnStackGrow` | `true` | Pulse when a stack gets bigger |
| `pulseDurationMs` | `140` | Length of the pulse |
| `pulseScale` | `1.18` | Peak size of the pulse |
| `flyOnQuickMove` | `true` | Fly animation on shift-click |
| `flyDurationMs` | `200` | Length of the flight |
| `animateCreativeInventory` | `false` | Also animate the creative inventory |
| `openGraceMs` | `200` | Ignore changes right after a screen opens (that is the server sending its contents) |

## Building

Java 25+: `./gradlew build` (builds against Minecraft 26.3; the jar runs on 26.1–26.3), the jar is in `build/libs`.

## License

MIT
