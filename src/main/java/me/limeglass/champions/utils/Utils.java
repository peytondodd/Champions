package me.limeglass.champions.utils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import me.limeglass.champions.Champions;

public class Utils {
	
	public static boolean compareArrays(String[] arg1, String[] arg2) {
		if (arg1.length != arg2.length) return false;
		Arrays.sort(arg1);
		Arrays.sort(arg2);
		return Arrays.equals(arg1, arg2);
	}
	
	public static Boolean isEmpty(Inventory inventory) {
		for (ItemStack item : inventory.getContents()) {
			if (item != null && item.getType() != Material.AIR) {
				return false;
			}
		}
		return true;
	}
	
	public static Boolean isEnum(Class<?> clazz, String object) {
		try {
			final Method method = clazz.getMethod("valueOf", String.class);
			method.setAccessible(true);
			method.invoke(clazz, object.replace("\"", "").trim().replace(" ", "_").toUpperCase());
			return true;
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException error) {
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getEnum(Class<?> clazz, String object) {
		try {
			final Method method = clazz.getMethod("valueOf", String.class);
			method.setAccessible(true);
			return (T) method.invoke(clazz, object.replace("\"", "").trim().replace(" ", "_").toUpperCase());
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException error) {
			Champions.consoleMessage("&cUnknown type " + object + " in " + clazz.getName());
			return null;
		}
	}
	
	public static Class<?> getArrayClass(Class<?> parameter) {
		return Array.newInstance(parameter, 0).getClass();
	}
	
	public static ItemStack getItem(FileConfiguration configuration, String node) {
		String name = configuration.getString(node + ".name");
		String[] lores = null;
		if (configuration.isSet(node + ".lores")) lores = configuration.getStringList(node + ".lores").toArray(new String[configuration.getStringList(node + ".lores").size()]);
		String material = configuration.getString(node + ".material", "AIR");
		List<ItemFlag> flags = null;
		if (configuration.isSet(node + ".itemflags")) {
			for (String flag : configuration.getStringList(node + ".itemflags")) {
				try {
					flags.add(Utils.getEnum(ItemFlag.class, flag));
				} catch (NullPointerException e) {}
			}
		}
		Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
		if (configuration.isSet(node + ".enchantments")) {
			for (String enchantment : configuration.getStringList(node + ".enchantments")) {
				try {
					if (!enchantment.contains(":")) {
						enchantments.put(Enchantment.getByName(enchantment), 1);
					} else {
						String[] handle = enchantment.split(":");
						int slot = Integer.parseInt(handle[1]);
						if (slot != -1) enchantments.put(Enchantment.getByName(handle[0]), slot);
						else enchantments.put(Enchantment.getByName(handle[0]), 1);
					}
				} catch (NullPointerException e) {}
			}
		}
		short colour = (short) configuration.getInt(node + ".colour", -1);
		//TODO add more option from the ItemBuilder if needed.
		if (colour == -1) return new ItemBuilder(material, 1).flags(flags).displayname(name).lore(lores).build();
		else return new ItemBuilder(material, 1).displayname(name).lore(lores).durability(colour).build();
	}
	
	public static String[] colour(String... messages) {
		for (int message = 0; message < messages.length; message++) {
			for (int i = 1; i <= 3; i++) {
				messages[message] = messages[message].replaceAll(Pattern.quote("{" + i + "}"), colour(i));
			}
		}
		return messages;
	}
	
	public static String colour(int index) {
		return (index == 1) ? Champions.getConfiguration("messages").getString("colours.main") : (index == 2) ? Champions.getConfiguration("messages").getString("colours.secondary") : Champions.getConfiguration("messages").getString("colours.third");
	}

	public static String cc(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	public static String[] getMessage(String node, Player player) {
		FileConfiguration configuration = Champions.getConfiguration("messages");
		String value = configuration.getString(node);
		if (value == null) return null;
		value = value.replaceAll(Pattern.quote("{PLAYER}"), player.getName());
		value = value.replaceAll(Pattern.quote("{PLAYER_DISPLAY_NAME}"), player.getDisplayName());
		return colour(value);
	}
}
