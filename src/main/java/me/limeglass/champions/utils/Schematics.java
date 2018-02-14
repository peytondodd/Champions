package me.limeglass.champions.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;

import me.limeglass.champions.Champions;

public class Schematics {

	public static void paste() {
		File directory = new File(Champions.getInstance().getDataFolder() + File.separator + "schematics");
		if (!directory.exists()) directory.mkdir();
		if (directory.listFiles() == null || directory.listFiles().length <= 0) return;
		File[] schematics = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".schematic");
			}
		});
		int random = new Random().nextInt(schematics.length);
		File schematic = schematics[random];
		if (Bukkit.getWorld("Kitpvp") != null) {
			File kitpvp = Bukkit.getWorld("Kitpvp").getWorldFolder();
			Bukkit.unloadWorld("Kitpvp", false);
			delete(kitpvp);
		}
		Bukkit.createWorld(new WorldCreator("Kitpvp").generator("EmptyWorldGenerator"));
		try {
			World weWorld = new BukkitWorld(Bukkit.getWorld("Kitpvp"));
			WorldData worldData = weWorld.getWorldData();
			Clipboard clipboard = ClipboardFormat.SCHEMATIC.getReader(new FileInputStream(schematic)).read(worldData);
			Extent extent = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1);
			AffineTransform transform = new AffineTransform();
			ForwardExtentCopy copy = new ForwardExtentCopy(extent, clipboard.getRegion(), clipboard.getOrigin(), extent, new Vector(0, 0, 0));
			if (!transform.isIdentity()) copy.setTransform(transform);
			copy.setSourceMask(new ExistingBlockMask(clipboard));
			Operations.completeLegacy(copy);
		} catch (NullPointerException | IOException | MaxChangedBlocksException e) {
			e.printStackTrace();
		}
	}
	
	public static void delete(File path) {
		if (path.exists()) {
			for (File file : path.listFiles()) {
				if (file.isDirectory()) {
					delete(file);
				} else {
					file.delete();
				}
			}
			path.delete();
		}
	}
}
