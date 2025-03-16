package me.kaeternn.pasteurcraft.object.transmission;

import java.util.List;
import org.bukkit.block.Biome;

public class BiomeTransmission {
    // Variables
    private List<Biome> biomes;
    private int chance;

    // Constructor
    public BiomeTransmission(List<Biome> biomes, int chance){
        this.biomes = biomes;
        this.chance = chance;
    }

    // Assessors
    public List<Biome> getBiomes() { return biomes; }
    public int getChance() { return chance; }
}
