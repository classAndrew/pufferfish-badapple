
package xyz.andw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.entity.PufferFish;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 * I give up trying to get Java h.264 decoder libs to work
 */

/**
 * Handler for the /pos sample command.
 * @author SpaceManiac
 */
public class ChoreoCommand implements CommandExecutor {
    private Plugin plugin;

    public ChoreoCommand(Plugin pl) { this.plugin = pl; }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) { sender.sendMessage("You must be a player to use this command"); return false; }
        Player p = (Player) sender;

        // get frames from directory
        File frameDir = new File("output");
        if (!frameDir.exists()) { p.sendMessage("Please run /createframes first."); return false; }

        int width = 32, height = 32;
        int[][] framebuffer;
        try {
            p.sendMessage(String.format("Building %d frames %d x %d", frameDir.list().length, width, height));
            framebuffer = BuildFramesCommand.loadFrames(frameDir, width, height);
        } catch (IOException e) {
            e.printStackTrace();
            p.sendMessage("Could not load frames");
            return false;
        }

        World world = p.getWorld();
        int pX = p.getLocation().getBlockX();
        int pY = p.getLocation().getBlockY();
        int pZ = p.getLocation().getBlockZ();
        int zOffset = 30;
        int yOffset = -height/2;
        int xOffset = -width/2;

        PufferFish[][] school = new PufferFish[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                Location loc = new Location(world, pX+xOffset+c, pY+yOffset+(height-r-1), pZ+zOffset);

                PufferFish fish = (PufferFish) world.spawnEntity(loc, EntityType.PUFFERFISH, false);
                fish.setGravity(false);
                fish.setMaximumAir(999999999);
                fish.setRemainingAir(999999999);
                fish.setAI(false);

                school[r][c] = fish;
            }
        }

        for (int frameIdx = 0; frameIdx < framebuffer.length; frameIdx++) {
            int[] frame = framebuffer[frameIdx];
            Bukkit.getScheduler().runTaskLater(this.plugin, () ->  {
                for (int i = 0; i < frame.length; i++) {
                    int row = i / width;
                    int col = i % width;
        
                    int r = (frame[i] >> 16) & 0xFF; // alpha channel should be 0 anyways
                    int g = (frame[i] >> 8) & 0xFF;
                    int b = frame[i] & 0xFF;
        
                    int grayscale = (r+g+b) / 3;
                    int puff = grayscale >= 127? 2 : 0; 

                    PufferFish fish = school[row][col];

                    // for some reason the fish gets un puffed and falls down
                    Location loc = new Location(world, pX+xOffset+col, pY+yOffset+(height-row-1), pZ+zOffset);
                    fish.teleport(loc);

                    // fish reinflates if it's already puffed. need to check its state
                    boolean needChange = (puff==2 && fish.getPuffState()==2) || (puff==0 && fish.getPuffState()==0);
                    if (!needChange) 
                        fish.setPuffState(puff);
                }
            }, 2*frameIdx+10);
        }

        return true;
    }
}
