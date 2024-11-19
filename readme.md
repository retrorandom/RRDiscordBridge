# Retro-Release Discord Bridge
Bukkit plugin that lets you setup a Discord bridge on older release versions of Minecraft.

Note: I am not a java developer, although I was able to find my way around this pretty easily.

## Config
### JSON keys
| **Config**                  | **Type**                              | **Description**                                                                                       |
|-----------------------------|---------------------------------------|-------------------------------------------------------------------------------------------------------|
| **botToken**                | String                                | Token of the discord bot that will be used for the relay.                                             |
| **channelId**               | long                                  | Channel ID that the bot will use for the relay.                                                       |
| **webhookToken**            | String                                | The token of the webhook that is in the same channel as the channel ID links to.                      |
| **webhookId**               | long                                  | The ID of the webhook that is in the same channel as the channel ID links to.                         |
| **discordInvite**           | String                                | The invite link to the relay's discord server (must be filled manually).                              |
| **useDisplayNames**         | boolean                               | Use display names instead of discord usernames when relaying Discord messages to MC.                  |
| **useNicknames**            | boolean                               | Use nicknames instead of discord usernames/display names when relaying Discord messages to MC.         |
| **maxMessageSize**          | int                                   | Maximum message size that will be relayed to the MC chat.                                             |
| **publicPlayerNames**       | boolean                               | Allow player names to be sent when the /players command is used in Discord.                           |
| **publicOperatorNames**     | boolean                               | Allow operators to be highlighted when the /players command is used in Discord. Also shown when /about is ran. |
| **skinProvider**            | String                                | Changes what URL the skin images are grabbed from.                                                    |
| **broadcastSkinName**       | String                                | Skin to use when /say or /dcbroadcast is used.                                                         |
| **enabledEvents**           | List<[Events](#GameEvents)>           | Events that the bot will send to the relay channel.                                                   |
| **enabledDiscordEvents**    | List<[DiscordEvents](#DiscordEvents)> | Events that the bot will relay from the relay channel.                                                |

### GameEvents
| **Event**           | **Description**                                                        |
|---------------------|------------------------------------------------------------------------|
| **PLAYER_JOIN**     | Sends an event message on player join.                                 |
| **PLAYER_LEAVE**    | Sends an event message on player leave.                                |
| **PLAYER_KICK**     | Sends an event message on player kick.                                 |
| **PLAYER_DEATH**    | Sends an event message on player death.                                |
| **PLAYER_CHAT**     | Sends a message on player chat.                                        |
| **SERVER_START**    | Sends an event message on server start.                                |
| **SERVER_STOP**     | Sends an event message on server stop.                                 |
| **SAY_BROADCAST**   | When /say is used.                                                     |
| **FANCY_BROADCAST** | When /dcbroadcast is used.                                             |
| **ME_COMMAND**      | When /me is used.                                                      |
| **GENERIC_OTHER**   | Sends an event message when other events happen.                       |

### DiscordEvents
| **Event**             | **Description**                                                            |
|-----------------------|----------------------------------------------------------------------------|
| **USER_MESSAGE**      | When a user sends a message to the channel.                                 |
| **USER_JOIN**         | When a user joins the server (only works if the watched channel is also the system messages channel). |
| **USER_BOOST**        | When a user boosts the server (only works if the watched channel is also the system messages channel). |
| **THREAD_CREATION**   | When a user creates a thread in the channel.                               |
| **MESSAGE_PIN**       | When a message in the channel is pinned.                                   |
| **POLL_CREATION**     | When a poll is created in the channel.                                     |
| **POLL_ENDED**        | When a poll in the channel ends.                                           |
| **SLASH_COMMAND**     | When a bot command is used in the channel.                                 |
| **USER_APP**          | When a user app is used in the channel (Activities are also considered user apps). |
| **FORWARDED_MESSAGE** | When a message is forwarded to the channel.                                |


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