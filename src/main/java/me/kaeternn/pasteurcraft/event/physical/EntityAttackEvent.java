package me.kaeternn.pasteurcraft.event.physical;

import java.util.Random;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import me.kaeternn.pasteurcraft.PasteurCraft;
import me.kaeternn.pasteurcraft.object.Disease;
import me.kaeternn.pasteurcraft.object.transmission.PhysicalTransmission;

public class EntityAttackEvent implements Listener {
    private final PasteurCraft PLUGIN;

    public EntityAttackEvent(PasteurCraft PLUGIN) { this.PLUGIN = PLUGIN; }

    @EventHandler(priority = EventPriority.HIGH)
    private void onEntityAttackEvent(EntityDamageByEntityEvent event){
        if(event.getEntity().getType().equals(EntityType.PLAYER)){
            Player player = (Player) event.getEntity();
            
            for(Disease disease : PLUGIN.getDiseases()){
                PhysicalTransmission physical = disease.getPhysical();

                if(physical != null && physical.getChance() >= new Random().nextInt(100)+1 && physical.getEntities().contains(event.getDamager().getType())){
                    disease.infect(player);}
            }
        }
    }
}
