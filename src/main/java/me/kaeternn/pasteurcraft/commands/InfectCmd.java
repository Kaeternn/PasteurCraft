package me.kaeternn.pasteurcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kaeternn.pasteurcraft.PasteurCraft;
import me.kaeternn.pasteurcraft.entities.Disease;

public class InfectCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Usage: /infect <name_of_the_disease> <player>");
            return false; }
        
        Disease disease = null;
        Player player = Bukkit.getOfflinePlayer(args[1]).getPlayer();

        for(Disease dis : PasteurCraft.diseases){
            if(dis.getName().replace('_', ' ').equalsIgnoreCase(args[0])){ disease = dis; }
        }

        if(disease == null){
            sender.sendMessage("Disease not found.");
            return false; }

        if(player == null){
            sender.sendMessage("Player not found.");
            return false; }

        disease.infect(player);
        
        return false;
    }
}
