package me.kaeternn.pasteurcraft.entities.transmission;

import java.util.Set;
import org.bukkit.entity.EntityType;

public class AirTransmission extends AbstractTransmission<EntityType> {
    // Variables
    int radius;

    // Constructor
    public AirTransmission(Set<EntityType> entities, int radius, int chance){
        super(entities, chance);
        this.radius = radius;
    }

    // Assessors
    public int getRadius() { return radius; }
}
