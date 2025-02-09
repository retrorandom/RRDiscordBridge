# Retro-Release Discord Bridge
Discord bridge/relay plugin for both [old and new release (plus some Beta) versions](#tested-versions) (b1.7.3*-1.21.1) of Minecraft.   

For config info, check out [this](https://github.com/DexrnZacAttack/RRDiscordBridge/wiki/Config).

Download: https://modrinth.com/plugin/rrdiscordbridge   
Note: I am not a java developer, although I was able to find my way around this pretty easily.

## Features
- Message Relay
- [Version support](#tested-versions)
- Broadcasting (/dcbroadcast)
- Server Stats from Discord (/about)
- Relays the following to Discord (configurable)
  - Player Join
  - Player Leave
  - Player Kick
  - Player Death
  - Player Chat
  - Server Start
  - Server Stop
  - /say
  - /me
- Relays the following information from Discord (configurable)
  - Channel message
  - User join (if the relay channel is also the system messages channel)
  - Server boost (if the relay channel is also the system messages channel)
  - Thread creation
  - Message pin
  - Poll created/ended (with results)
  - Slash commands used
  - User app used (also activities)
  - Message forwarded (probably not complete)
- Chat Extensions (configurable)
  - **Extra info available in-game by typing `/cext help`**.
  - Waypoint embed
    - Allows for Xaero's Minimap and JourneyMap/VoxelMap waypoints to be embedded in the relay channel.
  - Operator Chat
    - Allows for communicating between ops and optionally a (likely private) discord channel.
    - Inspired by MCGalaxy's OPChat feature.
    - Syntax: ## \<msg\>

## Tested versions
- [X] b1.7.3 (using [Project Poseidon](https://github.com/retromcorg/Project-Poseidon))
  - [LegacyPlayerChat.java](src/main/java/io/github/dexrnzacattack/rrdiscordbridge/eventcompatibility/legacy/LegacyPlayerChat.java)
  - No death messages (yet)
  - No MOTD
- [ ] b1.8.1
- [ ] 1.0   
  - (Event registering was really different in this version, I couldn't get it working using reflection.) 
- [X] 1.1
  - [LegacyPlayerChat.java](src/main/java/io/github/dexrnzacattack/rrdiscordbridge/eventcompatibility/legacy/LegacyPlayerChat.java)
  - [LegacyPlayerDeath.java](src/main/java/io/github/dexrnzacattack/rrdiscordbridge/eventcompatibility/legacy/LegacyPlayerDeath.java)
  - No MOTD
- [X] 1.2.5
  - [LegacyPlayerChat.java](src/main/java/io/github/dexrnzacattack/rrdiscordbridge/eventcompatibility/legacy/LegacyPlayerChat.java)
- [X] 1.3.2
- [X] 1.4.7 (Being used for development)
- [X] 1.5.2
- [X] 1.6.4
- [X] 1.7.2
- [X] 1.8.8
- [X] 1.9.4
- [X] 1.10.2
- [X] 1.14.4
- [X] 1.15.2
- [X] 1.16.5
- [X] 1.17.1
- [X] 1.18.2
- [X] 1.21.1
- [X] 1.21.4
