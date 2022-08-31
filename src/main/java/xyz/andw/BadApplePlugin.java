
package xyz.andw;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BadApplePlugin extends JavaPlugin {

    @Override
    public void onDisable() {
        getLogger().info("Goodbye world!");
    }

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();

        getCommand("createframes").setExecutor(new CreateFramesCommand());
        getCommand("buildframes").setExecutor(new BuildFramesCommand());
        getCommand("choreo").setExecutor(new ChoreoCommand(this));
        getCommand("playframes").setExecutor(new PlayFramesCommand(this));
    }
}
