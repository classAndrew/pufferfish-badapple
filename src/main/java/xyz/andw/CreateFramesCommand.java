
package xyz.andw;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * I give up trying to get Java h.264 decoder libs to work
 */

public class CreateFramesCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        File dir = new File("output/");
        dir.mkdir();
        // clean dir
        Arrays.stream(dir.listFiles()).forEach((File f) -> f.delete());

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process p;
                    try {
                        p = Runtime.getRuntime().exec("ffmpeg -i badapple.mp4 -r 10 output/output_%05d.png");
                        InputStream os = p.getErrorStream();
                        byte b[] = os.readAllBytes();
                        System.out.println(new String(b, StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).run();
        } catch (Exception e) { e.printStackTrace(); return false; }

        return true;
    }
}
