package me.kaeternn.pasteurcraft.entities.transmission;

import java.util.Set;
import org.bukkit.block.Biome;

public class BiomeTransmission extends AbstractTransmission<Biome> {
    public BiomeTransmission(Set<Biome> biomes, int chance){ super(biomes, chance); }
}
