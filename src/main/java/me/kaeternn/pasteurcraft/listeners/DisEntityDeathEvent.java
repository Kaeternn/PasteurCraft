package me.kaeternn.pasteurcraft.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import me.kaeternn.pasteurcraft.PasteurCraft;
import me.kaeternn.pasteurcraft.UsersData;

public class DisEntityDeathEvent implements Listener {
    private final PasteurCraft PLUGIN;

    public DisEntityDeathEvent(PasteurCraft PLUGIN) { this.PLUGIN = PLUGIN; }

    @EventHandler(priority = EventPriority.HIGH)
    private void onEntityDeathEvent(EntityDeathEvent event){
        if(event. getEntity().getType().equals(EntityType.PLAYER)){
            Player player = (Player) event.getEntity();

            for(String diseases : UsersData.getOrCreate(player).getConfigurationSection("diseases").getKeys(false)){
                
            }
        }
    }
}
