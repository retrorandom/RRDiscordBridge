package io.github.dexrnzacattack.rrdiscordbridge.eventcompatibility.legacy;

import io.github.dexrnzacattack.rrdiscordbridge.ReflectionHelper;
import org.bukkit.event.Listener;

public class PLegacyPlayerDeath implements Listener {
    public static boolean isSupported = ReflectionHelper.doesClassExist("com.legacyminecraft.poseidon.event.PlayerDeathEvent");
}
