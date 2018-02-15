package me.limeglass.champions.objects;

import java.util.HashSet;
import java.util.Set;

import me.limeglass.champions.managers.GameManager;
import me.limeglass.champions.managers.PlayerManager;

public class ChampionsGame {
	
	private final ChampionsMode mode;
	private ChampionsState state;
	private String name;
	
	private enum ChampionsState {
		WAITING,
		INGAME,
		STARTING,
		RESTARTING,
		DISABLED;
	}
	
	public enum ChampionsMode {
		TEAMDEATHMATCH;
	}
	
	public ChampionsGame(String name, ChampionsMode mode) {
		this.mode = mode;
		this.name = name;
		GameManager.addGame(this);
	}
	
	public ChampionsGame(Boolean temp, String name, ChampionsMode mode) {
		this.mode = mode;
		this.name = name;
		if (!temp) GameManager.addGame(this);
	}
	
	public final Set<ChampionsPlayer> getPlayers() {
		Set<ChampionsPlayer> players = new HashSet<ChampionsPlayer>();
		for (ChampionsPlayer player : PlayerManager.getPlayers()) {
			if (player.getMap().equalsIgnoreCase(name)) players.add(player);
		}
		return players;
	}

	public Boolean isIngame() {
		return state == ChampionsState.INGAME;
	}
	
	public void setState(ChampionsState state) {
		this.state = state;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void end() {
		//TODO
	}

	public ChampionsMode getMode() {
		return mode;
	}
}