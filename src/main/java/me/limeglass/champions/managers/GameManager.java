package me.limeglass.champions.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import me.limeglass.champions.objects.ChampionsGame;

public class GameManager {

	public static Map<Player, ChampionsGame> tempgames = new HashMap<Player, ChampionsGame>();
	private static Set<ChampionsGame> games = new HashSet<ChampionsGame>();
	
	public static void addGame(ChampionsGame game) {
		if (!games.contains(game)) games.add(game);
	}
	
	public static void removeGame(ChampionsGame game) {
		game.end();
		if (games.contains(game)) games.remove(game);
	}
	
	public static Boolean containsGame(String name) {
		for (ChampionsGame game : games) {
			if (game.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public static Set<ChampionsGame> getGames() {
		return games;
	}
	
	public static Set<ChampionsGame> getRunningGames() {
		Set<ChampionsGame> running = new HashSet<ChampionsGame>();
		for (ChampionsGame game : games) {
			if (game.isIngame()) running.add(game);
		}
		return (running != null && !running.isEmpty()) ? running : null;
	}
	
	public static Set<ChampionsGame> getIdlingGames() {
		Set<ChampionsGame> idle = new HashSet<ChampionsGame>();
		for (ChampionsGame game : games) {
			if (!game.isIngame()) idle.add(game);
		}
		return (idle != null && !idle.isEmpty()) ? idle : null;
	}
	
	public static void clearGames() {
		for (ChampionsGame game : games) {
			game.end();
		}
		games.clear();
	}
}
