package me.kaeternn.pasteurcraft.tasks;

import java.time.LocalDateTime;

import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.kaeternn.pasteurcraft.PasteurCraft;
import me.kaeternn.pasteurcraft.UsersData;
import me.kaeternn.pasteurcraft.entities.Disease;

public class DiseaseCheckTask extends BukkitRunnable {
    private final JavaPlugin plugin;

    public DiseaseCheckTask(JavaPlugin plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()){
            ConfigurationSection data = UsersData.getOrCreate(player).getConfigurationSection("diseases");

            if (!(data == null)){
                for (Disease disease : PasteurCraft.diseases){
                    if(data.contains(disease.getName())){
                        ConfigurationSection diseaseData = data.getConfigurationSection(disease.getName());

                        if(!diseaseData.getBoolean("immunity"))
                        {
                            if (player.getStatistic(Statistic.PLAY_ONE_MINUTE) > diseaseData.getInt("startplaytime") + diseaseData.getInt("incubation") + diseaseData.getInt("duration")){
                                disease.cure(player);
                                disease.unApply(player);
                                player.sendMessage(disease.getName() + " est finie.");
                            }
                            else if (player.getStatistic(Statistic.PLAY_ONE_MINUTE) > diseaseData.getInt("startplaytime") + diseaseData.getInt("incubation")){
                                disease.apply(player);
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
