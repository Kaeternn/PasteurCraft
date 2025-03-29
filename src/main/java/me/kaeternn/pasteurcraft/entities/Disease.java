package me.kaeternn.pasteurcraft.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.kaeternn.pasteurcraft.UsersData;
import me.kaeternn.pasteurcraft.entities.transmission.AbstractTransmission;
import me.kaeternn.pasteurcraft.entities.transmission.AirTransmission;
import me.kaeternn.pasteurcraft.entities.transmission.BiomeTransmission;
import me.kaeternn.pasteurcraft.entities.transmission.ConsumeTransmission;
import me.kaeternn.pasteurcraft.entities.transmission.PhysicalTransmission;

public class Disease {
    // Variables
    private String name;

    private List<AbstractTransmission> transmissions;
    private Set<EntityType> hosts;
    private Set<EntityType> vectors;
    private int immunityChance;

    private List<Integer> incubation;
    private List<Integer> duration;
    private List<DiseaseEffect> effects; // Isn't stored in a PotionEffectType list because of planned custom effects

    // Constructor
    public Disease(String name, Set<EntityType> hosts, Set<EntityType> vectors, int immunityChance, List<Integer> incubation, List<Integer> duration, List<DiseaseEffect> effects){
        this.name = name;
        
        this.hosts = hosts;
        this.vectors = vectors;
        this.hosts.addAll(vectors);

        this.immunityChance = immunityChance;
        
        this.incubation = incubation;
        this.duration = duration;
        this.effects = effects;

        this.transmissions = new ArrayList<>();
    }
    public void setAir(AirTransmission air){
        this.vectors.addAll(air.getList());
        this.hosts.addAll(this.vectors);
        this.transmissions.add(air);
    }
    public void setBiome(BiomeTransmission biome){ this.transmissions.add(biome); }
    public void setConsume(ConsumeTransmission consume){ this.transmissions.add(consume); }
    public void setPhysical(PhysicalTransmission physical){
        this.vectors.addAll(physical.getList());
        this.hosts.addAll(this.vectors);
        this.transmissions.add(physical);
    }

    // Assessors
    public String getName() { return name; }

    public boolean haveTransmission(){ return this.transmissions.size() == 0; }
    public List<AbstractTransmission> getTransmissions() { return transmissions; }
    public Set<EntityType> getHosts() { return hosts; }
    public Set<EntityType> getVectors() { return vectors; }
    public int getImmunityChance() { return immunityChance; }

    public List<Integer> getIncubation() { return incubation; }
    public List<Integer> getDuration() { return duration; }
    public List<DiseaseEffect> getEffects() { return effects; }

    // Methods
    public boolean infect(Player player) {
        YamlConfiguration data = UsersData.getOrCreate(player);
        if (!data.contains("diseases")){
            data.set("diseases", null);
        }

        ConfigurationSection diseases = data.getConfigurationSection("diseases");
        if (!diseases.contains(this.name)){
            diseases.set(this.name, null);
        } else {
            return false;
        }

        ConfigurationSection disease = diseases.getConfigurationSection(this.name);

        if(!disease.contains("immunity"))
        {
            disease.set("immunity", new Random().nextInt(100) < this.immunityChance);
        }

        if(!disease.getBoolean("immunity"))
        {
            disease.set("incubation", new Random().nextInt(incubation.get(0), incubation.get(1)) * 60 * 20);
            disease.set("duration", new Random().nextInt(duration.get(0), duration.get(1)) * 60 * 20);    
            disease.set("startplaytime", player.getStatistic(Statistic.PLAY_ONE_MINUTE));

            for(DiseaseEffect effect : effects){
                effect.apply(player);
            }
        }

        diseases.set(this.name, disease);
        data.set("diseases", diseases);

        return true;
    }

    public void cure(Player player) {
        YamlConfiguration data = UsersData.getOrCreate(player);

        if (data.contains("diseases")){
            ConfigurationSection diseases = data.getConfigurationSection("diseases");

            if (!diseases.contains(this.name)){
                diseases.set(this.name, null);
                data.set("diseases", diseases);
            }
        }
    }
}
