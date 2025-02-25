# PermissionWar

**PermissionWar** is a Minecraft Bukkit/Spigot plugin that introduces a competitive, team-based game mode where players battle by locking and unlocking in-game permissions. With built-in economy integration via Vault and configurable costs, this plugin brings a fresh twist to server gameplay by blending team strategy, economic management, and interactive permission control.

## Features

- **Team-Based Gameplay:**  
  Players can join the Red or Blue team. The plugin manages team assignments and scores, updating display names and sending team-specific messages.

- **Economy Integration:**  
  Utilizes Vault for in-game transactions. Players spend money to lock opponents’ permissions or unlock their own, while earning rewards over time during the game.

- **Permission Lock/Unlock Mechanics:**  
  Lock or unlock various actions (crouching, jumping, attacking, moving, running, NPC interaction, block breaking/placing, and item dropping) with costs defined in the configuration. Includes options for manual or random lock/unlock.

- **Interactive GUI Menu:**  
  Sneak and swap your hand items to open a custom inventory GUI that offers options such as "Lock Permission" and "Unlock Permission" for intuitive control.

- **Real-Time Scoreboard:**  
  Displays each player’s team, team score, and current balance, updating in real time throughout the game.

- **Custom Game Timer:**  
  Games run on a 40-minute timer with periodic money deposits and scoreboard updates to maintain dynamic gameplay.

- **Cheque System:**  
  Use special cheque items (customized paper with lore) to instantly add funds to your balance upon use.

## Installation

### Requirements

- A Bukkit/Spigot Minecraft server (compatible with Minecraft 1.20)
- [Vault](https://www.spigotmc.org/resources/vault.34315/) installed along with a compatible economy plugin (e.g., Essentials Economy)

### Build

The project uses Maven for dependency management. Build the plugin with:

```bash
mvn clean package
```

### Configuration

You can change Integer value of config.yml

```bash
lock-cost:
  앉기: 10
  점프: 10
  때리기: 15
  이동하기: 20
  달리기: 20
  NPC상호작용하기: 15
  블럭캐기: 25
  블럭설치하기: 25
  버리기: 10

unlock-cost:
  앉기: 5
  점프: 5
  때리기: 7
  이동하기: 10
  달리기: 10
  NPC상호작용하기: 7
  블럭캐기: 12
  블럭설치하기: 12
  버리기: 5

```

### Commands

```bash
/명령어
```
