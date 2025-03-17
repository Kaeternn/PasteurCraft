package me.kaeternn.pasteurcraft.object.transmission;

import java.util.List;
import org.bukkit.Material;

import me.kaeternn.pasteurcraft.abstraction.AbstractTransmission;

public class ConsumeTransmission extends AbstractTransmission<Material> {
    public ConsumeTransmission(List<Material> items, int chance){ super(items, chance); }
}