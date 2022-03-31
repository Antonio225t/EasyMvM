package patatavival.Antonio.mvm.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import patatavival.Antonio.mvm.Utils;

public class Wave implements Listener {
	
	private String ticket;
	private Location loc;
	private int wave;
	private Invasion inv;
	private ArrayList<UUID> enemies = new ArrayList<UUID>();
	private HashMap<UUID, Integer> enemies_points = new HashMap<UUID, Integer>();
	
	private int goal;
	private int progress = 0;
	private int timer;
	private double spawnRate;
	private int spawnDelayMin;
	private int spawnDelayMax;
	private int spawnMax;
	private int size;
	private boolean hasTimer = false;
	
	public Wave(String ticket, Location loc, int wave, Invasion invasion) throws Exception {
		
		Bukkit.getPluginManager().registerEvents(this, Utils.pl);
		this.ticket = ticket;
		this.loc = loc;
		this.wave = wave;
		this.inv = invasion;
		
		this.goal = Utils.pl.getConfig().getInt("invasions." + ticket + ".waves.wave" + wave + ".count");
		if (this.goal < 1) {
			throw new Exception("count cannot be les than 1!");
		}
		try {
			this.timer = Utils.pl.getConfig().getInt("invasions." + ticket + ".waves.wave" + wave + ".timer");
			if (this.timer > 0) {
				if (this.timer > 3600) {
					this.timer = 3599;
				}
				this.hasTimer = true;
			} else {
				this.hasTimer = false;
			}
		} catch(Exception e) {
			this.hasTimer = false;
		}
		
		this.size = Utils.pl.getConfig().getInt("invasions." + ticket + ".waves.wave" + wave + ".size");
		if (this.size < 1) {
			throw new Exception("size cannot be less than 1!");
		}
		
		boolean getSpawnRate = false;
		boolean getSpawnDelayMin = false;
		boolean getSpawnDelayMax = false;
		boolean getSpawnMax = false;
		int w = this.wave;
		while(w > 0) {
			if (Utils.pl.getConfig().isDouble("invasions." + ticket + ".waves.wave" + w + ".rateSpawn") && !getSpawnRate) {
				this.spawnRate = Utils.pl.getConfig().getDouble("invasions." + ticket + ".waves.wave" + w + ".rateSpawn");
				getSpawnRate = true;
			}
			if (Utils.pl.getConfig().isInt("invasions." + ticket + ".waves.wave" + w + ".minDelaySpawn") && !getSpawnDelayMin) {
				this.spawnDelayMin = Utils.pl.getConfig().getInt("invasions." + ticket + ".waves.wave" + w + ".minDelaySpawn");
				getSpawnDelayMin = true;
			}
			if (Utils.pl.getConfig().isInt("invasions." + ticket + ".waves.wave" + w + ".maxDelaySpawn") && !getSpawnDelayMax) {
				this.spawnDelayMax = Utils.pl.getConfig().getInt("invasions." + ticket + ".waves.wave" + w + ".maxDelaySpawn");
				getSpawnDelayMax = true;
			}
			if (Utils.pl.getConfig().isInt("invasions." + ticket + ".waves.wave" + w + ".maxSpawn") && !getSpawnMax) {
				this.spawnMax = Utils.pl.getConfig().getInt("invasions." + ticket + ".waves.wave" + w + ".maxSpawn");
				getSpawnMax = true;
			}
			
			w--;
		}
		
		if (!getSpawnRate) {
			this.spawnRate = 4;
		}
		if (!getSpawnDelayMin) {
			this.spawnDelayMin = 1;
		}
		if (!getSpawnDelayMax) {
			this.spawnDelayMax = 3;
		}
		if (!getSpawnMax) {
			this.spawnMax = 11;
		}
	}
	
	public void start() {
		try {
			this.inv.startWave(this.wave);
			new Thread(()->{
				this.timeStart();
			}).start();
			if (!this.hasTimer) {
				inv.updateProgress(this.progress, this.goal);
			}
			ArrayList<String> pickEnemies = new ArrayList<String>();
			List<String> en = null;
			boolean getEnemies = false;
			int a = this.wave;
			while (a > 0) {
				if (Utils.pl.getConfig().isList("invasions." + this.ticket + ".waves.wave" + a + ".enemies") && !getEnemies) {
					getEnemies = true;
					en = Utils.pl.getConfig().getStringList("invasions." + this.ticket + ".waves.wave" + a + ".enemies");
				}
				a--;
			}
			for (String s : en) {
				int x = Integer.parseInt(s.split(" ")[0].split(":")[0]);
				String nameId = s.split(" ")[0].split(":")[1] + " " + s.split(" ")[1] + " " + s.split(" ")[2];
				for (int i=0;i<x;i++) {
					pickEnemies.add(nameId);
				}
			}
			while (!this.inv.isWaveStopped(this.wave)) {
				Thread.sleep((long) (this.spawnRate * 1000));
				if (this.inv.isWaveStopped(this.wave)) {
					break;
				}
				if (this.enemies.size() < this.spawnMax) {
					int count = new Random().nextInt(this.spawnDelayMax - this.spawnDelayMin) + this.spawnDelayMin;
					while (count > 0) {
						if (this.enemies.size() < this.spawnMax) {
							int x = (new Random().nextInt(1) == 1 ? new Random().nextInt(this.size-1) : -new Random().nextInt(this.size - 1)) + loc.getBlockX();
							int y = loc.getBlockY();
							int z = (new Random().nextInt(1) == 1 ? new Random().nextInt(this.size-1) : -new Random().nextInt(this.size - 1)) + loc.getBlockZ();
							
							while (this.loc.getWorld().getBlockAt(x, y, z).isEmpty()) {
								y--;
							}
							
							while(!this.loc.getWorld().getBlockAt(x, y, z).isEmpty()) {
								y++;
								if (this.loc.getWorld().getBlockAt(x, y, z).isEmpty()) {
									y += 1;
								}
							}
							
							Location spawn = new Location(this.loc.getWorld(), x, y, z);
							String chooseEnemy = pickEnemies.get(new Random().nextInt(pickEnemies.size() - 1));
							this.loc.getWorld().setGameRuleValue("sendCommandFeedback", "false");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:summon " + chooseEnemy.split(" ")[1] + " " + spawn.getBlockX() + " " + spawn.getBlockY() + " " + spawn.getBlockZ());
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:entitydata @e[type=" + chooseEnemy.split(" ")[1] + ",x=" + spawn.getBlockX() + ",y=" + spawn.getBlockY() + ",z=" + spawn.getBlockZ() + ",r=1] " + chooseEnemy.split(" ")[2].substring(0, chooseEnemy.split(" ")[2].length()));
							this.loc.getWorld().setGameRuleValue("sendCommandFeedback", "true");
							boolean o = true;
							for (Entity e : spawn.getWorld().getNearbyEntities(spawn, 1, 1, 1)) {
								if (o) {
									if (chooseEnemy.split(" ")[1].replaceAll(":", "_").equalsIgnoreCase(e.getType() + "")) {
										this.enemies.add(e.getUniqueId());
										this.enemies_points.put(e.getUniqueId(), Integer.parseInt(chooseEnemy.split(" ")[0]));
										o = false;
									}
								}
							}
						}
						count--;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void hit(int i) {
		this.progress += i;
		if (!this.hasTimer) {
			this.inv.updateProgress(this.progress, this.goal);
		}
		if (this.progress >= this.goal) {
			this.inv.stopWave(this.wave);
			int credits = Utils.pl.getConfig().getInt("invasions." + this.ticket + ".waves.wave" + this.wave + ".rewards.credits");
			int l = 1;
			ArrayList<List<String>> rewards = new ArrayList<List<String>>();
			while (Utils.pl.getConfig().isList("invasions." + this.ticket + ".waves.wave" + this.wave + ".rewards.items.item" + l)) {
				rewards.add(Utils.pl.getConfig().getStringList("invasions." + this.ticket + ".waves.wave" + this.wave + ".rewards.items.item" + l));
				l++;
			}
			for (Player p : this.inv.getPlayers()) {
				if (Utils.pl.getConfig().getBoolean("sendRewardsEvenWhenNoRewards")) {
					p.sendMessage(Utils.colors(Utils.getTextByLangFile(p, "invasion.getrewards")));
				} else {
					if (rewards.size() > 0) {
						boolean canSend = false;
						for (List<String> items : rewards) {
							if (items.size() > 0) {
								canSend = true;
							}
						}
						if (credits > 0) {
							canSend = true;
						}
						if (canSend) {
							p.sendMessage(Utils.colors(Utils.getTextByLangFile(p, "invasion.getrewards")));
						}
					}
				}
				for (List<String> items : rewards) {
					ArrayList<String> patata = new ArrayList<String>();
					for (String item : items) {
						int x = Integer.parseInt(item.split(" ")[0]);
						for (int k=0;k<x;k++) {
							patata.add(item.substring((item.split(" ")[0] + " ").length()));
						}
					}
					
					String item = patata.get(new Random().nextInt(patata.size()-1));
					p.sendMessage(Utils.colors(Utils.getTextByLangFile(p, "invasion.reward", new Object[] {(item.contains(":") ? item.split(" ")[0].split(":")[1] : item)})));
					this.loc.getWorld().setGameRuleValue("sendCommandFeedback", "false");
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:give " + p.getName() + " " + item.split(" ")[0] + " " + item.split(" ")[1]);
					this.loc.getWorld().setGameRuleValue("sendCommandFeedback", "true");
				}
				if (credits > 0) {
					p.sendMessage(Utils.colors(Utils.getTextByLangFile(p, "invasion.credits", new Object[]{credits})));
					for (String item : Utils.getCreditsItems(credits)) {
						String data = "0";
						String nbt = "{}";
						try {
							data = item.split(" ")[2];
						} catch(Exception e) {}
						try {
							nbt = item.substring((item.split(" ")[0] + " " + item.split(" ")[1] + " " + item.split(" ")[2] + " ").length());
						} catch(Exception e) {}
						this.loc.getWorld().setGameRuleValue("sendCommandFeedback", "false");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:give " + p.getName() + " " + item.split(" ")[1] + " " + item.split(" ")[0] + " " + data + " " + nbt);
						this.loc.getWorld().setGameRuleValue("sendCommandFeedback", "true");
					}
				}
			}
			killAllEnemies();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void killAllEnemies() {
		Iterator<UUID> a = this.enemies.iterator();
		while(a.hasNext()) {
			UUID u = a.next();
			try {
				LivingEntity e = ((LivingEntity)Bukkit.getEntity(u));
				e.damage(e.getMaxHealth() + 10);
				a.remove();
			} catch(Exception e) {}
		}
		if (!this.inv.isStopped()) {
			this.inv.nextWave();
		}
	}
	
	public ArrayList<UUID> getEnemies() {
		return this.enemies;
	}
	
	@SuppressWarnings("deprecation")
	private void timeStart() {
		if (this.hasTimer) {
			Date start = new Date(System.currentTimeMillis());
			try {
				while (!this.inv.isWaveStopped(this.wave)) {
					Thread.sleep(10);
					if ((start.getTime() + (this.timer * 1000)) <= System.currentTimeMillis()) {
						this.inv.stop();
						break;
					}
					this.inv.updateProgress(this.progress, this.goal, "Time: " + new SimpleDateFormat("mm:ss").format(Date.UTC(0, 0, 0, 0, 0, (int) (this.timer - (( ((System.currentTimeMillis()) - start.getTime())) / 1000)))));
				}
			} catch(Exception e) {}
		}
	}
	
	@EventHandler
	public void onEnemieKill(EntityDeathEvent e) {
		if (!this.inv.isWaveStopped(this.wave) && this.inv.isWaveStarted(this.wave)) {
			if (this.getEnemies().contains((Object) e.getEntity().getUniqueId())) {
				this.getEnemies().remove(e.getEntity().getUniqueId());
				int points = this.enemies_points.get(e.getEntity().getUniqueId());
				this.enemies_points.remove(e.getEntity().getUniqueId());
				this.hit(points);
			}
		}
	}

}
