package me.kaeternn.pasteurcraft.object;

import java.util.List;
import org.bukkit.entity.EntityType;

import me.kaeternn.pasteurcraft.object.transmission.AirTransmission;
import me.kaeternn.pasteurcraft.object.transmission.BiomeTransmission;
import me.kaeternn.pasteurcraft.object.transmission.ConsumeTransmission;
import me.kaeternn.pasteurcraft.object.transmission.PhysicalTransmission;
import net.minecraft.network.chat.OutgoingChatMessage.Player;

public class Disease {
    private String name;

    private AirTransmission air;
    private BiomeTransmission biome;
    private ConsumeTransmission consume;
    private PhysicalTransmission physical;
    private List<EntityType> hosts;
    private List<EntityType> vectors;
    private int immunityChance;

    private List<Integer> incubation;
    private List<Integer> duration;
    private List<DiseaseEffect> effects;

    public Disease(String name, List<EntityType> hosts, List<EntityType> vectors, int immunityChance, List<Integer> incubation, List<Integer> duration, List<DiseaseEffect> effects){
        this.name = name;
        
        this.hosts = hosts;
        this.vectors = vectors;
        majHosts();

        this.immunityChance = immunityChance;
        
        this.incubation = incubation;
        this.duration = duration;
        this.effects = effects;
    }

    public void setAir(AirTransmission air){
        for(EntityType entity : air.getEntities())
            if(!this.vectors.contains(entity))
                this.vectors.add(entity);
        majHosts();
        this.air = air;
    }
    public void setBiome(BiomeTransmission biome){ this.biome = biome; }
    public void setConsume(ConsumeTransmission consume){ this.consume = consume; }
    public void setPhysical(PhysicalTransmission physical){
        for(EntityType entity : physical.getEntities())
            if(!this.vectors.contains(entity))
                this.vectors.add(entity);
        majHosts();
        this.physical = physical;
    }
    private void majHosts(){
        for(EntityType entity : this.vectors)
            if(!this.hosts.contains(entity))
                this.hosts.add(entity);
    }

    public String getName() { return name; }

    public boolean haveTransmission(){ return this.air.equals(null) && this.biome.equals(null) && this.consume.equals(null) && this.physical.equals(null); }
    public AirTransmission getAir() { return air; }
    public BiomeTransmission getBiome() { return biome; }
    public ConsumeTransmission getConsume() { return consume; }
    public PhysicalTransmission getPhysical() { return physical; }
    public List<EntityType> getHosts() { return hosts; }
    public List<EntityType> getVectors() { return vectors; }
    public int getImmunityChance() { return immunityChance; }

    public List<Integer> getIncubation() { return incubation; }
    public List<Integer> getDuration() { return duration; }
    public List<DiseaseEffect> getEffects() { return effects; }

    public boolean infect(Player player){
        return true;
    }
}
