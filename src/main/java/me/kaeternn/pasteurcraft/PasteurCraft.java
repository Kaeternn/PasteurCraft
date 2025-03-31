package me.kaeternn.pasteurcraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.kaeternn.pasteurcraft.commands.CureCmd;
import me.kaeternn.pasteurcraft.commands.InfectCmd;
import me.kaeternn.pasteurcraft.entities.Disease;
import me.kaeternn.pasteurcraft.entities.DiseaseEffect;
import me.kaeternn.pasteurcraft.entities.transmission.AirTransmission;
import me.kaeternn.pasteurcraft.entities.transmission.BiomeTransmission;
import me.kaeternn.pasteurcraft.entities.transmission.ConsumeTransmission;
import me.kaeternn.pasteurcraft.entities.transmission.PhysicalTransmission;
import me.kaeternn.pasteurcraft.listeners.DisEntityDeathEvent;
import me.kaeternn.pasteurcraft.listeners.consume.PlayerEatEvent;
import me.kaeternn.pasteurcraft.listeners.physical.EntityAttackEvent;
import me.kaeternn.pasteurcraft.tasks.DiseaseCheckTask;

public class PasteurCraft extends JavaPlugin{
    public static PasteurCraft plugin;
    public static List<Disease> diseases = new ArrayList<>();
    public static boolean debug;
    public static String lang;

    @Override
    public void onEnable(){
        saveDefaultConfig();
        PasteurCraft.plugin = this;
        FileConfiguration configuration = PasteurCraft.plugin.getConfig();
        UsersData.init();

        try{ // Try to get debug state from configuration
            debug = configuration.getBoolean("debug");
        }catch(Exception e){
            getLogger().info("Failed to get debug state from the configuration, it was set to false by default."); 
            configuration.addDefault("debug", false);
            debug = false; 
        }

        try{ // Try to get language from configuration
            lang = configuration.getString("language");
        }catch(Exception e){
            getLogger().info("Failed to get language from the configuration, it was set to en by default.");
            configuration.addDefault("language", "en");
            lang = "en"; 
        }

        try{ // Try to get the diseases from configuration
            plugin.diseases = loadDiseases(configuration);
        }catch(Exception e){
            getLogger().info("There was a major error while loading the diseases configuration : " + e); 
        }

        getCommand("infect").setExecutor(new InfectCmd());
        getCommand("cure").setExecutor(new CureCmd());

        getServer().getPluginManager().registerEvents(new PlayerEatEvent(plugin), plugin);
        getServer().getPluginManager().registerEvents(new EntityAttackEvent(plugin), plugin);
        getServer().getPluginManager().registerEvents(new DisEntityDeathEvent(plugin), plugin);

        new DiseaseCheckTask(plugin).runTaskTimer(plugin, 0, 1200);
    }

    public List<Disease> getDiseases() { return diseases; }

    private List<Disease> loadDiseases(FileConfiguration configuration){
        ConfigurationSection diseaseConfiguration = configuration.getConfigurationSection("diseases");
        int i = 0;
        
        if(diseaseConfiguration.getKeys(false).isEmpty()){ // Verify if there is configured diseases
            getLogger().info("There is no disease in the configuration file, please note that the plugin won't do anything without one."); }

        getLogger().info("Loading " + diseaseConfiguration.getKeys(false).size() + " diseases ...");

        for(String key : diseaseConfiguration.getKeys(false)){ // Loop on all configured diseases
            ConfigurationSection disease = diseaseConfiguration.getConfigurationSection(key);

            if(disease.getString("name") == null){ // Verify if the disease have a name
                getLogger().info("The disease number " + i+1 + " isn't named in the configuration, the disease was ignored by the plugin.");
                continue; }

            Set<EntityType> hosts = loadEntities(disease, "hosts", disease.getString("name"));
            Set<EntityType> vectors = loadEntities(disease, "vectors", disease.getString("name"));

            int immunityChance = 0;
            if(!(disease.getString("immunity_chance") == null)){ // Set immunity_chance value if configured
                immunityChance = disease.getInt("immunity_chance"); }
                
            List<Integer> incubation = loadDuration(disease.getConfigurationSection("incubation_duration"), "incubation_duration", disease.getString("name"));
            List<Integer> infection = loadDuration(disease.getConfigurationSection("infection_duration"), "infection_duration", disease.getString("name"));
            if(incubation.isEmpty() || infection.isEmpty()){ continue; } // Verify if the disease's incubation and infection have min and max values configured
            
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

            if(transmissionSection.getConfigurationSection("air_transmission") != null){ // Verify if the disease have a air transmission
                AirTransmission air = (AirTransmission) loadTransmission(transmissionSection.getConfigurationSection("air_transmission"), "air_transmission", disease.getString("name"));
                diseaseToAdd.setAir(air); }

            if(transmissionSection.getConfigurationSection("biome_transmission") != null){ // Verify if the disease have a biome transmission
                BiomeTransmission biome = (BiomeTransmission) loadTransmission(transmissionSection.getConfigurationSection("biome_transmission"), "biome_transmission", disease.getString("name"));
                diseaseToAdd.setBiome(biome); }

            if(transmissionSection.getConfigurationSection("consume_transmission") != null){ // Verify if the disease have a consume transmission
                ConsumeTransmission consume = (ConsumeTransmission) loadTransmission(transmissionSection.getConfigurationSection("consume_transmission"), "consume_transmission", disease.getString("name"));
                diseaseToAdd.setConsume(consume); }

            if(transmissionSection.getConfigurationSection("physical_transmission") != null){ // Verify if the disease have a physical transmission
                PhysicalTransmission physical = (PhysicalTransmission) loadTransmission(transmissionSection.getConfigurationSection("physical_transmission"), "physical_transmission", disease.getString("name"));
                diseaseToAdd.setPhysical(physical); }

            diseases.add(diseaseToAdd);
            i++; 
        }

        return diseases;
    }

    private List<Integer> loadDuration(ConfigurationSection section, String type, String disease){
        List<Integer> duration = new ArrayList<>();
        
        if(section.getString("min") == null || section.getString("max") == null){ // Verify if the disease's incubation have min and max values configured
            getLogger().info(disease + "'s " + type + " duration setting seems to be incomplete, the disease was ignored by the plugin."); }
        else{
            duration.add(section.getInt("min"));
            duration.add(section.getInt("max")); }

        return duration;
    }

    private Set<EntityType> loadEntities(ConfigurationSection section, String type, String disease){
        Set<EntityType> entities = new HashSet<>();

        for(Object entity : section.getList(type)){ // Loop on all entities of designated section
            try{ // Try to match the entity with a Minecraft one
                entities.add(EntityType.valueOf(entity.toString().toUpperCase())); }
            catch(Exception e){
                getLogger().info(entity.toString() + " in " + disease + "'s " + type + " list isn't a valid entity so it was ignored by the plugin."); }
        }

        return entities;
    }

    private Object loadTransmission (ConfigurationSection section, String type, String disease) {
        Set<Object> objects = new HashSet<>();

        for (Object object : section.getList(type.equals("air_transmission") || type.equals("physical_transmission") ? "entities" : type.equals("biome_transmission") ? "biomes" : "items")) {
            if (type.equals("air_transmission")) {
                try {
                    objects.add(EntityType.valueOf(object.toString().toUpperCase()));
                } catch (Exception e) {
                    getLogger().info(object.toString() + " in " + disease + "'s air_transmission entity list isn't a valid entity so it was ignored by the plugin.");
                }
            } else if (type.equals("biome_transmission")) {
                try {
                    objects.add(Biome.valueOf(object.toString().toUpperCase()));
                } catch (Exception e) {
                    getLogger().info(object.toString() + " in " + disease + "'s biome_transmission biome list isn't a valid biome so it was ignored by the plugin.");
                }
            } else if (type.equals("consume_transmission")) {
                try {
                    objects.add(Material.valueOf(object.toString().toUpperCase()));
                } catch (Exception e) {
                    getLogger().info(object.toString() + " in " + disease + "'s consume_transmission item list isn't a valid item so it was ignored by the plugin.");
                }
            } else if (type.equals("physical_transmission")) {
                try {
                    objects.add(EntityType.valueOf(object.toString().toUpperCase()));
                } catch (Exception e) {
                    getLogger().info(object.toString() + " in " + disease + "'s physical_transmission entity list isn't a valid entity so it was ignored by the plugin.");
                }
            }
        }

        if (type.equals("air_transmission")) {
            return new AirTransmission(objects.stream().map(EntityType.class::cast).collect(Collectors.toSet()), section.getInt("radius"), section.getInt("chance"));
        } else if (type.equals("biome_transmission")) {
            return new BiomeTransmission(objects.stream().map(Biome.class::cast).collect(Collectors.toSet()), section.getInt("chance"));
        } else if (type.equals("consume_transmission")) {
            return new ConsumeTransmission(objects.stream().map(Material.class::cast).collect(Collectors.toSet()), section.getInt("chance"));
        } else if (type.equals("physical_transmission")) {
            return new PhysicalTransmission(objects.stream().map(EntityType.class::cast).collect(Collectors.toSet()), section.getInt("chance"));
        }

        return null;
    }
}
