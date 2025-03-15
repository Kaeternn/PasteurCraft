package me.grole.pasteurcraft.object.transmission;

import java.util.List;
import org.bukkit.block.Biome;

public class BiomeTransmission {
    private List<Biome> biomes;
    private int chance;

    public BiomeTransmission(List<Biome> biomes, int chance){
        this.biomes = biomes;
        this.chance = chance;
    }

    public List<Biome> getBiomes() { return biomes; }
    public int getChance() { return chance; }
}
