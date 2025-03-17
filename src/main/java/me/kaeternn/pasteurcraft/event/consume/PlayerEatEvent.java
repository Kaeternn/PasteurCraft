package me.kaeternn.pasteurcraft.event.consume;

import java.util.Random;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import me.kaeternn.pasteurcraft.object.Disease;
import me.kaeternn.pasteurcraft.object.transmission.ConsumeTransmission;
import me.kaeternn.pasteurcraft.PasteurCraft;
import me.kaeternn.pasteurcraft.abstraction.AbstractTransmission;

import org.bukkit.event.EventPriority;

public class PlayerEatEvent implements Listener {
    private final PasteurCraft PLUGIN;

    public PlayerEatEvent(PasteurCraft PLUGIN) { this.PLUGIN = PLUGIN; }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerEatEvent(PlayerItemConsumeEvent event){
        for(Disease disease : PLUGIN.getDiseases()){
            for(AbstractTransmission transmission : disease.getTransmissions()){
                if(transmission instanceof ConsumeTransmission){
                    ConsumeTransmission consume = (ConsumeTransmission) transmission;
                    if(consume.getChance() > new Random().nextInt(100) && consume.getList().contains(event.getItem().getType())){
                        disease.infect(event.getPlayer());
                    }
                }
            }
        }
    }
}
