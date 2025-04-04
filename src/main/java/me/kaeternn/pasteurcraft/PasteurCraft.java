package me.kaeternn.pasteurcraft;

import java.io.File;
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
import org.bukkit.configuration.file.YamlConfiguration;
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
import me.kaeternn.pasteurcraft.tasks.PasteurCraftTask;

public class PasteurCraft extends JavaPlugin{
    public static PasteurCraft plugin;
    public static List<Disease> diseases = new ArrayList<>();
    public static boolean debug;
    private static YamlConfiguration lang;

    @SuppressWarnings("static-access")
    @Override
    public void onEnable(){
        saveDefaultConfig();
        PasteurCraft.plugin = this;
        FileConfiguration configuration = PasteurCraft.plugin.getConfig();
        UsersData.init();

        try{ // Try to get language from configuration
            lang = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang/" + configuration.getString("language") + ".yml"));
        } catch(Exception e) {
            getLogger().info("Failed to get language from the configuration, it was set to en by default.");
            configuration.addDefault("language", "en");
            lang = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang/en.yml"));
        }

        try{ // Try to get debug state from configuration
            debug = configuration.getBoolean("debug");
        } catch(Exception e) {
            getLogger().warning(plugin.getMSG("error_debug_config").replace("%error%", e.getMessage()));
            configuration.addDefault("debug", false);
            debug = false; 
        }

        try{ // Try to get the diseases from configuration
            plugin.diseases = loadDiseases(configuration);
        } catch(Exception e) {
            getLogger().warning(plugin.getMSG("error_diseases_config").replace("%error%", e.getMessage()));
        }

        getCommand("infect").setExecutor(new InfectCmd());
        getCommand("cure").setExecutor(new CureCmd());
        // TODO : Ajout des commandes DiseaseList et Reload

        getServer().getPluginManager().registerEvents(new PlayerEatEvent(plugin), plugin);
        getServer().getPluginManager().registerEvents(new EntityAttackEvent(plugin), plugin);
        getServer().getPluginManager().registerEvents(new DisEntityDeathEvent(plugin), plugin);

        new PasteurCraftTask(plugin).runTaskTimer(plugin, 0, 20);
    }

    public List<Disease> getDiseases() { return diseases; }

    private List<Disease> loadDiseases(FileConfiguration configuration){
        ConfigurationSection diseaseConfiguration = configuration.getConfigurationSection("diseases");
        int i = 0;
        boolean stop = false;
        
        // Verify if there is any configured diseases
        if(diseaseConfiguration.getKeys(false).isEmpty()) getLogger().info(getMSG("info_no_disease"));

        for(String key : diseaseConfiguration.getKeys(false)){ // Loop on all configured diseases
            ConfigurationSection disease = diseaseConfiguration.getConfigurationSection(key);

            //TODO : Continuer la migration des messages.

            if(disease.getString("name") == null){ // Verify if the disease have a name
                getLogger().info(getMSG("info_disease_no_name"));
                continue; 
            }
            
            for(Disease savedDisease : this.diseases){
                if(disease.getString("name").equals(savedDisease.getName())){ // Verify if a disease exist with the same name
                    getLogger().info(getMSG("info_disease_same_name").replace("%disease%", disease.getString("name")).replace("%diseaseid%", "" + (i+1)));
                    stop = true;
                }
            }

            if(stop) continue;

            Set<EntityType> hosts = loadEntities(disease, "hosts", disease.getString("name"));
            Set<EntityType> vectors = loadEntities(disease, "vectors", disease.getString("name"));

            int immunityChance = 0;
            if(!(disease.getString("immunity_chance") == null)){ // Set immunity_chance value if configured
                immunityChance = disease.getInt("immunity_chance");
            }
                
            List<Integer> incubation = loadDuration(disease.getConfigurationSection("incubation_duration"), "incubation_duration", disease.getString("name"));
            List<Integer> infection = loadDuration(disease.getConfigurationSection("infection_duration"), "infection_duration", disease.getString("name"));
            if(incubation.isEmpty() || infection.isEmpty()) continue;  // Verify if the disease's incubation and infection have min and max values configured
            
            List<DiseaseEffect> effects = new ArrayList<>();
            if(disease.getList("effects") == null){ // Verify is the disease have any effect configured
                getLogger().info(getMSG("info_disease_no_effect").replace("%disease%", disease.getString("name")).replace("%diseaseid%", "" + (i+1)));
                continue; 
            } else {
                for(Object effect : disease.getList("effects")){ // Loop on all disease's effects
                    String effectName = effect.toString().split("\\s")[0];
                    int effectLevel = Integer.parseInt(effect.toString().split("\\s")[1]);
                    
                    effects.add(new DiseaseEffect(effectName, effectLevel));
                }
            }
            
            // Create the disease's instance
            Disease diseaseToAdd = new Disease(disease.getString("name"), hosts, vectors, immunityChance, incubation, infection, effects);
            ConfigurationSection transmissionSection = disease.getConfigurationSection("transmission");

            if(transmissionSection.getConfigurationSection("air_transmission") != null){ // Verify if the disease have a air transmission
                AirTransmission air = (AirTransmission) loadTransmission(transmissionSection.getConfigurationSection("air_transmission"), "air_transmission", disease.getString("name"));
                diseaseToAdd.setAir(air);
            }

            if(transmissionSection.getConfigurationSection("biome_transmission") != null){ // Verify if the disease have a biome transmission
                BiomeTransmission biome = (BiomeTransmission) loadTransmission(transmissionSection.getConfigurationSection("biome_transmission"), "biome_transmission", disease.getString("name"));
                diseaseToAdd.setBiome(biome);
            }

            if(transmissionSection.getConfigurationSection("consume_transmission") != null){ // Verify if the disease have a consume transmission
                ConsumeTransmission consume = (ConsumeTransmission) loadTransmission(transmissionSection.getConfigurationSection("consume_transmission"), "consume_transmission", disease.getString("name"));
                diseaseToAdd.setConsume(consume);
            }

            if(transmissionSection.getConfigurationSection("physical_transmission") != null){ // Verify if the disease have a physical transmission
                PhysicalTransmission physical = (PhysicalTransmission) loadTransmission(transmissionSection.getConfigurationSection("physical_transmission"), "physical_transmission", disease.getString("name"));
                diseaseToAdd.setPhysical(physical);
            }

            diseases.add(diseaseToAdd);
            i++; 
        }

        getLogger().info(getMSG("info_loaded_diseases").replace("%nbdisease%","" + diseases.size()));

        return diseases;
    }

    private List<Integer> loadDuration(ConfigurationSection section, String type, String disease){
        List<Integer> duration = new ArrayList<>();

        // Verify if the disease's incubation have min and max values configured
        if(section.getString("min") == null || section.getString("max") == null) getLogger().info(type + " : " + getMSG("info_disease_no_duration").replace("%disease%", disease));
        else {
            duration.add(section.getInt("min"));
            duration.add(section.getInt("max"));
        }

        return duration;
    }

    private Set<EntityType> loadEntities(ConfigurationSection section, String type, String disease){
        Set<EntityType> entities = new HashSet<>();

        for(Object entity : section.getList(type)){ // Loop on all entities of designated section
            try{ // Try to match the entity with a Minecraft one
                entities.add(EntityType.valueOf(entity.toString().toUpperCase()));
            } catch(Exception e) {
                getLogger().info(type + " : " + getMSG("info_object_mismatch_entity").replace("%object%", entity.toString()).replace("%disease%", disease));
            }
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
                    getLogger().info(getMSG("info_object_mismatch_air").replace("%object%", object.toString()).replace("%disease%", disease));
                }
            } else if (type.equals("biome_transmission")) {
                try {
                    objects.add(Biome.valueOf(object.toString().toUpperCase()));
                } catch (Exception e) {
                    getLogger().info(getMSG("info_object_mismatch_biome").replace("%object%", object.toString()).replace("%disease%", disease));
                }
            } else if (type.equals("consume_transmission")) {
                try {
                    objects.add(Material.valueOf(object.toString().toUpperCase()));
                } catch (Exception e) {
                    getLogger().info(getMSG("info_object_mismatch_consume").replace("%object%", object.toString()).replace("%disease%", disease));
                }
            } else if (type.equals("physical_transmission")) {
                try {
                    objects.add(EntityType.valueOf(object.toString().toUpperCase()));
                } catch (Exception e) {
                    getLogger().info(getMSG("info_object_mismatch_physical").replace("%object%", object.toString()).replace("%disease%", disease));
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

    public String getMSG(String msgID){
        return lang.getString(msgID)
            .replace("%version%", plugin.getPluginMeta().getVersion()
            .replace("%nbdisease%", ((Integer) plugin.diseases.size()).toString()));
    }
}
