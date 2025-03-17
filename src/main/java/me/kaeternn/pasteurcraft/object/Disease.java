package me.kaeternn.pasteurcraft.object;

import java.util.List;
import java.util.Set;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.kaeternn.pasteurcraft.abstraction.AbstractTransmission;
import me.kaeternn.pasteurcraft.object.transmission.AirTransmission;
import me.kaeternn.pasteurcraft.object.transmission.BiomeTransmission;
import me.kaeternn.pasteurcraft.object.transmission.ConsumeTransmission;
import me.kaeternn.pasteurcraft.object.transmission.PhysicalTransmission;

public class Disease {
    // Variables
    private String name;

    private List<AbstractTransmission> transmissions;
    private Set<EntityType> hosts;
    private Set<EntityType> vectors;
    private int immunityChance;

    private List<Integer> incubation;
    private List<Integer> duration;
    private List<DiseaseEffect> effects;

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
    public void infect(Player player) {

    }
}
