package xyz.andw;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import javax.imageio.ImageIO;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildFramesCommand implements CommandExecutor {

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
            framebuffer = loadFrames(frameDir, width, height);
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

        for (int frameIdx = 0; frameIdx < 15; frameIdx++) {
            int[] frame = framebuffer[frameIdx+90];
            
            buildFrameWorld(world, frame, width, pX, pY+yOffset+frameIdx*height, pZ+zOffset);
            // separate with a line
            for (int col = 0; col < width; col++)
                world.getBlockAt(pX+col, pY+yOffset+frameIdx*(height+1), pZ+zOffset).setType(Material.OBSIDIAN);
        }
        
        return true;
    }

    public static void buildFrameWorld(World world, int[] frame, int width, int x, int y, int z) {
        for (int i = 0; i < frame.length; i++) {
            int row = i / width;
            int col = i % width;

            int r = (frame[i] >> 16) & 0xFF; // alpha channel should be 0 anyways
            int g = (frame[i] >> 8) & 0xFF;
            int b = frame[i] & 0xFF;

            int grayscale = (r+g+b) / 3;
            Material replacement = grayscale >= 127? Material.WHITE_WOOL : Material.BLACK_WOOL; 
            world.getBlockAt(x+col, y+row, z).setType(replacement);
        }
    }

    public static int[][] loadFrames(File frameDir, int width, int height) throws IOException {
        int[][] framebuffer;

        BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gfx = im.createGraphics();
        File[] imgs = frameDir.listFiles();
        
        Arrays.sort(imgs, (File a, File b) -> a.getName().compareTo(b.getName()));
        
        framebuffer = new int[imgs.length][width*height];
        for (int i = 0; i < imgs.length; i++) {
            File f = imgs[i];
            BufferedImage imOriginal;

            try { imOriginal = ImageIO.read(f); } catch (Exception e) { gfx.dispose(); throw e; }

            Image imScaled = imOriginal.getScaledInstance(width, height, Image.SCALE_DEFAULT);
            gfx.drawImage(imScaled, 0, 0, null);
            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    framebuffer[i][y*width+x] = im.getRGB(x, y);
                }
            }
        }

        gfx.dispose(); 

        return framebuffer;
    }
}
