
package xyz.andw;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BadApplePlugin extends JavaPlugin {

    @Override
    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
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
