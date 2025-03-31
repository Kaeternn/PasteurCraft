package me.kaeternn.pasteurcraft.listeners.physical;

import java.util.Random;

import org.bukkit.entity.Entity;
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

    @EventHandler(priority = EventPriority.NORMAL)
    private void onEntityAttackEvent(EntityDamageByEntityEvent event){
        if(event.getEntity().getType().equals(EntityType.PLAYER)){
            Entity damaged = event.getEntity();
            Entity damager = event.getDamager();
            
            for(Disease disease : PLUGIN.getDiseases()){
                for(AbstractTransmission transmission : disease.getTransmissions()){
                    if(transmission instanceof PhysicalTransmission){
                        PhysicalTransmission physical = (PhysicalTransmission) transmission;
                        boolean isDamagedInfectedVector = disease.isInfected((Player) damaged) && disease.getVectors().contains(damaged.getType());
                        boolean isDamagerInfectedVector = disease.isInfected((Player) damager) && disease.getVectors().contains(damager.getType());

                        if(physical.getChance() > new Random().nextInt(100)){
                            if((isDamagedInfectedVector || physical.getList().contains(damaged.getType()))
                                && damager.getType().equals(EntityType.PLAYER)){
                                disease.infect((Player) damager);
                            }

                            if(isDamagerInfectedVector || physical.getList().contains(damager.getType())
                                && damager.getType().equals(EntityType.PLAYER)){
                                disease.infect((Player) damaged);
                            }
                        }
                    }
                }
            }
        }
    }
}
