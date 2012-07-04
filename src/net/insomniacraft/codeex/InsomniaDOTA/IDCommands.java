package net.insomniacraft.codeex.InsomniaDOTA;

import java.util.ArrayList;
import java.util.Random;

import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeamManager;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam.Colour;
import net.insomniacraft.codeex.InsomniaDOTA.chat.IDChatManager;
import net.insomniacraft.codeex.InsomniaDOTA.entities.InsomniaZombieControl;
import net.insomniacraft.codeex.InsomniaDOTA.structures.turrets.*;
import net.insomniacraft.codeex.InsomniaDOTA.structures.turrets.IDTurret.Turret;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class IDCommands implements CommandExecutor {

	public static boolean setup = false;
	public static String setupPlayer = null;
	
	public static InsomniaZombieControl izc = new InsomniaZombieControl();
	
	Plugin p;

	public IDCommands(Plugin p) {
		this.p = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		// Player Commands
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			// Player commands
			// DOTA PLAY PERMISSIONS
			if (sender.hasPermission("DOTA.play")) {

				// JOIN COMMAND
				if (cmd.getName().equalsIgnoreCase("join")) {
					Colour c = IDTeamManager.getTeam(player);
					if ((c.toString().equals("RED"))
							|| (c.toString().equals("BLUE"))) {
						player.sendMessage("You are already on a team!");
						return true;
					}
					int blue = IDTeamManager.getBlueCount();
					int red = IDTeamManager.getRedCount();
					if (blue > red) {
						IDTeamManager.setTeam(Colour.RED, player);
						return true;
					} else if (red > blue) {
						IDTeamManager.setTeam(Colour.BLUE, player);
						return true;
					} else {
						Random r = new Random();
						if (r.nextBoolean()) {
							IDTeamManager.setTeam(Colour.BLUE, player);
						} else {
							IDTeamManager.setTeam(Colour.RED, player);
						}
						return true;
					}
				}
				// JOIN COMMAND END

				// READY COMMAND
				if (cmd.getName().equalsIgnoreCase("rdy")) {
					if (!player.getWorld().getName().equals(InsomniaDOTA.strWorld)) {
						player.sendMessage("This command is for the DOTA world!");
						return true;
					}
					Colour c = IDTeamManager.getTeam(player);
					if (c.toString().equals("NEUTRAL")) {
						player.sendMessage("You are not on a team!");
						return true;
					}
					if (IDGameManager.isStarted()) {
						player.sendMessage("A game is already in progress.");
						return true;
					}
					if (IDTeamManager.isPlayerReady(player)) {
						IDTeamManager.removeReady(c, player);
						return true;
					} else {
						IDTeamManager.addReady(c, player);
						if (IDTeamManager.isPlayerReady(player)) {
							player.sendMessage("You are now ready.");
						} else {
							System.out
									.println("[DEBUG] Player was not set to ready.");
						}
						return true;
					}
				}
				// RDY COMMAND END
				
				// START COMMAND
				if (cmd.getName().equalsIgnoreCase("start")) {
					if (!player.getWorld().getName().equals(InsomniaDOTA.strWorld)) {
						player.sendMessage("This command is for the DOTA world!");
						return true;
					}
					IDGameManager.startGame(player);
					return true;
				}
				// START COMMAND END

				// RECALL COMMAND
				if (cmd.getName().equalsIgnoreCase("b")) {
					if (!player.getWorld().getName().equals(InsomniaDOTA.strWorld)) {
						player.sendMessage("This command is for the DOTA world!");
						return true;
					}
					Colour col = IDTeamManager.getTeam((Player) sender);
					final Location l = IDTeamManager.getSpawn(col);
					final Player pl = (Player) sender;
					if (l == null) {
						pl.sendMessage("Your team does not have a spawn set!");
						return true;
					}
					pl.sendMessage("Recalling...");
					IDTeamManager.setRecalling(pl, true);
					Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(p, new Runnable() {
						public void run() {
							//Give full bar
							float expCount = 1;
							pl.setExp(expCount);
							while (IDTeamManager.isRecalling(pl)) {
								if (expCount > 0) {
									expCount = expCount - 0.001F;
								} else {
									expCount = 0;
								}
								pl.setExp(expCount);
								try {
									Thread.sleep(6);
								} catch (InterruptedException e) { }
								if (expCount == 0) {
									pl.teleport(l);
									System.out.println("Recalled "+pl.getName()+" to spawn!");
									break;
								}
							}
							if (expCount != 0) {
								pl.sendMessage("Recall interrupted!");
							}
							pl.setExp(0);
						}
					});
					return true;
				}
				// RECALL COMMAND END
				
				//ALL CHAT COMMAND
				if (cmd.getName().equalsIgnoreCase("all")) {
					if (!player.getWorld().getName().equals(InsomniaDOTA.strWorld)) {
						player.sendMessage("This command is for the DOTA world!");
						return true;
					}
					String msg = "";
					for (int i = 0; i < args.length; i++) {
						if (i == 0) {
							msg = args[0];
						} else {
							msg = msg + " " + args[i];
						}
					}
					IDChatManager.addToAllChat(player);
					player.chat(msg);
					return true;
				}
				//ALL CHAT COMMAND END
			}
			// DOTA PLAY PERMISSIONS END
		}
		// Moderator commands
		// DOTA MOD PERMISSIONS
		if (sender.hasPermission("DOTA.mod")) {

			// REMOVE COMMAND
			if (cmd.getName().equalsIgnoreCase("remove")) {
				if (!((Player)sender).getWorld().getName().equals(InsomniaDOTA.strWorld)) {
					((Player)sender).sendMessage("This command is for the DOTA world!");
					return true;
				}
				if (!(args.length == 1)) {
					return false;
				}
				Player pl = p.getServer().getPlayer(args[0]);
				IDTeamManager.setTeam(Colour.NEUTRAL, pl);
			}
			// REMOVE COMMAND END

			// ADD COMMAND
			if (cmd.getName().equalsIgnoreCase("add")) {
				if (!((Player)sender).getWorld().getName().equals(InsomniaDOTA.strWorld)) {
					((Player)sender).sendMessage("This command is for the DOTA world!");
					return true;
				}
				if (!(args.length == 2)) {
					return false;
				}
				Player pl = ((InsomniaDOTA) p).findPlayer(args[0]);
				if (pl == null) {
					sender.sendMessage("That player does not exist!");
					return true;
				}
				if (args[1].equalsIgnoreCase("blue")) {
					IDTeamManager.setTeam(Colour.BLUE, pl);
					return true;
				} else if (args[1].equalsIgnoreCase("red")) {
					IDTeamManager.setTeam(Colour.RED, pl);
					return true;
				} else if (args[1].equalsIgnoreCase("neutral")) {
					IDTeamManager.setTeam(Colour.NEUTRAL, pl);
					return true;
				} else {
					sender.sendMessage("That team does not exist!");
					return true;
				}
			}
			// ADD COMMAND END

			// TEAMSWITCH COMMAND
			if (cmd.getName().equalsIgnoreCase("teamswitch")) {
				if (!((Player)sender).getWorld().getName().equals(InsomniaDOTA.strWorld)) {
					((Player)sender).sendMessage("This command is for the DOTA world!");
					return true;
				}
				if (!(args.length == 1)) {
					return false;
				}
				Player pl = ((InsomniaDOTA) p).findPlayer(args[0]);
				if (pl == null) {
					sender.sendMessage("That player does not exist!");
					return true;
				}
				Colour col = IDTeamManager.getTeam(pl);
				if (col.toString().equals("BLUE")) {
					IDTeamManager.setTeam(Colour.RED, pl);
					return true;
				} else if (col.toString().equals("RED")) {
					IDTeamManager.setTeam(Colour.BLUE, pl);
					return true;
				} else {
					sender.sendMessage("Player is not on a team.");
					return true;
				}
			}
			// TEAMSWITCH COMMAND END
		}
		// DOTA MOD PERMISSIONS END
		// Admin commands
		// DOTA ADMIN PERMISSIONS
		if (sender.hasPermission("DOTA.admin")) {
			// RESET COMMAND
			if (cmd.getName().equalsIgnoreCase("reset")) {
				if (!((Player)sender).getWorld().getName().equals(InsomniaDOTA.strWorld)) {
					((Player)sender).sendMessage("This command is for the DOTA world!");
					return true;
				}
				IDGameManager.endGame();
				sender.sendMessage("Game reset!");
				return true;
			}
			// RESET COMMAND END

			// SETUP COMMAND
			if (cmd.getName().equalsIgnoreCase("setup")) {
				if (sender instanceof ConsoleCommandSender) {
					setup = !setup;
					InsomniaDOTA.l.info("[DEBUG] Setup is now " + setup + ".");
					return true;
				}
				if (sender instanceof Player) {
					if (!((Player)sender).getWorld().getName().equals(InsomniaDOTA.strWorld)) {
						((Player)sender).sendMessage("This command is for the DOTA world!");
						return true;
					}
				}
				if (setup == false) {
					sender.sendMessage("Now in setup mode.");
					sender.sendMessage("Select blocks by clicking on them then");
					sender.sendMessage("Type /set [colour] [structure] to set a structure.");
					sender.sendMessage("To exit setup mode type /setup again.");
					setup = true;
					setupPlayer = ((Player) sender).getName();
					return true;
				} else if (setup == true) {
					if (setupPlayer.equalsIgnoreCase(((Player) sender)
							.getName())) {
						sender.sendMessage("Now exiting setup mode.");
						setup = false;
						IDBlockSelector.clearBlocks();
						return true;
					} else {
						sender.sendMessage("Only " + setupPlayer
								+ " or console can exit setup mode!");
						return true;
					}
				}
				return false;
			}
			// SETUP COMMAND END

			// INFO COMMAND
			if (cmd.getName().equalsIgnoreCase("info")) {
				if (!((Player)sender).getWorld().getName().equals(InsomniaDOTA.strWorld)) {
					((Player)sender).sendMessage("This command is for the DOTA world!");
					return true;
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("BLUE")) {
						for (IDTurret t : IDTurretManager.getBlueTurrets()) {
							if (t == null) {
								sender.sendMessage("Blue turret is null!");
							} else {
								sender.sendMessage(t.getTeam() + " "
										+ t.getId() + " HP:" + t.getHealth() + "isDead: "+t.isDead());
							}
						}
						return true;
					} else if (args[0].equalsIgnoreCase("RED")) {
						for (IDTurret t : IDTurretManager.getRedTurrets()) {
							if (t == null) {
								sender.sendMessage("Red turret is null!");
							} else {
								sender.sendMessage(t.getTeam() + " "
										+ t.getId() + " HP:" + t.getHealth() + "isDead: "+t.isDead());
							}
						}
						return true;
					}
					return false;
				} else if (args.length == 2) {
					return true;
				}
				return false;
			}
			// INFO COMMAND END

			// SET COMMAND
			if (cmd.getName().equalsIgnoreCase("set")) {
				if (!((Player)sender).getWorld().getName().equals(InsomniaDOTA.strWorld)) {
					((Player)sender).sendMessage("This command is for the DOTA world!");
					return true;
				}
				if (IDCommands.setup == false) {
					sender.sendMessage("Server is not in setup mode.");
					return true;
				}
				if (!(args.length == 2)) {
					return false;
				}
				// If turret, set turret
				if (args[1].equalsIgnoreCase("TOP_OUTER")
						|| args[1].equalsIgnoreCase("TOP_INNER")
						|| args[1].equalsIgnoreCase("MID_OUTER")
						|| args[1].equalsIgnoreCase("MID_INNER")
						|| args[1].equalsIgnoreCase("BOT_OUTER")
						|| args[1].equalsIgnoreCase("BOT_INNER")) {
					ArrayList<Block> bl = IDBlockSelector.getSelected();
					IDTurretParams itp;
					try {
						itp = new IDTurretParams(bl, args[0], args[1],
								IDTurretManager.getDefaultHealth());
					} catch (Exception e) {
						sender.sendMessage("Error: " + e.getMessage());
						return true;
					}
					if (itp != null) {
						IDTurretManager.setTurret(itp);
						sender.sendMessage("Turret set!");
					}
					IDBlockSelector.clearBlocks();
				}
				// If nexus, set nexus
				else if (args[1].equalsIgnoreCase("NEXUS")) {
					if (args[0].equalsIgnoreCase("RED")) {
						IDGameManager.setNexus(
								IDBlockSelector.getArraySelected(), Colour.RED,
								IDGameManager.getNexusDefHealth());
						sender.sendMessage("Red nexus set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else if (args[0].equalsIgnoreCase("BLUE")) {
						IDGameManager.setNexus(
								IDBlockSelector.getArraySelected(),
								Colour.BLUE, IDGameManager.getNexusDefHealth());
						sender.sendMessage("Blue nexus set!");
						IDBlockSelector.clearBlocks();
						return true;
					} else {
						return false;
					}
				}
				// If spawn, set spawn
				else if (args[1].equalsIgnoreCase("SPAWN")) {
					Location l = ((Player) sender).getLocation();
					if (args[0].equalsIgnoreCase("RED")) {
						IDTeamManager.setSpawn(Colour.RED, l);
						sender.sendMessage("Red spawn set!");						
						return true;
					} else if (args[0].equalsIgnoreCase("BLUE")) {
						IDTeamManager.setSpawn(Colour.BLUE, l);
						sender.sendMessage("Blue spawn set!");
						return true;
					} else {
						sender.sendMessage("Not a valid colour!");
						return true;
					}
				}
				// If none of the above...
				else {
					return false;
				}

			}
			// SET COMMAND END

			// CLEAR COMMAND
			if (cmd.getName().equalsIgnoreCase("clear")) {
				if (!((Player)sender).getWorld().getName().equals(InsomniaDOTA.strWorld)) {
					((Player)sender).sendMessage("This command is for the DOTA world!");
					return true;
				}
				if (setup == false) {
					sender.sendMessage("Server is not in setup mode.");
					return true;
				} else {
					IDBlockSelector.clearBlocks();
					sender.sendMessage("Blocks cleared.");
					return true;
				}
			}
			// CLEAR COMMAND END
			
			//SETTURRETBLOCK COMMAND
			if (cmd.getName().equalsIgnoreCase("setturretblock")) {
				if (!((Player)sender).getWorld().getName().equals(InsomniaDOTA.strWorld)) {
					((Player)sender).sendMessage("This command is for the DOTA world!");
					return true;
				}
				IDGameManager.setTurretBlock(Integer.parseInt(args[0]));
				return true;
			}
			//SETTURRETBLOCK COMMAND END
			
			//SPAWN ZOMBIE COMMAND
			if (cmd.getName().equalsIgnoreCase("spawnzombie")) {
				Location l = ((Player)sender).getTargetBlock(null, 100).getLocation();
				if (l != null) {
					izc.spawnZombie(l);
				}
				return true;
			}
			//SPAWN ZOMBIE COMMAND END
			
			//MOVETO COMMAND
			if (cmd.getName().equalsIgnoreCase("moveto")) {
				Location l = ((Player)sender).getTargetBlock(null, 100).getLocation();
				if (l != null) {
					izc.moveTo(l);
				}
				return true;
			}
			//MOVETO COMMAND END
			
			//SPAWNWAVE COMMAND
			if (cmd.getName().equalsIgnoreCase("spawnwave")) {
				if (args.length == 2) {
					Colour c = IDTeam.getColourFromStr(args[0]);
					Turret id = IDTurret.getIdFromStr(args[1]);
					if (c == null || id == null) {
						return false;
					}
					IDTurret t = IDTurretManager.getTurret(id, c);
					t.spawnWave();
					return true;
				} else {
					return false;
				}
			}
			//SPAWN WAVE COMMAND END
			
			//SETLEVEL COMMAND
			if (cmd.getName().equalsIgnoreCase("setlevel")) {
				if (args.length != 1) {
					return false;
				}
				float level = Float.parseFloat(args[0]);
				((Player)sender).setExp(level);
			}
		}
		// DOTA ADMIN PERMISSIONS END
		return true;
	}
}
