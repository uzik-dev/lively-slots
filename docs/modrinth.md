<!--
Modrinth page draft.
Title: Lively Slots
Summary (max 256 chars): Items pop into inventory slots, stacks pulse when they grow, and shift-clicked items fly to their new slot. Small, client-side, no Fabric API needed.
Categories: decoration, utility (client-side)
Environment: client required, server unsupported
Loader: Fabric; Minecraft: 26.3
License: MIT
-->

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
- The creative item list is not animated by default (it would pop on every scroll); `animateCreativeInventory=true` turns it on.

## Compatibility

Lively Slots only changes how slots are drawn, not what is in them, so it does not affect gameplay, servers or other mods' logic. If a mod draws its own slots instead of using vanilla ones, those slots simply stay still.

---

Made for Fabric on Minecraft 26.3. Source code and issues: *(link to the repository)*.
