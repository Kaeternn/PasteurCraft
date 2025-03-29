package me.kaeternn.pasteurcraft.listeners.physical;

import java.util.Random;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.kaeternn.pasteurcraft.PasteurCraft;
import me.kaeternn.pasteurcraft.entities.Disease;
import me.kaeternn.pasteurcraft.entities.transmission.AbstractTransmission;
import me.kaeternn.pasteurcraft.entities.transmission.PhysicalTransmission;

public class EntityAttackEvent implements Listener {
    private final PasteurCraft PLUGIN;

    public EntityAttackEvent(PasteurCraft PLUGIN) { this.PLUGIN = PLUGIN; }

    @EventHandler(priority = EventPriority.HIGH)
    private void onEntityAttackEvent(EntityDamageByEntityEvent event){
        if(event.getEntity().getType().equals(EntityType.PLAYER)){
            Player player = (Player) event.getEntity();
            
            for(Disease disease : PLUGIN.getDiseases()){
                for(AbstractTransmission transmission : disease.getTransmissions()){
                    if(transmission instanceof PhysicalTransmission){
                        PhysicalTransmission physical = (PhysicalTransmission) transmission;
                        if(physical.getChance() > new Random().nextInt(100) && physical.getList().contains(event.getDamager().getType())){
                            disease.infect(player);
                        }
                    }
                }
            }
        }
    }
}
