package patatavival.Antonio.mvm.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import patatavival.Antonio.mvm.Main;
import patatavival.Antonio.mvm.Utils;

public class Invasion {
	
	private String ticket;
	private Location loc;
	private ArrayList<Wave> waves;
	private BossBar bb;
	private ArrayList<Player> players;
	private ArrayList<Boolean> stoppedWaves = new ArrayList<Boolean>();
	private ArrayList<Boolean> startedWaves = new ArrayList<Boolean>();
	
	private int atWave = 0;
	private boolean didStop = false;
	
	public Invasion(String ticket, Location loc) {
		this.ticket = ticket;
		this.loc = loc;
		this.waves = new ArrayList<Wave>();
		int i=1;
		Invasion that = this;
		while (Utils.pl.getConfig().isConfigurationSection("invasions." + ticket + ".waves.wave" + i)) {
			Wave w = null;
			try {
				w = new Wave(ticket, loc, i, that);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			waves.add(w);
			that.stoppedWaves.add(false);
			that.startedWaves.add(false);
			i++;
		}
	}
	
	public void start() {
		this.atWave = 1;
		this.didStop = false;
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(Utils.colors(Utils.getTextByLangFile(p, "invasion.spawned", new Object[]{loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()})));
		}
		double size = Utils.pl.getConfig().getDouble("invasions." + ticket + ".waves.1.size");
		this.bb = Bukkit.createBossBar("Invasion - Wave " + this.atWave + "/" + this.waves.size(), BarColor.BLUE, BarStyle.SOLID);
		this.players = new ArrayList<Player>();
		for (Entity e : loc.getWorld().getNearbyEntities(loc, size + 25, loc.getWorld().getMaxHeight(), size + 25)) {
			if (e instanceof Player) {
				this.bb.addPlayer((Player) e);
				players.add((Player) e);
			}
		}
		Invasion that = this;
		new Thread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				try {
					Thread.sleep(Utils.pl.getConfig().getInt("waveInterval") * 1000);
					for (Player p : players) {
						if (Utils.pl.getConfig().getBoolean("counting.enabled")) {
							Sound s = null;
							float pitch = Float.parseFloat(Utils.pl.getConfig().getString("counting.3.pitch"));
							float volume = Float.parseFloat(Utils.pl.getConfig().getString("counting.3.volume"));
							for (Sound s1 : Sound.values()) {
								if (s1.name().equals(Utils.pl.getConfig().getString("counting.3.sound"))) {
									s = s1;
								}
							}
							p.playSound(p.getLocation(), s, volume, pitch);
						}
						p.sendTitle(Utils.colors(Utils.getTextByLangFile(p, "invasion.count.3")), Utils.colors(Utils.getTextByLangFile(p, "invasion.count.3d")));
					}
					Thread.sleep(1000);
					for (Player p : players) {
						if (Utils.pl.getConfig().getBoolean("counting.enabled")) {
							Sound s = null;
							float pitch = Float.parseFloat(Utils.pl.getConfig().getString("counting.2.pitch"));
							float volume = Float.parseFloat(Utils.pl.getConfig().getString("counting.2.volume"));
							for (Sound s1 : Sound.values()) {
								if (s1.name().equals(Utils.pl.getConfig().getString("counting.2.sound"))) {
									s = s1;
								}
							}
							p.playSound(p.getLocation(), s, volume, pitch);
						}
						p.sendTitle(Utils.colors(Utils.getTextByLangFile(p, "invasion.count.2")), Utils.colors(Utils.getTextByLangFile(p, "invasion.count.2d")));
					}
					Thread.sleep(1000);
					for (Player p : players) {
						if (Utils.pl.getConfig().getBoolean("counting.enabled")) {
							Sound s = null;
							float pitch = Float.parseFloat(Utils.pl.getConfig().getString("counting.1.pitch"));
							float volume = Float.parseFloat(Utils.pl.getConfig().getString("counting.1.volume"));
							for (Sound s1 : Sound.values()) {
								if (s1.name().equals(Utils.pl.getConfig().getString("counting.1.sound"))) {
									s = s1;
								}
							}
							p.playSound(p.getLocation(), s, volume, pitch);
						}
						p.sendTitle(Utils.colors(Utils.getTextByLangFile(p, "invasion.count.1")), Utils.colors(Utils.getTextByLangFile(p, "invasion.count.1d")));
					}
					Thread.sleep(1000);
					for (Player p : players) {
						if (Utils.pl.getConfig().getBoolean("counting.enabled")) {
							Sound s = null;
							float pitch = Float.parseFloat(Utils.pl.getConfig().getString("counting.start.pitch"));
							float volume = Float.parseFloat(Utils.pl.getConfig().getString("counting.start.volume"));
							for (Sound s1 : Sound.values()) {
								if (s1.name().equals(Utils.pl.getConfig().getString("counting.start.sound"))) {
									s = s1;
								}
							}
							p.playSound(p.getLocation(), s, volume, pitch);
						}
						p.sendTitle(Utils.colors(Utils.getTextByLangFile(p, "invasion.count.start")), Utils.colors(Utils.getTextByLangFile(p, "invasion.count.startd")));
					}
					that.waves.get(that.atWave - 1).start();
				} catch(Exception e) {}
			}
		}).start();
	}
	
	@SuppressWarnings("deprecation")
	public void nextWave() {
		if (this.atWave < this.waves.size()) {
			this.atWave++;
			this.bb.setTitle("Invasion - Wave " + this.atWave + "/" + this.waves.size());
			this.bb.setProgress(1);
			new Thread(() -> {
				try {
					Thread.sleep(Utils.pl.getConfig().getInt("waveInterval") * 1000);
					for (Player p : players) {
						if (Utils.pl.getConfig().getBoolean("counting.enabled")) {
							Sound s = null;
							float pitch = Float.parseFloat(Utils.pl.getConfig().getString("counting.3.pitch"));
							float volume = Float.parseFloat(Utils.pl.getConfig().getString("counting.3.volume"));
							for (Sound s1 : Sound.values()) {
								if (s1.name().equals(Utils.pl.getConfig().getString("counting.3.sound"))) {
									s = s1;
								}
							}
							p.playSound(p.getLocation(), s, volume, pitch);
						}
						p.sendTitle(Utils.colors(Utils.getTextByLangFile(p, "invasion.count.3")), Utils.colors(Utils.getTextByLangFile(p, "invasion.count.3d")));
					}
					Thread.sleep(1000);
					for (Player p : players) {
						if (Utils.pl.getConfig().getBoolean("counting.enabled")) {
							Sound s = null;
							float pitch = Float.parseFloat(Utils.pl.getConfig().getString("counting.2.pitch"));
							float volume = Float.parseFloat(Utils.pl.getConfig().getString("counting.2.volume"));
							for (Sound s1 : Sound.values()) {
								if (s1.name().equals(Utils.pl.getConfig().getString("counting.2.sound"))) {
									s = s1;
								}
							}
							p.playSound(p.getLocation(), s, volume, pitch);
						}
						p.sendTitle(Utils.colors(Utils.getTextByLangFile(p, "invasion.count.2")), Utils.colors(Utils.getTextByLangFile(p, "invasion.count.2d")));
					}
					Thread.sleep(1000);
					for (Player p : players) {
						if (Utils.pl.getConfig().getBoolean("counting.enabled")) {
							Sound s = null;
							float pitch = Float.parseFloat(Utils.pl.getConfig().getString("counting.1.pitch"));
							float volume = Float.parseFloat(Utils.pl.getConfig().getString("counting.1.volume"));
							for (Sound s1 : Sound.values()) {
								if (s1.name().equals(Utils.pl.getConfig().getString("counting.1.sound"))) {
									s = s1;
								}
							}
							p.playSound(p.getLocation(), s, volume, pitch);
						}
						p.sendTitle(Utils.colors(Utils.getTextByLangFile(p, "invasion.count.1")), Utils.colors(Utils.getTextByLangFile(p, "invasion.count.1d")));
					}
					Thread.sleep(1000);
					for (Player p : players) {
						if (Utils.pl.getConfig().getBoolean("counting.enabled")) {
							Sound s = null;
							float pitch = Float.parseFloat(Utils.pl.getConfig().getString("counting.start.pitch"));
							float volume = Float.parseFloat(Utils.pl.getConfig().getString("counting.start.volume"));
							for (Sound s1 : Sound.values()) {
								if (s1.name().equals(Utils.pl.getConfig().getString("counting.start.sound"))) {
									s = s1;
								}
							}
							p.playSound(p.getLocation(), s, volume, pitch);
						}
						p.sendTitle(Utils.colors(Utils.getTextByLangFile(p, "invasion.count.start")), Utils.colors(Utils.getTextByLangFile(p, "invasion.count.startd")));
					}
					this.waves.get(this.atWave - 1).start();
				} catch(Exception e) {}
			}).start();
		} else {
			for (Player p : this.players) {
				p.sendMessage(Utils.colors(Utils.getTextByLangFile(p, "invasion.stopped")));
			}
			Main.invasions.remove(this);
			this.bb.removeAll();
		}
	}
	
	public void updateProgress(int count, int max) {
		this.updateProgress(count, max, null);
	}
	
	public void updateProgress(int count, int max, String s) {
		if (s != null) {
			s = " - " + s;
		} else {
			s = "";
		}
		this.bb.setTitle("Invasion - Wave " + this.atWave + "/" + this.waves.size() + " - Goal: " + count + "/" + max + s);
		float c = (float) count;
		float m = (float) max;
		this.bb.setProgress((1.0 - (float) (c / m)));
	}
	
	public List<Player> getPlayers() {
		return this.bb.getPlayers();
	}
	
	public boolean isStopped() {
		return this.didStop;
	}
	
	public void stop() {
		this.stop(true);
	}
	
	public void stop(boolean remove) {
		this.didStop = true;
		for (int i=0;i<this.startedWaves.size();i++) {
			this.startedWaves.set(i, false);
			this.stoppedWaves.set(i, true);
			this.waves.get(i).killAllEnemies();
		}
		for (Player p : this.players) {
			p.sendMessage(Utils.colors(Utils.getTextByLangFile(p, "invasion.stopped")));
		}
		if (remove) {
			Main.invasions.remove(this);
		}
		this.bb.removeAll();
	}
	
	public void stopWave(int w) {
		this.stoppedWaves.set(w-1, true);
	}
	
	public boolean isWaveStopped(int w) {
		return this.stoppedWaves.get(w-1);
	}
	
	public void startWave(int w) {
		this.startedWaves.set(w-1, true);
	}
	
	public boolean isWaveStarted(int w) {
		return this.startedWaves.get(w-1);
	}

}
