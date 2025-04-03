package me.kaeternn.pasteurcraft.tasks;

import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.kaeternn.pasteurcraft.PasteurCraft;
import me.kaeternn.pasteurcraft.UsersData;
import me.kaeternn.pasteurcraft.entities.Disease;

public class PasteurCraftTask extends BukkitRunnable {
    private final JavaPlugin plugin;

    public PasteurCraftTask(JavaPlugin plugin) { this.plugin = plugin; }

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
                                player.sendMessage("[PasteurCraft] Vous ne ressentez plus les effets de " + disease.getName() + ".");
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
                                    
                                    player.sendMessage("[PasteurCraft] Vous commencez à ressentir les effets de " + disease.getName() + ".");
                                }
                                player.sendMessage(disease.getName() + " est appliquée." + ((diseaseData.getInt("startplaytime") + diseaseData.getInt("incubation") + diseaseData.getInt("duration") - player.getStatistic(Statistic.PLAY_ONE_MINUTE)) / 20 / 60) + " minutes restantes.");
                            }
                            else{
                                player.sendMessage(disease.getName() + " est dans sa phase d'incubation." + ((diseaseData.getInt("startplaytime") + diseaseData.getInt("incubation") - player.getStatistic(Statistic.PLAY_ONE_MINUTE)) / 20 / 60) + " minutes restantes.");
                            }
                        }
                    }
                }
            }
        }
    }
}
