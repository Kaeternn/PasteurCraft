package me.kaeternn.pasteurcraft.object.transmission;

import java.util.Set;
import org.bukkit.entity.EntityType;
import me.kaeternn.pasteurcraft.abstraction.AbstractTransmission;

public class PhysicalTransmission extends AbstractTransmission<EntityType> {
    public PhysicalTransmission(Set<EntityType> entities, int chance){ super(entities, chance); }
}
