package me.kaeternn.pasteurcraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
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
            PLUGIN.diseases = loadDiseases(configuration, configuration.getBoolean("debug")); }
        catch(Exception e){
            getLogger().info("There was a major error while loading the diseases configuration : " + e); }
    }

    public List<Disease> loadDiseases(FileConfiguration configuration, boolean hasDebug){
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

            List<EntityType> hosts = new ArrayList<>();
            for(Object host : disease.getList("hosts")){ // Loop on all entities designed as hosts
                try{ // Try to match the entity with a Minecraft one
                    hosts.add(EntityType.valueOf(host.toString().toUpperCase())); }
                catch(Exception e){
                    getLogger().info(host.toString() + " in " + disease.getString("name") + "'s host list isn't a valid entity so it was ignored by the plugin."); }
            }
            
            List<EntityType> vectors = new ArrayList<>();
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

            if(transmissionSection.getConfigurationSection("air_transmission") != null){ // Getting the air transmission parameters
                ConfigurationSection airSection = transmissionSection.getConfigurationSection("air_transmission");

                List<EntityType> entities = new ArrayList<>();
                for(Object entity : airSection.getList("entities")){ // Loop on all the transmission's entities
                    try{ // Try to match the entity with a Minecraft one
                        entities.add(EntityType.valueOf(entity.toString().toUpperCase())); }
                    catch(Exception e){
                        getLogger().info(entity.toString() + " in " + disease.getString("name") + "'s air_transmission entity list isn't a valid entity so it was ignored by the plugin."); } }

                AirTransmission air = new AirTransmission(entities, airSection.getInt("radius"), airSection.getInt("chance"));
                diseaseToAdd.setAir(air); }

            if(transmissionSection.getConfigurationSection("biome_transmission") != null){ // Getting the biome transmission parameters
                ConfigurationSection biomeSection = transmissionSection.getConfigurationSection("biome_transmission");

                List<Biome> biomes = new ArrayList<>();
                for(Object biome : biomeSection.getList("biomes")){ // Loop on all the transmission's biomes
                    try{ // Try to match the biome with a Minecraft one
                        biomes.add(Biome.valueOf(biome.toString().toUpperCase())); }
                    catch(Exception e){
                        getLogger().info(biome.toString() + " in " + disease.getString("name") + "'s biome_transmission biome list isn't a valid biome so it was ignored by the plugin."); } }

                BiomeTransmission biome = new BiomeTransmission(biomes, biomeSection.getInt("chance"));
                diseaseToAdd.setBiome(biome); }


            if(transmissionSection.getConfigurationSection("consume_transmission") != null){ // Getting the consume transmission parameters
                ConfigurationSection consumeSection = transmissionSection.getConfigurationSection("consume_transmission");

                List<Material> items = new ArrayList<>();
                for(Object item : consumeSection.getList("items")){ // Loop on all the transmission's items
                    try{ // Try to match the item with a Minecraft one
                        items.add(Material.valueOf(item.toString().toUpperCase())); }
                    catch(Exception e){
                        getLogger().info(item.toString() + " in " + disease.getString("name") + "'s consume_transmission item list isn't a valid item so it was ignored by the plugin."); } }
                
                ConsumeTransmission consume = new ConsumeTransmission(items, consumeSection.getInt("chance"));
                diseaseToAdd.setConsume(consume); }


            if(transmissionSection.getConfigurationSection("physical_transmission") != null){ // Getting the physical transmission parameters
                ConfigurationSection physicalSection = transmissionSection.getConfigurationSection("physical_transmission");

                List<EntityType> entities = new ArrayList<>();
                for(Object entity : physicalSection.getList("entities")){ // Loop on all the transmission's entities
                    try{ // Try to match the entity with a Minecraft one
                        entities.add(EntityType.valueOf(entity.toString().toUpperCase())); }
                    catch(Exception e){
                        getLogger().info(entity.toString() + " in " + disease.getString("name") + "'s physical_transmission entity list isn't a valid entity so it was ignored by the plugin."); } }
                
                PhysicalTransmission physical = new PhysicalTransmission(entities, physicalSection.getInt("chance"));
                diseaseToAdd.setPhysical(physical); }

            diseases.add(diseaseToAdd);
            i++; 
        }

        return diseases;
    }
}