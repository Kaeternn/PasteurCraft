package me.kaeternn.pasteurcraft.object.transmission;

import java.util.List;
import org.bukkit.entity.EntityType;

public class PhysicalTransmission {
    // Variables
    private List<EntityType> entities;
    private int chance;

    // Constructor
    public PhysicalTransmission(List<EntityType> entities, int chance){
        this.entities = entities;
        this.chance = chance;
    }

    // Assessors
    public List<EntityType> getEntities() { return entities; }
    public int getChance() { return chance; }
}
