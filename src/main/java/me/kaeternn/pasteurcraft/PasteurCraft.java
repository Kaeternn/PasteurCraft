package me.kaeternn.pasteurcraft;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.EntityType;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import me.kaeternn.pasteurcraft.event.consume.PlayerEatEvent;
import me.kaeternn.pasteurcraft.object.Disease;
import me.kaeternn.pasteurcraft.object.DiseaseEffect;
import me.kaeternn.pasteurcraft.object.transmission.AirTransmission;
import me.kaeternn.pasteurcraft.object.transmission.BiomeTransmission;
import me.kaeternn.pasteurcraft.object.transmission.ConsumeTransmission;
import me.kaeternn.pasteurcraft.object.transmission.PhysicalTransmission;

public class PasteurCraft extends JavaPlugin{
    public static PasteurCraft PLUGIN;
    private List<Disease> diseases = new ArrayList<>();

    @Override
    public void onEnable(){
        saveDefaultConfig();
        PasteurCraft.PLUGIN = this;
        FileConfiguration configuration = PasteurCraft.PLUGIN.getConfig();

        try{ // Try to get the diseases from configuration
            PLUGIN.diseases = loadDiseases(configuration);}
        catch(Exception e){
            getLogger().info("There was a major error while loading the diseases configuration : " + e); }

        getServer().getPluginManager().registerEvents(new PlayerEatEvent(PLUGIN), PLUGIN);
    }

    public List<Disease> getDiseases() { return diseases; }

    private List<Disease> loadDiseases(FileConfiguration configuration){
        ConfigurationSection diseaseConfiguration = configuration.getConfigurationSection("diseases");
        int i = 0;
        
        if(diseaseConfiguration.getKeys(false).isEmpty()){ // Verify if there is configured diseases
            getLogger().info("There is no disease in the configuration file, please note that the plugin won't do anything without one."); }
        else{ return null; }

        getLogger().info("Loading " + diseaseConfiguration.getKeys(false).size() + " diseases ...");

        for(String key : diseaseConfiguration.getKeys(false)){ // Loop on all configured diseases
            ConfigurationSection disease = diseaseConfiguration.getConfigurationSection(key);

            if(disease.getString("name") == null){ // Verify if the disease have a name
                getLogger().info("The disease number " + i+1 + " isn't named in the configuration, the disease was ignored by the plugin.");
                continue; }

            Set<EntityType> hosts = new HashSet<>();
            for(Object host : disease.getList("hosts")){ // Loop on all entities designed as hosts
                try{ // Try to match the entity with a Minecraft one
                    hosts.add(EntityType.valueOf(host.toString().toUpperCase())); }
                catch(Exception e){
                    getLogger().info(host.toString() + " in " + disease.getString("name") + "'s host list isn't a valid entity so it was ignored by the plugin."); }
            }
            
            Set<EntityType> vectors = new HashSet<>();
            for(Object vector : disease.getList("vectors")){ // Loop on all entities designed as vectors
                try{ // Try to match the entity with a Minecraft one
                    vectors.add(EntityType.valueOf(vector.toString().toUpperCase())); }
                catch(Exception e){
                    getLogger().info(vector.toString() + " in " + disease.getString("name") + "'s vector list isn't a valid entity so it was ignored by the plugin."); }
            }

            int immunityChance = 0;
            if(!(disease.getString("immunity_chance") == null)){ // Set immunity_chance value if configured
                immunityChance = disease.getInt("immunity_chance"); }

            ConfigurationSection incubationSection = disease.getConfigurationSection("incubation_duration");
            List<Integer> incubation = new ArrayList<>();
            if(incubationSection.getString("min") == null || incubationSection.getString("max") == null){ // Verify if the disease's incubation have min and max values configured
                getLogger().info(disease.getString("name") + "'s incubation duration setting seems to be incomplete, the disease was ignored by the plugin.");
                continue; }
            else{
                incubation.add(incubationSection.getInt("min"));
                incubation.add(incubationSection.getInt("max")); }

            ConfigurationSection infectionSection = disease.getConfigurationSection("infection_duration");
            List<Integer> infection = new ArrayList<>();
            if(infectionSection.getString("min") == null || infectionSection.getString("max") == null){ // Verify if the disease's incubation have min and max values configured
                getLogger().info(disease.getString("name") + "'s infection duration setting seems to be incomplete, the disease was ignored by the plugin.");
                continue; }
            else{
                infection.add(infectionSection.getInt("min"));
                infection.add(infectionSection.getInt("max")); }
            
            List<DiseaseEffect> effects = new ArrayList<>();
            if(disease.getList("effects") == null){ // Verify is the disease have any effect configured
                getLogger().info(disease.getString("name") + " don't have any effect, the disease was ignored by the plugin.");
                continue; }
            else{
                for(Object effect : disease.getList("effects")){ // Loop on all disease's effects
                    String effectName = effect.toString().split("\\s")[0];
                    int effectLevel = Integer.parseInt(effect.toString().split("\\s")[1]);
                    
                    effects.add(new DiseaseEffect(effectName, effectLevel)); } }
            
            // Create the disease's instance
            Disease diseaseToAdd = new Disease(disease.getString("name"), hosts, vectors, immunityChance, incubation, infection, effects);
            ConfigurationSection transmissionSection = disease.getConfigurationSection("transmission");

            if(transmissionSection.getConfigurationSection("air_transmission") != null){ // Verify if the disease have an air transmission
                AirTransmission air = (AirTransmission) instTransmission(transmissionSection.getConfigurationSection("air_transmission"), "air_transmission", disease.getString("name"));
                diseaseToAdd.setAir(air); }

            if(transmissionSection.getConfigurationSection("biome_transmission") != null){ // Verify if the disease have an biome transmission
                BiomeTransmission biome = (BiomeTransmission) instTransmission(transmissionSection.getConfigurationSection("biome_transmission"), "biome_transmission", disease.getString("name"));
                diseaseToAdd.setBiome(biome); }

            if(transmissionSection.getConfigurationSection("consume_transmission") != null){ // Verify if the disease have an consume transmission
                ConsumeTransmission consume = (ConsumeTransmission) instTransmission(transmissionSection.getConfigurationSection("consume_transmission"), "consume_transmission", disease.getString("name"));
                diseaseToAdd.setConsume(consume); }

            if(transmissionSection.getConfigurationSection("physical_transmission") != null){ // Verify if the disease have an physical transmission
                PhysicalTransmission physical = (PhysicalTransmission) instTransmission(transmissionSection.getConfigurationSection("physical_transmission"), "physical_transmission", disease.getString("name"));
                diseaseToAdd.setPhysical(physical); }

            diseases.add(diseaseToAdd);
            i++; 
        }

        return diseases;
    }

    private Object instTransmission (ConfigurationSection section, String type, String disease) {
        Set<Object> objects = new HashSet<>();

        for(Object object : section.getList(type.equals("air_transmission")||type.equals("physical_transmission") ? "entities" : type.equals("biome_transmission") ? "biomes" : "items")){
            switch(type){
                case "biome_transmission":
                    try{
                        objects.add(Biome.valueOf(object.toString().toUpperCase()));}
                    catch(Exception e){
                        getLogger().info(object.toString() + " in " + disease + "'s " + type + " biome list isn't a valid biome so it was ignored by the plugin."); }
                case "consume_transmission":
                    try{
                        objects.add(Material.valueOf(object.toString().toUpperCase()));}
                    catch(Exception e){
                        getLogger().info(object.toString() + " in " + disease + "'s " + type + " item list isn't a valid item so it was ignored by the plugin."); }
                default:
                    try{
                        objects.add(EntityType.valueOf(object.toString().toUpperCase()));}
                    catch(Exception e){
                        getLogger().info(object.toString() + " in " + disease + "'s " + type + " entity list isn't a valid entity so it was ignored by the plugin."); }
            }
        }

        switch(type){
            case "air_transmission":
                return new AirTransmission(objects.stream().map(EntityType.class::cast).collect(Collectors.toSet()), section.getInt("radius"), section.getInt("chance"));
            case "biome_transmission":
                return new BiomeTransmission(objects.stream().map(Biome.class::cast).collect(Collectors.toSet()), section.getInt("chance"));
            case "consume_transmission":
                return new ConsumeTransmission(objects.stream().map(Material.class::cast).collect(Collectors.toSet()), section.getInt("chance"));
            case "physical_transmission":
                return new PhysicalTransmission(objects.stream().map(EntityType.class::cast).collect(Collectors.toSet()), section.getInt("chance"));
        }

        return null;
    }
}
