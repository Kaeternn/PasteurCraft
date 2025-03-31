package me.kaeternn.pasteurcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kaeternn.pasteurcraft.PasteurCraft;
import me.kaeternn.pasteurcraft.entities.Disease;

public class CureCmd implements CommandExecutor  {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length < 2) { // Check if there are enough arguments
            return false; 
        }
        
        Disease disease = null;
        for(Disease diseaseByName : PasteurCraft.diseases){ // Try to match the disease name with the disease argument
            if(diseaseByName.getName().equals(args[0].replace('_', ' '))){
                disease = diseaseByName;
            }
        }

        if(disease == null){ // Verify that the disease was found
            sender.sendMessage("Disease not found.");
            return false; 
        }

        Player player = Bukkit.getOfflinePlayer(args[1]).getPlayer();
        if(player == null){ // Verify that the player was found
            sender.sendMessage("Player not found.");
            return false; 
        }

        disease.cure(player);
        return true;
    }
}
