package net.insomniacraft.codeex.InsomniaDOTA;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.insomniacraft.codeex.InsomniaDOTA.structures.turrets.IDTurretManager;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeamManager;
import net.insomniacraft.codeex.InsomniaDOTA.IDGameManager;

public class InsomniaDOTA extends JavaPlugin {
	
	public static Logger l = Logger.getLogger("Minecraft");
	public static Server s;
	public static Plugin pl;
	public static File pFolder = new File("plugins/InsomniaDOTA");
	public static File proFile = new File(pFolder, "DOTA.properties");
	public static Properties properties;
	public static String strWorld;

	public void onEnable() {
		boolean abort = false;
		pl = this;
		s = getServer();
		//Set listener
		IDListener listener = new IDListener(this);
		getServer().getPluginManager().registerEvents(listener, this);
		//Set commands
		IDCommands commands = new IDCommands(this);
		getCommand("setup").setExecutor(commands);
		getCommand("set").setExecutor(commands);
		getCommand("clear").setExecutor(commands);
		getCommand("rdy").setExecutor(commands);
		getCommand("join").setExecutor(commands);
		getCommand("reset").setExecutor(commands);
		getCommand("info").setExecutor(commands);
		getCommand("all").setExecutor(commands);
		getCommand("teamswitch").setExecutor(commands);
		getCommand("b").setExecutor(commands);		
		getCommand("fb").setExecutor(commands);
		getCommand("setturretblock").setExecutor(commands);
		getCommand("start").setExecutor(commands);
		getCommand("spawnzombie").setExecutor(commands);
		getCommand("moveto").setExecutor(commands);
		getCommand("spawnwave").setExecutor(commands);
		getCommand("setlevel").setExecutor(commands);
		
		//Make plugin folder
		if (!pFolder.exists()) {
			pFolder.mkdir();
		}
		//Load properties
		properties = new Properties();
		//If doesnt exist use defaults
		try {
			FileInputStream fis = new FileInputStream(proFile);
			properties.load(fis);
		} catch (FileNotFoundException e) {
			l.warning("Could not find properties file, generating new one...");
			properties = getDefaultProperties();
		} catch (IOException e) {
			l.warning("Error loading properties file, generating new one...");
			properties = getDefaultProperties();
		}
		//Set gamestarted boolean
		String strgs = properties.getProperty("gamestarted");
		IDGameManager.gameStarted = Boolean.parseBoolean(strgs);
		System.out.println("Game started="+IDGameManager.gameStarted);
		//Set world variable
		strWorld = properties.getProperty("world");
		System.out.println("STRWORLD IS "+strWorld);
		//Check world
		World w;
		try {
			w = getServer().getWorld(strWorld);
		} catch (Exception e) {
			w = null;
		}
		if (w == null) {
			l.severe("World specified in properties file does not exist!");
			abort = true;
		}
		try {
			//Load teams
			IDTeamManager.load();
			//Load turrets
			IDTurretManager.load();
			//Load nexus
			IDGameManager.load();
		} catch (Exception e) {
			l.severe("Error loading files!");
			e.printStackTrace();
		}
		if (abort) {
			getServer().getPluginManager().disablePlugin(this);
		}
	}
	
	public void onDisable() {
		//Set everyone to not recalling
		for (Player p: getServer().getOnlinePlayers()) {
			IDTeamManager.setRecalling(p, false);
		}
		//Update properties file
		properties.setProperty("gamestarted", Boolean.toString(IDGameManager.gameStarted));
		//Save properties file
		if (!proFile.exists()) {
			try {
				proFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(proFile);
			properties.store(fos, "InsomniaDOTA Properties");
		} catch (FileNotFoundException e) {
			l.severe("Could not access properties file to save!");
			e.printStackTrace();
		} catch (IOException e) {
			l.severe("Error saving properties file!");
			e.printStackTrace();
		}
		//Save teams
		try {
			IDTeamManager.save();
		} catch (Exception e) {
			l.severe("Error saving teams!");
			e.printStackTrace();
		}
		//Save turrets
		try {
			IDTurretManager.save();
		} catch (Exception e) {
			l.severe("Error saving turrets!");
			e.printStackTrace();
		}
		//Save nexus
		try {
			IDGameManager.save();
		} catch (Exception e) {
			l.severe("Error saving nexus!");
			e.printStackTrace();
		}
	}
	
	public Player findPlayer(String spl) {
		Player[] players = getServer().getOnlinePlayers();
		for (Player p: players) {
			if (p.getName().equalsIgnoreCase(spl)) {
				return p;
			}
		}
		return null;
	}
	
	public static void broadcast(String str) {
		s.broadcastMessage(str);
	}
	
	private Properties getDefaultProperties() {
		Properties def = new Properties();
		def.setProperty("gamestarted", "false");
		def.setProperty("world", "dota");
		return def;
	}
}
