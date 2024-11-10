package codes.snowy.dupeJS;

import co.aikar.commands.PaperCommandManager;
import codes.snowy.dupeJS.session.SessionListener;
import codes.snowy.dupeJS.staff.chat.StaffChatCommand;
import codes.snowy.dupeJS.staff.chat.StaffChatListener;
import codes.snowy.dupeJS.staff.chat.StaffChatManager;
import codes.snowy.dupeJS.staff.vanish.VanishListener;
import codes.snowy.dupeJS.staff.vanish.VanishManager;
import codes.snowy.dupeJS.utils.CommandCompletions;
import codes.snowy.dupeJS.bundles.AdminBundleCommand;
import codes.snowy.dupeJS.bundles.BundleListener;
import codes.snowy.dupeJS.bundles.BundleManager;
import codes.snowy.dupeJS.bundles.PreviewBundleCommand;
import codes.snowy.dupeJS.crushplus.CrushPlusManager;
import codes.snowy.dupeJS.crushplus.FlightCommand;
import codes.snowy.dupeJS.crushplus.FlightRestrictionListener;
import codes.snowy.dupeJS.dupe.DupeBlacklistCommand;
import codes.snowy.dupeJS.dupe.DupeCommand;
import codes.snowy.dupeJS.dupe.DupeManager;
import codes.snowy.dupeJS.homes.HomeCommand;
import codes.snowy.dupeJS.homes.HomeListener;
import codes.snowy.dupeJS.homes.HomeManager;
import codes.snowy.dupeJS.lifesteal.*;
import codes.snowy.dupeJS.teleporter.TeleportManager;
import codes.snowy.dupeJS.utils.Config;
import codes.snowy.dupeJS.utils.Language;
import codes.snowy.dupeJS.utils.Logger;
import codes.snowy.dupeJS.staff.vanish.VanishCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public final class DupeJS extends JavaPlugin {

    DupeManager dupeManager;
    LifestealManager lifestealmanager;
    CrushPlusManager crushPlusManager;
    HomeManager homeManager;
    TeleportManager teleportManager;
    BundleManager bundleManager;
    StaffChatManager staffChatManager;
    VanishManager vanishManager;
    private Config config;
    private Language language;
    private static DupeJS instance;

    public static DupeJS getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {


        DupeJS.instance = this;

        loadConfig();
        homeManager = new HomeManager();
        teleportManager = new TeleportManager(this);
        dupeManager = new DupeManager();
        lifestealmanager = new LifestealManager();
        crushPlusManager = new CrushPlusManager(this);

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("help");

        CommandCompletions.INSTANCE.register(manager);
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
        manager.registerCommand(new HomeCommand(homeManager, teleportManager, manager));
        Logger.INSTANCE.log("Loaded the Home Command", "success");
        manager.registerCommand(new AdminBundleCommand(config));
        Logger.INSTANCE.log("Loaded the AdminBundle Command", "success");
        manager.registerCommand(new PreviewBundleCommand());
        Logger.INSTANCE.log("Loaded the PreviewBundle Command", "success");
        manager.registerCommand(new StaffChatCommand());
        Logger.INSTANCE.log("Loaded the StaffChat Command", "success");
        manager.registerCommand(new VanishCommand());
        Logger.INSTANCE.log("Loaded the Vanish Command", "success");

        getServer().getPluginManager().registerEvents(new LifestealListener(lifestealmanager, dupeManager), this);
        Logger.INSTANCE.log("Loaded the Lifesteal Listener", "success");

        getServer().getPluginManager().registerEvents(new FlightRestrictionListener(crushPlusManager), this);
        Logger.INSTANCE.log("Loaded the Flight Restriction Listener", "success");

        getServer().getPluginManager().registerEvents(new HomeListener(homeManager, teleportManager), this);
        Logger.INSTANCE.log("Loaded the Home Listener", "success");

        getServer().getPluginManager().registerEvents(new SessionListener(), this);
        Logger.INSTANCE.log("Loaded the Session Listener", "success");

        getServer().getPluginManager().registerEvents(new BundleListener(dupeManager), this);
        Logger.INSTANCE.log("Loaded the Bundle Listener", "success");

        getServer().getPluginManager().registerEvents(new StaffChatListener(), this);
        Logger.INSTANCE.log("Loaded the StaffChat Listener", "success");

        getServer().getPluginManager().registerEvents(new VanishListener(this), this);
        Logger.INSTANCE.log("Loaded the Vanish Listener", "success");



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

        try {
            Logger.INSTANCE.log("Loading plugin language file.", "info");
            File configFiles = new File(getDataFolder(), "language.yml");
            if (!configFiles.exists()) {
                saveResource("language.yml", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.INSTANCE.log("Failed to load language file on start-up", "error");
            Logger.INSTANCE.log("Auto disabling DupeJS... contact @snowyjs", "error");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Language lang = new Language(this, config);

    }
}
