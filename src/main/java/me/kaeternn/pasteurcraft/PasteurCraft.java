package me.kaeternn.pasteurcraft;

import java.util.List;
import org.bukkit.entity.EntityType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import me.kaeternn.pasteurcraft.object.Disease;

public class PasteurCraft extends JavaPlugin{
    public static PasteurCraft PLUGIN;

    @Override
    public void onEnable(){
        saveDefaultConfig();
        PasteurCraft.PLUGIN = this;
        ConfigurationSection diseases = PasteurCraft.PLUGIN.getConfig().getConfigurationSection("diseases");

        if(diseases.equals(null)){
            System.out.println("There is no disease in the configuration file, please note that the plugin won't do anything without one.");
        }
        else
            for(String key : diseases.getKeys(false)){
                ConfigurationSection disease = diseases.getConfigurationSection(key);
                List<EntityType> hosts = null;
                List<EntityType> vectors = null;
                
                for(Object host : disease.getList("hosts")){
                    try{
                        hosts.add(EntityType.fromName(host.toString()));
                    }
                    catch(Exception e){
                        System.out.println(host.toString() + " in " + disease.getString("name") + "'s host list isn't a valid entity so it was ignored by the plugin.");
                    }
                }
                
                for(Object vector : disease.getList("vectors")){
                    try{
                        vectors.add(EntityType.fromName(vector.toString()));
                    }
                    catch(Exception e){
                        System.out.println(vector.toString() + " in " + disease.getString("name") + "'s vector list isn't a valid entity so it was ignored by the plugin.");
                    }
                }

            //new Disease(disease.getString("name"),);
        }      
    }
}