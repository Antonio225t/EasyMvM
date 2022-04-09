package patatavival.Antonio.mvm;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import patatavival.Antonio.mvm.utils.Invasion;

public class Main extends JavaPlugin {
	
	public static ArrayList<Invasion> invasions = new ArrayList<Invasion>();
	public static HashMap<String, LangFile> langs = new HashMap<String, LangFile>();
	
	@Override
	public void onEnable() {
		Utils.pl = this;
		Utils.send("&7Enabling TF2's MVM support plugin...");
		new Events(this);
		//this.getConfig().options().copyDefaults(true);
		Utils.copyAssetsFolder();
		Utils.loadAllLangs();
		Utils.send("&6TF2's MVM support plugin enabled!");
	}
	
	@Override
	public void onDisable() {
		Utils.send("&cTF2's MVM support plugin disabled!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		Player player = null;
		try {
			player = (Player) sender;
		} catch(Exception e) {}
		
		if (alias.equalsIgnoreCase("mvminvasion")) {
			if (sender.hasPermission("mvm.invasion")) {
				if (args.length > 0) {
					String arg1 = args[0];
					if (arg1.equalsIgnoreCase("reload")) {
						if (sender.hasPermission("mvm.invasion.reload")) {
							this.reloadConfig();
							Utils.loadAllLangs();
							sender.sendMessage(Utils.colors(Utils.getTextByLangFile(player, "mvminvasion.reload")));
						} else {
							sender.sendMessage(Utils.getTextByLangFile(player, "nopermissions"));
						}
					} else if (arg1.equalsIgnoreCase("spawn")) {
						if (sender.hasPermission("mvm.invasion.spawn")) {
							if (args.length > 1) {
								String name = null;
								Location loc = null;
								try {
									name = args[1];
									if (args.length > 2) {
										loc = Utils.coords(player, player.getLocation().getWorld(), args[2], args[3], args[4]);
									} else {
										loc = player.getLocation();
									}
								} catch(Exception e) {
									sender.sendMessage(Utils.colors(Utils.getTextByLangFile(player, "mvminvasion.spawn.usage")));
									return true;
								}
								
								if (this.getConfig().isConfigurationSection("invasions." + name)) {
									Utils.Invade(name, loc);
								} else {
									sender.sendMessage(Utils.colors(Utils.getTextByLangFile(player, "mvminvasion.spawn.ticketnotfound", new Object[]{name})));
								}
							} else {
								sender.sendMessage(Utils.colors(Utils.getTextByLangFile(player, "mvminvasion.spawn.usage")));
							}
						} else {
							sender.sendMessage(Utils.getTextByLangFile(player, "nopermissions"));
						}
					} else if (arg1.equalsIgnoreCase("stop")) {
						for (Invasion i : invasions) {
							i.stop(false);
						}
						invasions.clear();
					}
				} else {
					sender.sendMessage(Utils.colors(Utils.getTextByLangFile(player, "mvminvasion.usage")));
				}
			} else {
				sender.sendMessage(Utils.getTextByLangFile(player, "nopermissions"));
			}
		}
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		@SuppressWarnings("unused")
		Player p = null;
		try {
			p = (Player) sender;
		} catch(Exception e) {}
		
		if (alias.equalsIgnoreCase("mvminvasion")) {
			if (sender.hasPermission("mvm.invasion")) {
				if (args.length > 1) {
					String arg1 = args[0];
					if (arg1.equalsIgnoreCase("spawn")) {
						if (sender.hasPermission("mvm.invasion.spawn")) {
							List<String> tab = new ArrayList<String>();
							for (Object s : this.getConfig().getKeys(true)) {
								String[] h = ((String) s).split("\\.");
								if (h.length > 1) {
									if (h[0].equals("invasions") && h[1].contains(args[1])) {
										tab.add(h[1]);
									}
								}
							}
							if (args.length > 2 && args.length < 6) {
								tab.clear();
								tab.add("~");
							} else if (args.length >= 7) {
								tab.clear();
							}
							return tab;
						}
					}
				} else {
					List<String> tab1 = new ArrayList<String>();
					tab1.add("spawn");
					tab1.add("reload");
					tab1.add("stop");
					List<String> tab = new ArrayList<String>();
					for (String g : tab1) {
						if (g.startsWith(args[0])) {
							tab.add(g);
						}
					}
					return tab;
				}
			}
		}
		return null;
	}
}