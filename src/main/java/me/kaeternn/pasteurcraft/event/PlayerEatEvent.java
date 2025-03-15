package me.kaeternn.pasteurcraft.event;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.EventPriority;

public class PlayerEatEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerEatEvent(PlayerItemConsumeEvent event){
        if(event.getItem().getType().equals(Material.BREAD)){
            
        }
    }
}
