package me.kaeternn.pasteurcraft.object.transmission;

import java.util.List;
import org.bukkit.entity.EntityType;

public class AirTransmission {
    List<EntityType> entities;
    int radius;
    int chance;

    public AirTransmission(List<EntityType> entities, int radius, int chance){
        this.entities = entities;
        this.radius = radius;
        this.chance = chance;
    }

    public List<EntityType> getEntities() { return entities; }
    public int getRadius() { return radius; }
    public int getChance() { return chance; }
}
