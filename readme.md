# Retro-Release Discord Bridge
Bukkit plugin that lets you setup a Discord bridge on older release versions of Minecraft.

Note: I am not a java developer, although I was able to find my way around this pretty easily.

## Tested versions
- [ ] 1.0   
  - (Event registering was really different in this version, I couldn't get it working using reflection.) 
- [X] 1.1
  - [LegacyPlayerChat.java](/src/main/java/io/github/dexrnzacattack/rrdiscordbridge/eventcompatibility/legacy/LegacyPlayerChat.java)
  - [LegacyPlayerDeath.java](/src/main/java/io/github/dexrnzacattack/rrdiscordbridge/eventcompatibility/legacy/LegacyPlayerDeath.java)
- [X] 1.2.5
  - [LegacyPlayerChat.java](/src/main/java/io/github/dexrnzacattack/rrdiscordbridge/eventcompatibility/legacy/LegacyPlayerChat.java)
- [X] 1.3.2
- [X] 1.4.7 (what was initially being used for development)
- [X] 1.5.2
- [X] 1.6.4
- [X] 1.7.2
- [X] 1.8.8
- [X] 1.9.4
- [X] 1.10.2