package net.insomniacraft.codeex.InsomniaDOTA.structures.turrets;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import net.insomniacraft.codeex.InsomniaDOTA.IDGameManager;
import net.insomniacraft.codeex.InsomniaDOTA.InsomniaDOTA;
import net.insomniacraft.codeex.InsomniaDOTA.structures.IDStructure;
import net.insomniacraft.codeex.InsomniaDOTA.structures.nexus.IDNexus;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeamManager;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam.Colour;

public class IDTurret extends IDStructure {

	public enum Turret {TOP_OUTER, TOP_INNER, MID_OUTER, MID_INNER, BOT_OUTER, BOT_INNER};
	
	private Block turretBlock;
	private boolean isDead;
	private Turret id;
	private Colour c;
	private Location waveSpawn;
	private ArrayList<UUID> wave;

	//Regular constructor
	public IDTurret(ArrayList<Block> blocks, int health, Turret id, Colour col) {
		super (blocks, health);
		turretBlock = blocks.get(0);
		isDead = false;
		this.id = id;
		this.c = col;
		wave = new ArrayList<UUID>();
		if (c == Colour.RED) {
			waveSpawn = IDTurretManager.findGround(new Location (turretBlock.getWorld(), turretBlock.getX(), turretBlock.getY(), (turretBlock.getZ()-6)));
		} else {
			waveSpawn = IDTurretManager.findGround(new Location (turretBlock.getWorld(), turretBlock.getX(), turretBlock.getY(), (turretBlock.getZ()+6)));
		}
	}
	
	//Alternate constructor to create turret from IDTurretParams object
	public IDTurret(IDTurretParams itp) {
		super (itp.blocks, itp.hp);
		turretBlock = itp.turretBlock;
		isDead = false;
		this.id = itp.id;
		this.c = itp.col;
		wave = new ArrayList<UUID>();
		if (c == Colour.RED) {
			waveSpawn = IDTurretManager.findGround(new Location (turretBlock.getWorld(), turretBlock.getX(), turretBlock.getY(), (turretBlock.getZ()-6)));
		} else {
			waveSpawn = IDTurretManager.findGround(new Location (turretBlock.getWorld(), turretBlock.getX(), turretBlock.getY(), (turretBlock.getZ()+6)));
		}
	}
	
	public Block getTurretBlock() {
		return turretBlock;
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	public Turret getId() {
		return id;
	}
	
	public Colour getTeam() {
		return c;
	}
	
	public void doDamage() {
		if (isDead) {
			return;
		}
		if (health >= 0) {
			super.doDamage();
		}
		if (health == 0) {
			//Destroy turret
			isDead = true;
			//Set wool blocks to black
			ArrayList<Block> woolBlocks = IDGameManager.searchForBlock(turretBlock.getLocation(), 35, 10);
			for (Block b: woolBlocks) {
				b.setTypeIdAndData(35, Byte.valueOf("15"), true);
			}
			turretBlock.getWorld().createExplosion(turretBlock.getLocation(), 0.0F);
		}
	}
	
	public void reset() {
		isDead = false;
		setHealth(IDTurretManager.getDefaultHealth());
	}
	
	public void spawnWave() {
		if (isDead || !IDGameManager.gameStarted) {
			return;
		}
		final IDTurret t = this;
		InsomniaDOTA.s.getScheduler().scheduleAsyncDelayedTask(InsomniaDOTA.pl, new Runnable() {
			public void run() {
				IDStructure struct = IDTurretManager.getOpposingStructure(t.getId(), t.getTeam());
				Location dest;
				//Get destination
				if(struct instanceof IDTurret) {
					dest = ((IDTurret)struct).getWaveSpawn();
				} else if (struct instanceof IDNexus) {
					dest = IDTeamManager.getSpawn(((IDNexus)struct).getColour());
				} else {
					dest = null;
				}
				for (int i = 6; i >= 0; i--) {
					//Spawn zombie
					Entity e = t.getTurretBlock().getWorld().spawnCreature(t.waveSpawn, EntityType.ZOMBIE);
					System.out.println("Spawned zombie "+e.getUniqueId()+" at "+e.getLocation().getX()+" "+e.getLocation().getY()+" "+e.getLocation().getZ());
					//Give destination through navigation object
					if (dest != null) {
						((CraftZombie) e).getHandle().al().a(dest.getX(), dest.getY(), dest.getZ(), 0.2F);
						System.out.println("Gave destination to "+e.getUniqueId()+"!");
					}
					//Add to tower's ownership list
					wave.add(e.getUniqueId());
					//Wait 1 second before repeating
					try {
						Thread.sleep(1000);
					}
					catch (InterruptedException ex) {}
				}
			}
		}
		, 1L);
	}
	
	public ArrayList<UUID> getWave() {
		return wave;
	}
	
	public Location getWaveSpawn() {
		return waveSpawn;
	}
	
	public static Turret getIdFromStr(String s) {
		if (s.equalsIgnoreCase("TOP_OUTER")) {
			return Turret.TOP_OUTER;
		} else if (s.equalsIgnoreCase("TOP_INNER")) {
			return Turret.TOP_INNER;
		} else if (s.equalsIgnoreCase("MID_OUTER")) {
			return Turret.MID_OUTER;
		} else if (s.equalsIgnoreCase("MID_INNER")) {
			return Turret.MID_INNER;
		} else if (s.equalsIgnoreCase("BOT_OUTER")) {
			return Turret.BOT_OUTER;
		} else if (s.equalsIgnoreCase("BOT_INNER")) {
			return Turret.BOT_INNER;
		} else {
			return null;
		}
	}
}
