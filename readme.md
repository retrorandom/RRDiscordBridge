# Retro-Release Discord Bridge (RetroHaven version)
For config info, check out [this](https://github.com/DexrnZacAttack/RRDiscordBridge/wiki/Config).

Orignal Version Download: https://modrinth.com/plugin/rrdiscordbridge   


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
## Removed for RetroHaven 
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


## Tested versions
- [X] b1.7.3 (using [Project Poseidon](https://github.com/retromcorg/Project-Poseidon))
  - [LegacyPlayerChat.java](src/main/java/io/github/dexrnzacattack/rrdiscordbridge/eventcompatibility/legacy/LegacyPlayerChat.java)
  - No MOTD      
