package me.kaeternn.pasteurcraft.object.transmission;

import java.util.List;
import org.bukkit.entity.EntityType;

public class AirTransmission {
    // Variables
    List<EntityType> entities;
    int radius;
    int chance;

    // Constructor
    public AirTransmission(List<EntityType> entities, int radius, int chance){
        this.entities = entities;
        this.radius = radius;
        this.chance = chance;
    }

    // Assessors
    public List<EntityType> getEntities() { return entities; }
    public int getRadius() { return radius; }
    public int getChance() { return chance; }
}
