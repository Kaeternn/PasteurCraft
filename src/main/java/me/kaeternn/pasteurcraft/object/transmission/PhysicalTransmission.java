package me.kaeternn.pasteurcraft.object.transmission;

import java.util.List;
import org.bukkit.entity.EntityType;

import me.kaeternn.pasteurcraft.abstraction.AbstractTransmission;

public class PhysicalTransmission extends AbstractTransmission<EntityType> {
    public PhysicalTransmission(List<EntityType> entities, int chance){ super(entities, chance); }
}
