package patatavival.Antonio.mvm;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import patatavival.Antonio.mvm.utils.Invasion;

public class Utils {
	public static Plugin pl = null;
	
	public static void send(String msg) {
		System.out.println(ChatColor.translateAlternateColorCodes('&', "[" + pl.getDescription().getName() + "] " + msg));
	}
	
	public static String colors(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public static void sendCommandAsOp(Player p, String command) {
		if (p.isOp()) {
			p.performCommand(command);
			return;
		}
		p.setOp(true);
		try {
			p.performCommand(command);
		} catch(Exception e) {}
		p.setOp(false);
	}
	
	public static Location coords(Player p, World w, String x, String y, String z) {
		double nx = 0;
		double ny = 0;
		double nz = 0;
		if (x.startsWith("~")) {
			if (x.length() == 1) {
				x+= "0";
			}
			nx = p.getLocation().getX() + Double.parseDouble(x.substring(1));
		} else {
			nx = Double.parseDouble(x);
		}
		
		if (y.startsWith("~")) {
			if (y.length() == 1) {
				y+= "0";
			}
			ny = p.getLocation().getY() + Double.parseDouble(y.substring(1));
		} else {
			ny = Double.parseDouble(y);
		}
		
		if (z.startsWith("~")) {
			if (z.length() == 1) {
				z+= "0";
			}
			nz = p.getLocation().getZ() + Double.parseDouble(z.substring(1));
		} else {
			nz = Double.parseDouble(z);
		}
		
		return new Location(w, nx, ny, nz);
	}
	
	public static void Invade(String ticket, Location loc) {
		Invasion i = new Invasion(ticket, loc);
		Main.invasions.add(i);
		i.start();
	}
	
	public static String generateUUID() {
		String[] chars = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
		String id = "";
		for (int i=0;i<8;i++) {
			id += chars[new Random().nextInt(chars.length-1)];
		}
		id += "-";
		for (int i=0;i<4;i++) {
			id += chars[new Random().nextInt(chars.length-1)];
		}
		id += "-";
		for (int i=0;i<4;i++) {
			id += chars[new Random().nextInt(chars.length-1)];
		}
		id += "-";
		for (int i=0;i<4;i++) {
			id += chars[new Random().nextInt(chars.length-1)];
		}
		id += "-";
		for (int i=0;i<12;i++) {
			id += chars[new Random().nextInt(chars.length-1)];
		}
		return id;
	}
	
	public static ArrayList<String> getCreditsItems(int credits) {
		ArrayList<String> credits_list = new ArrayList<String>();
		HashMap<String, String> reference = new HashMap<String, String>();
		if (pl.getConfig().getBoolean("credits.useTF2CreditsItems")) {
			reference.put("large", "rafradek_tf2_weapons:money 2");
			reference.put("medium", "rafradek_tf2_weapons:money 1");
			reference.put("small", "rafradek_tf2_weapons:money");
		} else {
			reference.put("large", pl.getConfig().getString("credits.items.large"));
			reference.put("medium", pl.getConfig().getString("credits.items.medium"));
			reference.put("small", pl.getConfig().getString("credits.items.small"));
		}
		int large = 0;
		while (credits-81 >= 0) {
			credits -= 81;
			large++;
		}
		if (large > 0) {
			credits_list.add(large + " " + reference.get("large"));
		}
		int medium = 0;
		while (credits-9 >= 0) {
			credits -= 9;
			medium++;
		}
		if (medium > 0) {
			credits_list.add(medium + " " + reference.get("medium"));
		}
		int small = 0;
		while (credits-1 >= 0) {
			credits--;
			small++;
		}
		if (small > 0) {
			credits_list.add(small + " " + reference.get("small"));
		}
		return credits_list;
	}
	
	public static String getTextByLangFile(Player p, String key) {
		return Utils.getTextByLangFile(p, key, null);
	}
	
	public static String getTextByLangFile(Player p, String key, Object[] args) {
		String l = p.getLocale();
		if (Main.langs.get(l) == null) {
			l = "en_us";
		}
		if (args != null) {
			return Main.langs.get(l).get(key, args);
		} else {
			return Main.langs.get(l).get(key);
		}
	}
	
	@SuppressWarnings("resource")
	public static void copyAssetsFolder() {
		String assetsDir = "assets";
		
		try {
			ZipFile plugzip = new ZipFile(new File(Bukkit.getPluginManager().getPlugin(Utils.pl.getDescription().getName()).getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
			Enumeration<? extends ZipEntry> e = plugzip.entries();
			File fol = Paths.get(pl.getDataFolder().getAbsolutePath()).toFile();
			if (!fol.isDirectory()) {
				fol.mkdir();
			}
			while(e.hasMoreElements()) {
				ZipEntry f = e.nextElement();
				if (f.getName().startsWith(assetsDir)) {
					File out = Paths.get(pl.getDataFolder().getAbsolutePath(), f.getName().substring(assetsDir.length()+1)).toFile();
					
					if (!out.isFile()) {
						File kk = Paths.get(pl.getDataFolder().getAbsolutePath()).toFile();
						for (String k : f.getName().substring(assetsDir.length()+1).split("/")) {
							kk = Paths.get(kk.getAbsolutePath(), k).toFile();
							if (!k.equals(out.getName())) {
								if (!kk.isDirectory()) {
									kk.mkdir();
								}
							}
						}
						InputStream fis = plugzip.getInputStream(f);
						FileOutputStream fos = new FileOutputStream(out);
						int data = fis.read();
						while(data != -1){
							fos.write(data);
							data = fis.read();
                        }
						fos.close();
						fis.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadAllLangs() {
		Main.langs.clear();
		if (Paths.get(pl.getDataFolder().getAbsolutePath(), "langs").toFile().isDirectory()) {
			Stream<Path> ph;
			try {
				ph = Files.walk(Paths.get(pl.getDataFolder().getAbsolutePath(), "langs"));
				ph.forEach(p -> {
					if (!p.toFile().isDirectory()) {
						Utils.send("Loading " + p.getFileName() + "...");
						Main.langs.put(p.toFile().getName().split("\\.")[0], new LangFile(p.toFile()));
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void checkPlayerRightclickEventItem(Player p, ItemStack item) {
		for (Object s : pl.getConfig().getKeys(true)) {
			String[] h = ((String) s).split("\\.");
			if (h.length > 1) {
				ItemStack target = pl.getConfig().getItemStack("invasions." + h[1] + ".item");
				if (target != null) {
					if (target.equals(item)) {
						for (Player p1 : Bukkit.getServer().getOnlinePlayers()) {
							p1.sendMessage(Utils.colors(Utils.getTextByLangFile(p1, "invasion.triggeredItem", new Object[]{p.getDisplayName(), pl.getConfig().getString("invasions." + h[1] + ".name")})));
						}
						Utils.Invade(h[1], p.getLocation());
						item.setAmount(1);
						p.getInventory().remove(item);
						break;
					}
				}
			}
		}
	}
}
