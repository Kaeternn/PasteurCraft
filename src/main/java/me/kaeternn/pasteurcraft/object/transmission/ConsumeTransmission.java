package me.kaeternn.pasteurcraft.object.transmission;

import java.util.Set;
import org.bukkit.Material;
import me.kaeternn.pasteurcraft.abstraction.AbstractTransmission;

public class ConsumeTransmission extends AbstractTransmission<Material> {
    public ConsumeTransmission(Set<Material> items, int chance){ super(items, chance); }
}