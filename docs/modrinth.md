# Lively Slots

Your inventory, but alive. **Lively Slots** adds small, snappy animations to item slots so moving items around feels good.

## Features

- **Pop-in** — items that land in an empty slot spring into place.
- **Pulse** — a stack that grows gives a quick pulse, so you notice the furnace finishing a smelt or more cobblestone joining the pile.
- **Fly** — shift-click an item and watch it glide to where it went.

It works in every screen that uses normal slots: your inventory, chests, furnaces, crafting tables and modded machines.

## Good to know

- **Client-side only.** Install it on your game; it works on any server, vanilla or modded.
- **No Fabric API needed.** Just Fabric Loader.
- **Configurable.** `config/lively_slots.properties` lets you change speeds and sizes or turn each animation off.
- The creative item list is not animated by default (it would pop on every scroll); set `animateCreativeInventory=true` to turn it on.

## Configuration

| Key | Default | What it does |
|---|---|---|
| `enabled` | `true` | Turns every animation on or off |
| `popIn` / `popDurationMs` / `popStartScale` | `true` / `180` / `0.5` | Pop-in when an item appears |
| `pulseOnStackGrow` / `pulseDurationMs` / `pulseScale` | `true` / `140` / `1.18` | Pulse when a stack grows |
| `flyOnQuickMove` / `flyDurationMs` | `true` / `200` | Fly animation on shift-click |
| `animateCreativeInventory` | `false` | Also animate the creative inventory |

## Compatibility

Lively Slots only changes how slots are drawn, not what is in them, so it does not affect gameplay, servers or other mods' logic. If a mod draws its own slots instead of using vanilla ones, those slots simply stay still.

Fabric, Minecraft 26.1 – 26.3.
