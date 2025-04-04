package me.kaeternn.pasteurcraft.tasks;

import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.kaeternn.pasteurcraft.PasteurCraft;
import me.kaeternn.pasteurcraft.UsersData;
import me.kaeternn.pasteurcraft.entities.Disease;

public class PasteurCraftTask extends BukkitRunnable {
    private final PasteurCraft plugin;

    public PasteurCraftTask(PasteurCraft plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()){
            YamlConfiguration data = UsersData.getOrCreate(player);
            ConfigurationSection diseases = data.getConfigurationSection("diseases");

            if (!(diseases == null)){
                for (Disease disease : PasteurCraft.diseases){
                    if(diseases.contains(disease.getName())){
                        ConfigurationSection diseaseData = diseases.getConfigurationSection(disease.getName());

                        if(!diseaseData.getBoolean("immunity"))
                        {
                            if (player.getStatistic(Statistic.PLAY_ONE_MINUTE) > diseaseData.getInt("startplaytime") + diseaseData.getInt("incubation") + diseaseData.getInt("duration")){
                                disease.cure(player);
                                disease.unApply(player);
                                player.sendMessage(plugin.getMSG("notify_disease_effect_stop").replace("%disease%", disease.getName()));
                            }
                            else if (player.getStatistic(Statistic.PLAY_ONE_MINUTE) > diseaseData.getInt("startplaytime") + diseaseData.getInt("incubation")){
                                disease.apply(player);
                                if (!diseaseData.getBoolean("messagesent")){
                                    diseaseData.set("messagesent", true);
                                    diseases.set(disease.getName(), diseaseData);
                                    data.set("diseases", diseases);
                                    try {
                                        UsersData.save(player, data);
                                    } catch (Exception e) {}
                                    
                                    player.sendMessage(plugin.getMSG("notify_disease_effect_start").replace("%disease%", disease.getName()));
                                }

                                // DEBUG
                                player.sendMessage(disease.getName() + " est appliquée." + ((diseaseData.getInt("startplaytime") + diseaseData.getInt("incubation") + diseaseData.getInt("duration") - player.getStatistic(Statistic.PLAY_ONE_MINUTE)) / 20 / 60) + " minutes restantes.");
                            }
                            else{
                                // DEBUG
                                player.sendMessage(disease.getName() + " est dans sa phase d'incubation." + ((diseaseData.getInt("startplaytime") + diseaseData.getInt("incubation") - player.getStatistic(Statistic.PLAY_ONE_MINUTE)) / 20 / 60) + " minutes restantes.");
                            }
                        }
                    }
                }
            }
        }
    }
}
