package me.kaeternn.pasteurcraft.entities.transmission;

import java.util.Set;
import org.bukkit.Material;

public class ConsumeTransmission extends AbstractTransmission<Material> {
    public ConsumeTransmission(Set<Material> items, int chance){ super(items, chance); }
}