
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
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * I give up trying to get Java h.264 decoder libs to work
 */

public class PlayFramesCommand implements CommandExecutor {
    private Plugin plugin;

    public PlayFramesCommand(Plugin pl) { this.plugin = pl; }

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
        int zOffset = 5;
        int yOffset = 5;

        for (int frameIdx = 0; frameIdx < framebuffer.length; frameIdx++) {
            int[] frame = framebuffer[frameIdx+20];
            
            Bukkit.getScheduler().runTaskLater(this.plugin, () ->  {
                BuildFramesCommand.buildFrameWorld(world, frame, width, pX, pY+yOffset, pZ+zOffset);
            }, 2*frameIdx+10);
            // separate with a line
            for (int col = 0; col < width; col++)
                world.getBlockAt(pX+col, pY+yOffset+height, pZ+zOffset).setType(Material.OBSIDIAN);
        }

        return true;
    }
}
