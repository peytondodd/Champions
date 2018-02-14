package me.limeglass.champions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.limeglass.champions.Champions;
import me.limeglass.champions.managers.ChampionsData;
import me.limeglass.champions.managers.PlayerManager;
import me.limeglass.champions.utils.Utils;

public class CommandHandler implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			Champions.consoleMessage("Only players may execute this command!");
			return true;
		}
		Player player = (Player) sender;
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("setup") && player.hasPermission("champions.admin")) {
				player.sendMessage("test message");
			} else if (args[0].equalsIgnoreCase("admin") && player.hasPermission("champions.admin")) {
				if (!Champions.getConfiguration("messages").isList("adminhelp")) return false;
				for (String message : Champions.getConfiguration("messages").getStringList("adminhelp")) {
					player.sendMessage(Utils.cc(message));
				}
			} else if (args[0].equalsIgnoreCase("setspawn") && player.hasPermission("champions.admin")) {
				ChampionsData.setSpawn(player.getLocation());
				player.sendMessage(Utils.getMessage("setspawn", player));
			} else if (args[0].equalsIgnoreCase("spawn")) {
				//TODO check if the player is in a Champions world + config option for it.
				if (!PlayerManager.isIngame(player)) {
					player.teleport(ChampionsData.getSpawn());
					player.sendMessage(Utils.getMessage("spawnteleport", player));
				} else {
					player.sendMessage(Utils.getMessage("spawnteleportIngame", player));
				}
			} else {
				player.performCommand("/champions");
			}
		} else {
			if (!Champions.getConfiguration("messages").isList("commandhelp")) return false;
			for (String message : Champions.getConfiguration("messages").getStringList("commandhelp")) {
				player.sendMessage(Utils.cc(message));
			}
		}
		return true;
	}
}
