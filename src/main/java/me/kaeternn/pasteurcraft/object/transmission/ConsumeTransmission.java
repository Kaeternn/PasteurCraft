package me.kaeternn.pasteurcraft.object.transmission;

import java.util.List;
import org.bukkit.Material;

public class ConsumeTransmission {
    // Variables
    private List<Material> items;
    private int chance;

    // Constructor
    public ConsumeTransmission(List<Material> items, int chance){
        this.items = items;
        this.chance = chance;
    }

    // Assessors
    public List<Material> getItems() { return items; }
    public int getChance() { return chance; }
}