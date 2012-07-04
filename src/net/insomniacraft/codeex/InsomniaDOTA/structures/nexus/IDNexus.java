package net.insomniacraft.codeex.InsomniaDOTA.structures.nexus;

import java.util.ArrayList;

import org.bukkit.block.Block;

import net.insomniacraft.codeex.InsomniaDOTA.IDGameManager;
import net.insomniacraft.codeex.InsomniaDOTA.InsomniaDOTA;
import net.insomniacraft.codeex.InsomniaDOTA.structures.*;
import net.insomniacraft.codeex.InsomniaDOTA.teams.IDTeam.Colour;

public class IDNexus extends IDStructure {

	private boolean isDestroyed;
	private Colour col;

	public IDNexus(ArrayList<Block> blocks, int health, Colour col) {
		super(blocks, health);
		isDestroyed = false;
		this.col = col;
	}

	public void doDamage() {
		if (health > 0) {
			super.doDamage();
		}
		if (health == 0) {
			isDestroyed = true;
			ArrayList<Block> wool = IDGameManager.searchForBlock(coreBlocks.get(0).getLocation(), 35, 20);
			for (Block b: coreBlocks) {
				b.getWorld().createExplosion(b.getLocation(), 0.0F);
			}
			for (Block b: wool) {
				b.setTypeIdAndData(35, Byte.valueOf("15"), true);
				b.getWorld().createExplosion(b.getLocation(), 0.0F);
			}
			Colour winner = Colour.NEUTRAL;
			if (col.toString().equals("BLUE")) {
				winner = Colour.RED;
			}
			else if (col.toString().equals("RED")) {
				winner = Colour.BLUE;
			}
			InsomniaDOTA.broadcast("Team "+winner.toString()+" has won the game!");
			InsomniaDOTA.broadcast("The game is ending in 60 seconds!");
			InsomniaDOTA.s.getScheduler().scheduleAsyncDelayedTask(InsomniaDOTA.pl, new Runnable() {
				public void run() {
					for (int i = 60; i >= 0; i--) {
						if (i == 30) {
							InsomniaDOTA.broadcast("The game is ending in 30 seconds!");
						}
						if (i == 10) {
							InsomniaDOTA.broadcast("The game is ending in 10 seconds!");
						}
						if (i == 1) {
							InsomniaDOTA.broadcast("The game is now ending...");
						}
						try {
							Thread.sleep(1000);
						}
						catch (InterruptedException e) {}
					}
					IDGameManager.endGame();
				}
			}
			, 1L);
		}
	}

	public boolean getDestroyed() {
		return isDestroyed;
	}

	public Colour getColour() {
		return col;
	}
	
	public void reset() {
		isDestroyed = false;
		setHealth(IDGameManager.getNexusDefHealth());
	}
}
