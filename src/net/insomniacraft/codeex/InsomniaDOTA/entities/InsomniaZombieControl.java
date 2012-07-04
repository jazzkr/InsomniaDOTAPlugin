package net.insomniacraft.codeex.InsomniaDOTA.entities;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

import net.minecraft.server.Navigation;

public class InsomniaZombieControl {
	public CraftZombie z;
	private boolean set = false;
	private Navigation nav;
	
	public void spawnZombie(Location l) {
		LivingEntity e = l.getWorld().spawnCreature(l, EntityType.ZOMBIE);
		if (e instanceof Zombie) {
			Zombie z = (Zombie) e;
			CraftZombie cz = (CraftZombie) z;
			System.out.println("Setting zombie!");
			setZombie(cz);
		}
	}
	
	public void setZombie(CraftZombie e) {
		this.z = e;
		nav = z.getHandle().al();
		set = true;
		System.out.println("Zombie navigation set up!");
	}
	
	public void moveTo(Location l) {
		if (set) {
			nav.a(l.getX(), l.getY(), l.getZ(), 0.3F);
		} else {
			System.out.println("Zombie not set!");
		}
	}
}
