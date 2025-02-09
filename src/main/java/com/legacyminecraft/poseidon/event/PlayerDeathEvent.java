package com.legacyminecraft.poseidon.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Dummy class as we use Bukkit 1.4.7 instead of Project Poseidon to build the plugin.
 */
public class PlayerDeathEvent extends EntityDeathEvent {
    public PlayerDeathEvent(LivingEntity entity, List<ItemStack> drops) {
        super(entity, drops);
    }

    public void setDeathMessage(String message) {
        throw new RuntimeException("DUMMY CLASS");
    }

    public String getDeathMessage() {
        throw new RuntimeException("DUMMY CLASS");
    }

    public void setKeepInventory(boolean keepInventory) {
        throw new RuntimeException("DUMMY CLASS");
    }

    public boolean getKeepInventory() {
        throw new RuntimeException("DUMMY CLASS");
    }
}
