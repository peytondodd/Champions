package me.limeglass.champions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import me.limeglass.champions.Champions;
import me.limeglass.champions.managers.Functions;
import me.limeglass.champions.managers.InventoryManager;
import me.limeglass.champions.managers.PlayerManager;
import me.limeglass.champions.objects.ChampionsPlayer;
import me.limeglass.champions.scoreboard.ChampionsScoreboard;
import me.limeglass.champions.utils.Utils;

public class EventListener implements Listener {
	
	private final FileConfiguration joinItems = Champions.getConfiguration("joinItems");
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
		Inventory inventory = event.getClickedInventory();
		Player player = (Player) event.getWhoClicked();
		ChampionsPlayer championsPlayer = PlayerManager.getChampionsPlayer(player);
		if (InventoryManager.isMenu(inventory)) {
			event.setCancelled(true);
			InventoryManager.getMenu(inventory).onInventoryClick(event);
		} else if (inventory.getType() == InventoryType.PLAYER && championsPlayer.isIngame()) {
			if (!player.hasPermission("champions.ingame.moveinventory")) event.setCancelled(true);
		} else if (inventory.getType() == InventoryType.PLAYER) {
			if (Champions.isBungeecordMode() && !player.hasPermission("champions.bungeemode.moveinventory")) event.setCancelled(true);
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
				if (Utils.getItem(joinItems, "JoinItems." + event.getSlot()).isSimilar(event.getCurrentItem())) {
					event.setCancelled(true);
					if (joinItems.isSet("JoinItems." + event.getSlot() + ".function")) {
						Functions.executeFunction(championsPlayer, joinItems.getString("JoinItems." + event.getSlot() + ".function"));
					}
				}
			}
		}
    }
	
	@EventHandler
    public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ChampionsPlayer championsPlayer = PlayerManager.getChampionsPlayer(player);
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
				for (String slot : joinItems.getConfigurationSection("JoinItems").getKeys(false)) {
					if (Utils.getItem(joinItems, "JoinItems." + slot).isSimilar(event.getItem())) {
						event.setCancelled(true);
						if (joinItems.isSet("JoinItems." + slot + ".function")) {
							Functions.executeFunction(championsPlayer, joinItems.getString("JoinItems." + slot + ".function"));
							break;
						}
					}
				}
			}
		}
    }
	
	@EventHandler
    public void onJoin(PlayerLoginEvent event) {
		final Player player = event.getPlayer();
		//TODO player.teleport(stuff
		//This task is delayed because on some Minecraft versions there is a glitch that when clearing an inventory on join
		//it makes the player deal less damage for some reason.`
		Bukkit.getScheduler().scheduleSyncDelayedTask(Champions.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (!Utils.isEmpty(player.getInventory())) player.getInventory().clear();
				for (String value : joinItems.getConfigurationSection("JoinItems").getKeys(false)) {
					int slot = Integer.parseInt(value);
					if (!(slot < 0 || slot > InventoryType.PLAYER.getDefaultSize())) {
						player.getInventory().setItem(slot, Utils.getItem(joinItems, "JoinItems." + value));
					}
				}
				//TODO player.teleport(stuff
				ChampionsPlayer championsPlayer = PlayerManager.getChampionsPlayer(player);
				championsPlayer.setScoreboard(new ChampionsScoreboard(championsPlayer));
			}
		}, 1);
    }
	
	@EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
		ChampionsPlayer player = PlayerManager.getChampionsPlayer(event.getPlayer());
		PlayerManager.removePlayer(player);
    }
	
	@EventHandler
    public void onDisconnect(WeatherChangeEvent event) {
		if (Champions.isBungeecordMode()) event.setCancelled(event.toWeatherState());
    }
}