package me.kaeternn.pasteurcraft.object.transmission;

import java.util.Set;
import org.bukkit.block.Biome;
import me.kaeternn.pasteurcraft.abstraction.AbstractTransmission;

public class BiomeTransmission extends AbstractTransmission<Biome> {
    public BiomeTransmission(Set<Biome> biomes, int chance){ super(biomes, chance); }
}
