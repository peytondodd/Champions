package me.limeglass.champions.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.limeglass.champions.Champions;
import me.limeglass.champions.managers.InventoryManager;
import me.limeglass.champions.utils.Utils;

public class CommandHandler implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			Champions.consoleMessage("Only players may execute this command!");
			return true;
		}
		Player player = (Player) sender;
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("admin") && Utils.isSecure(player)) {
				player.sendMessage("test admin message");
			} else {
				player.performCommand("/kitpvp");
			}
		} else if (player.hasPermission("kitpvp.use")) {
			player.openInventory(InventoryManager.get("mainmenu"));
		}
		return true;
	}
}
