package me.kaeternn.pasteurcraft.object.transmission;

import java.util.List;
import org.bukkit.block.Biome;

import me.kaeternn.pasteurcraft.abstraction.AbstractTransmission;

public class BiomeTransmission extends AbstractTransmission<Biome> {
    public BiomeTransmission(List<Biome> biomes, int chance){ super(biomes, chance); }
}
