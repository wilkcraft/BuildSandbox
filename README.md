# 🧱 Build Sandbox

**Build Sandbox** is a NeoForge mod for Minecraft 1.21.1 that introduces an isolated creative test dimension designed for unrestricted building while preserving survival progression integrity.

It provides a controlled environment where players can design, experiment, and iterate builds without impacting their survival world state.

---

## 📥 Download

* 🔗 [Download on Modrinth](https://modrinth.com/mod/build-sandbox)
* 🔗 [Download on CurseForge](https://www.curseforge.com/minecraft/mc-mods/build-sandbox)

---

## ✨ Features

### 🌍 Isolated Sandbox Dimension

* Adds a dedicated flat creative dimension (`buildsandbox:sandbox`) for construction and testing.
* Fully separated from the survival world state (inventory, position, and game mode tracking are independent).
* No direct interaction or persistence crossover with survival gameplay.

### 🎮 Dimension Switching System

* Players toggle between survival and sandbox mode using `/buildsandbox`.

**First execution (enter sandbox):**

* Persists survival dimension, position, and inventory.
* Transfers player to the sandbox dimension.
* Switches game mode to Creative.
* Loads previously saved sandbox state if available.

**Second execution (return to survival):**

* Persists sandbox position and inventory.
* Restores survival inventory and position.
* Switches game mode back to Survival.

---

### 🎒 Persistent Dual Inventory System

* Maintains separate inventories for survival and sandbox environments.
* Inventory state is preserved independently per dimension session.
* Ensures deterministic restoration when switching contexts.

---

### 🚫 Sandbox Restrictions System

The sandbox dimension enforces controlled building conditions:

* ❌ Prevents natural mob spawning
* ❌ Blocks portal creation (Nether / End portals)
* ❌ Prevents fire and portal-related block generation
* ❌ Fully isolated gameplay loop from survival mechanics

---

### 🧠 Controlled Dimension Travel

* Dimension transitions are strictly controlled via the mod command system.
* Unauthorized or external teleportation into/out of the sandbox is restricted.
* Internal travel permissions are validated through a per-player state system.

---

## 📦 Internal Architecture

The mod is built around a UUID-based persistent player state model:

* Stores per-player data:

  * Survival dimension (`ResourceKey<Level>`)
  * Survival position (`BlockPos`)
  * Survival inventory snapshot
  * Sandbox position
  * Sandbox inventory snapshot

This ensures consistent state restoration across sessions and prevents data desynchronization.

---

## 🧪 Intended Use Cases

Build Sandbox is designed for:

* 🏗️ High-fidelity structure prototyping
* 🧱 Safe creative construction without survival impact
* 🧪 Iterative design and experimentation workflows
* 🛠️ Server-side build training environments

---

## ⚙️ Command Interface

### `/buildsandbox`

Primary toggle command between environments:

* 🟢 Survival context
* 🟣 Sandbox creative context

---

## 🛑 Compatibility

* Requires **NeoForge 1.21.1**
* Compatible with:

  * Singleplayer environments
  * Multiplayer / dedicated servers

---

## 📁 Technical Implementation

* Custom dimension: `buildsandbox:sandbox`
* Flat world generator
* Event-driven restriction system (mob spawning, portals, blocks)
* Persistent player state management via UUID mapping
* Controlled teleportation and travel validation layer

---

## 🚀 Planned Features

* GUI-based sandbox selector replacing or complementing `/buildsandbox`
* Structure generation control refinement per biome or region
* Export system for saving and sharing builds
* Server permission system for sandbox access control
* Advancement system isolation improvements (full sandbox lockout)

---

## 👤 Author

Developed by **[Wilkcraft](https://github.com/wilkcraft)**

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

🚀 Build freely, safely, and without affecting your survival world in a fully isolated creative sandbox dimension!
