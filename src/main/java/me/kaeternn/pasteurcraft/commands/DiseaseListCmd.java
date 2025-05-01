package me.kaeternn.pasteurcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.kaeternn.pasteurcraft.PasteurCraft;
import me.kaeternn.pasteurcraft.entities.Disease;

public class DiseaseListCmd implements CommandExecutor  {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        try{
        sender.sendMessage(PasteurCraft.plugin.getMSG("cmd_diseaselist_header"));
        } catch (NullPointerException e) {
            sender.sendMessage(e.getMessage());
            return false;
        }
        for (Disease disease : PasteurCraft.diseases) sender.sendMessage("    - " + disease.getName());

        return true;
    }
}
