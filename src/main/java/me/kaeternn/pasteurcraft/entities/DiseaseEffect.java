package me.kaeternn.pasteurcraft.entities;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DiseaseEffect {
    // Variables
    private String name;
    private int level;

    // Constructor
    public DiseaseEffect(String name, int level){
        this.name = name;
        this.level = level;
    }

    // Assessors
    public String getName() { return name; }
    public int getLevel() { return level; }

    // Methods
    public boolean apply(Player player){
        if(PotionEffectType.getByName(this.name) != null){
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.getByName(this.name), PotionEffect.INFINITE_DURATION, this.level, false, false)); }
        return false;
    }

    public boolean remove(Player player){
        if(PotionEffectType.getByName(this.name) != null){
            player.removePotionEffect(PotionEffectType.getByName(this.name)); }
        return false;
    }
}
