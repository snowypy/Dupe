package codes.snowy.dupeJS;

import co.aikar.commands.PaperCommandManager;
import codes.snowy.dupeJS.crushplus.CrushPlusManager;
import codes.snowy.dupeJS.crushplus.FlightCommand;
import codes.snowy.dupeJS.crushplus.FlightRestrictionListener;
import codes.snowy.dupeJS.dupe.DupeBlacklistCommand;
import codes.snowy.dupeJS.dupe.DupeCommand;
import codes.snowy.dupeJS.dupe.DupeManager;
import codes.snowy.dupeJS.lifesteal.*;
import codes.snowy.dupeJS.utils.Config;
import codes.snowy.dupeJS.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class DupeJS extends JavaPlugin {

    DupeManager dupeManager;
    LifestealManager lifestealmanager;
    CrushPlusManager crushPlusManager;
    private Config config;
    private static DupeJS instance;

    public static DupeJS getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {


        DupeJS.instance = this;

        loadConfig();
        dupeManager = new DupeManager();
        lifestealmanager = new LifestealManager();
        crushPlusManager = new CrushPlusManager(this);
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("help");
        manager.registerCommand(new DupeCommand(dupeManager));
        Logger.INSTANCE.log("Loaded the Dupe Command", "success");
        manager.registerCommand(new DupeBlacklistCommand(dupeManager));
        Logger.INSTANCE.log("Loaded the DupeBlacklist Command", "success");
        manager.registerCommand(new LSAdminCommand(lifestealmanager));
        Logger.INSTANCE.log("Loaded the Lifesteal Admin Command", "success");
        manager.registerCommand(new PayHeartsCommand(lifestealmanager));
        Logger.INSTANCE.log("Loaded the PayHearts Command", "success");
        manager.registerCommand(new WithdrawCommand(lifestealmanager));
        Logger.INSTANCE.log("Loaded the Withdraw Command", "success");
        manager.registerCommand(new FlightCommand(crushPlusManager));
        Logger.INSTANCE.log("Loaded the Flight Command", "success");

        getServer().getPluginManager().registerEvents(new LifestealListener(lifestealmanager, dupeManager), this);
        Logger.INSTANCE.log("Loaded the Lifesteal Listener", "success");

        getServer().getPluginManager().registerEvents(new FlightRestrictionListener(crushPlusManager), this);
        Logger.INSTANCE.log("Loaded the Flight Restriction Listener", "success");


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadConfig() {
        try {
            Logger.INSTANCE.log("Loading the configuration file", "info");
            File configFiles = new File(getDataFolder(), "configuration.yml");
            if (!configFiles.exists()) {
                saveResource("configuration.yml", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("&cFailed to load configuration file in start up");
            getLogger().severe("&cAuto disabling WatchTower... make a ticket in discord");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.config = new Config(this);
        Logger.INSTANCE.log("The configuration has been loaded", "success");
    }
}
