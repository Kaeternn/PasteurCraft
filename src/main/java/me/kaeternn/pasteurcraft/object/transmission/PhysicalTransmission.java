package me.kaeternn.pasteurcraft.object.transmission;

import java.util.List;
import org.bukkit.entity.EntityType;

public class PhysicalTransmission {
    private List<EntityType> entities;
    private int chance;

    public PhysicalTransmission(List<EntityType> entities, int chance){
        this.entities = entities;
        this.chance = chance;
    }

    public List<EntityType> getEntities() { return entities; }
    public int getChance() { return chance; }
}
