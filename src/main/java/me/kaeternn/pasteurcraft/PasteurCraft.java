package me.kaeternn.pasteurcraft;

import java.util.List;
import org.bukkit.entity.EntityType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import me.kaeternn.pasteurcraft.object.Disease;
import me.kaeternn.pasteurcraft.object.DiseaseEffect;
import me.kaeternn.pasteurcraft.object.transmission.AirTransmission;
import me.kaeternn.pasteurcraft.object.transmission.BiomeTransmission;
import me.kaeternn.pasteurcraft.object.transmission.ConsumeTransmission;
import me.kaeternn.pasteurcraft.object.transmission.PhysicalTransmission;
import net.minecraft.world.level.biome.Biome;

public class PasteurCraft extends JavaPlugin{
    public static PasteurCraft PLUGIN;
    private List<Disease> diseases;

    @SuppressWarnings("null")
    @Override
    public void onEnable(){
        saveDefaultConfig();
        PasteurCraft.PLUGIN = this;
        ConfigurationSection diseaseConfiguration = null;

        try{
            diseaseConfiguration = PasteurCraft.PLUGIN.getConfig().getConfigurationSection("diseases");
        }
        catch(Exception e){
            System.out.println("There was a major error while loading the configuration : " + e);
        }

        if(diseaseConfiguration.equals(null)){
            System.out.println("There is no disease in the configuration file, please note that the plugin won't do anything without one.");
        }
        else{
            int i = 0;

            System.out.println("Loading " + diseaseConfiguration.getKeys(false).size() + " diseases.");

            for(String key : diseaseConfiguration.getKeys(false)){
                ConfigurationSection disease = diseaseConfiguration.getConfigurationSection(key);

                if(disease.getString("name").equals(null)){
                    System.out.println("The disease number " + i+1 + " isn't named in the configuration, the disease was ignored by the plugin.");
                    continue;
                }

                List<EntityType> hosts = null;
                for(Object host : disease.getList("hosts")){
                    try{
                        hosts.add(EntityType.fromName(host.toString().toUpperCase()));
                    }
                    catch(Exception e){
                        System.out.println(host.toString() + " in " + disease.getString("name") + "'s host list isn't a valid entity so it was ignored by the plugin.");
                    }
                }
                
                List<EntityType> vectors = null;
                for(Object vector : disease.getList("vectors")){
                    try{
                        vectors.add(EntityType.fromName(vector.toString().toUpperCase()));
                    }
                    catch(Exception e){
                        System.out.println(vector.toString() + " in " + disease.getString("name") + "'s vector list isn't a valid entity so it was ignored by the plugin.");
                    }
                }

                int immunityChance = 0;
                if(!disease.getString("immunity_chance").equals(null)){
                    immunityChance = disease.getInt("immunity_chance");
                }

                ConfigurationSection incubationSection = disease.getConfigurationSection("incubation");
                List<Integer> incubation = null;
                if(incubationSection.getString("min").equals(null) || incubationSection.getString("max").equals(null)){
                    System.out.println(disease.getString("name") + "'s incubation duration setting seems to be incomplete, the disease was ignored by the plugin.");
                    continue;
                }
                else{
                    incubation.add(incubationSection.getInt("min"));
                    incubation.add(incubationSection.getInt("max"));
                }

                ConfigurationSection infectionSection = disease.getConfigurationSection("incubation");
                List<Integer> infection = null;
                if(infectionSection.getString("min").equals(null) || infectionSection.getString("max").equals(null)){
                    System.out.println(disease.getString("name") + "'s infection duration setting seems to be incomplete, the disease was ignored by the plugin.");
                    continue;
                }
                else{
                    infection.add(infectionSection.getInt("min"));
                    infection.add(infectionSection.getInt("max"));
                }
                
                List<DiseaseEffect> effects = null;
                if(disease.getList("effects").equals(null))
                {
                    System.out.println(disease.getString("name") + " don't have any effect, the disease was ignored by the plugin.");
                    continue;
                }
                else{
                    for(Object effect : disease.getList("effects")){
                        String effectName = effect.toString().split("\\\\s")[0];
                        int effectLevel = Integer.parseInt(effect.toString().split("\\\\s")[1]);
                        
                        effects.add(new DiseaseEffect(effectName, effectLevel));
                    }
                }

                Disease diseaseToAdd = new Disease(disease.getString("name"), hosts, vectors, immunityChance, incubation, infection, effects);

                if(disease.getConfigurationSection("transmission").equals(null))
                {
                    System.out.println(disease.getString("name") + " don't have any transmission settings, the disease was ignored by the plugin.");
                    continue;
                }
                else{
                    ConfigurationSection transmissionSection = disease.getConfigurationSection("transmission");
    
                    if(!transmissionSection.getConfigurationSection("air_transmission").equals(null)){
                        ConfigurationSection airSection = transmissionSection.getConfigurationSection("air_transmission");

                        List<EntityType> entities = null;
                        for(Object entity : airSection.getList("entities")){
                            try{
                                entities.add(EntityType.fromName(entity.toString().toUpperCase()));
                            }
                            catch(Exception e){
                                System.out.println(entity.toString() + " in " + disease.getString("name") + "'s air_transmission entity list isn't a valid entity so it was ignored by the plugin.");
                            }
                        }

                        AirTransmission air = new AirTransmission(entities, 0, 0);
                        diseaseToAdd.setAir(air);
                    }

                    if(!transmissionSection.getConfigurationSection("biome_transmission").equals(null)){
                        ConfigurationSection biomeSection = transmissionSection.getConfigurationSection("biome_transmission");

                        /*List<Biome> biomes = null;
                        for(Object entity : biomeSection.getList("entities")){
                            try{
                                biomes.add(org.bukkit. fromName(entity.toString().toUpperCase()));
                            }
                            catch(Exception e){
                                System.out.println(entity.toString() + " in " + disease.getString("name") + "'s air_transmission entity list isn't a valid entity so it was ignored by the plugin.");
                            }
                        }

                        BiomeTransmission biome = new BiomeTransmission(biomes, 0);
                        diseaseToAdd.setBiome(biome);*/
                    }
    

                    if(!transmissionSection.getConfigurationSection("consume_transmission").equals(null)){
                        ConfigurationSection consumeSection = transmissionSection.getConfigurationSection("consume_transmission");

                        /*List<EntityType> entities = null;
                        for(Object entity : airSection.getList("entities")){
                            try{
                                entities.add(EntityType.fromName(entity.toString().toUpperCase()));
                            }
                            catch(Exception e){
                                System.out.println(entity.toString() + " in " + disease.getString("name") + "'s air_transmission entity list isn't a valid entity so it was ignored by the plugin.");
                            }
                        }
                        
                        ConsumeTransmission consume = new ConsumeTransmission(null, 0);
                        diseaseToAdd.setConsume(consume);*/
                    }
    

                    if(!transmissionSection.getConfigurationSection("physical_transmission").equals(null)){
                        ConfigurationSection physicalSection = transmissionSection.getConfigurationSection("physical_transmission");

                        /*List<EntityType> entities = null;
                        for(Object entity : airSection.getList("entities")){
                            try{
                                entities.add(EntityType.fromName(entity.toString().toUpperCase()));
                            }
                            catch(Exception e){
                                System.out.println(entity.toString() + " in " + disease.getString("name") + "'s air_transmission entity list isn't a valid entity so it was ignored by the plugin.");
                            }
                        }
                        
                        PhysicalTransmission physical = new PhysicalTransmission(null, 0);
                        diseaseToAdd.setPhysical(physical);*/
                    }

                    if(!diseaseToAdd.haveTransmission()){
                        System.out.println(disease.getString("name") + " don't have any valid transmission settings, it was ignored by the plugin.");
                        continue;
                    }
                }

                diseases.add(diseaseToAdd);

                i++;
            }      
        }
    }
}