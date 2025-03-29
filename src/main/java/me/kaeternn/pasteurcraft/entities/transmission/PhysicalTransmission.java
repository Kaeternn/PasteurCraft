package me.kaeternn.pasteurcraft.entities.transmission;

import java.util.Set;
import org.bukkit.entity.EntityType;

public class PhysicalTransmission extends AbstractTransmission<EntityType> {
    public PhysicalTransmission(Set<EntityType> entities, int chance){ super(entities, chance); }
}
