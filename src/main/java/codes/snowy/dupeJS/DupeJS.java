package codes.snowy.dupeJS;

import co.aikar.commands.PaperCommandManager;
import codes.snowy.dupeJS.adminutils.commands.AdminCommand;
import codes.snowy.dupeJS.adminutils.commands.AnnounceCommand;
import codes.snowy.dupeJS.adminutils.commands.HealCommand;
import codes.snowy.dupeJS.afk.AFKAdminCommand;
import codes.snowy.dupeJS.afk.AFKCommand;
import codes.snowy.dupeJS.afk.AFKManager;
import codes.snowy.dupeJS.basic.*;
import codes.snowy.dupeJS.dupe.*;
import codes.snowy.dupeJS.economy.DupeyEconomy;
import codes.snowy.dupeJS.economy.EconomyCommand;
import codes.snowy.dupeJS.economy.PayCommand;
import codes.snowy.dupeJS.economy.BalanceCommand;
import codes.snowy.dupeJS.missions.MissionGUIListener;
import codes.snowy.dupeJS.missions.MissionManager;
import codes.snowy.dupeJS.missions.MissionDatabase;
import codes.snowy.dupeJS.missions.MissionGUI;
import codes.snowy.dupeJS.missions.RewardSystem;
import codes.snowy.dupeJS.missions.RewardSystemListener;
import codes.snowy.dupeJS.missions.MissionListener;
import codes.snowy.dupeJS.missions.commands.ForceResetMissionsCommand;
import codes.snowy.dupeJS.missions.commands.MissionCommand;
import codes.snowy.dupeJS.player.PlayerListener;
import codes.snowy.dupeJS.player.PlayerManager;
import codes.snowy.dupeJS.session.SessionListener;
import codes.snowy.dupeJS.shards.AdminShardShopCommand;
import codes.snowy.dupeJS.shards.ShardShopCommand;
import codes.snowy.dupeJS.shards.ShardShopGUI;
import codes.snowy.dupeJS.shards.ShardShopListener;
import codes.snowy.dupeJS.staff.chat.StaffChatCommand;
import codes.snowy.dupeJS.staff.chat.StaffChatListener;
import codes.snowy.dupeJS.staff.chat.StaffChatManager;
import codes.snowy.dupeJS.staff.vanish.VanishListener;
import codes.snowy.dupeJS.staff.vanish.VanishManager;
import codes.snowy.dupeJS.teams.TeamListener;
import codes.snowy.dupeJS.teams.TeamManager;
import codes.snowy.dupeJS.teams.commands.TeamAdminCommand;
import codes.snowy.dupeJS.teams.commands.TeamCommand;
import codes.snowy.dupeJS.tpa.TpaAcceptCommand;
import codes.snowy.dupeJS.tpa.TpaCancelCommand;
import codes.snowy.dupeJS.tpa.TpaCommand;
import codes.snowy.dupeJS.tpa.TpaDenyCommand;
import codes.snowy.dupeJS.tpa.TpaHereCommand;
import codes.snowy.dupeJS.utils.*;
import codes.snowy.dupeJS.bundles.AdminBundleCommand;
import codes.snowy.dupeJS.bundles.BundleListener;
import codes.snowy.dupeJS.bundles.BundleManager;
import codes.snowy.dupeJS.bundles.PreviewBundleCommand;
import codes.snowy.dupeJS.crushplus.CrushPlusManager;
import codes.snowy.dupeJS.crushplus.FlightCommand;
import codes.snowy.dupeJS.crushplus.FlightRestrictionListener;
import codes.snowy.dupeJS.homes.HomeCommand;
import codes.snowy.dupeJS.homes.HomeListener;
import codes.snowy.dupeJS.homes.HomeManager;
import codes.snowy.dupeJS.lifesteal.LifestealListener;
import codes.snowy.dupeJS.lifesteal.LifestealManager;
import codes.snowy.dupeJS.lifesteal.LSAdminCommand;
import codes.snowy.dupeJS.lifesteal.PayHeartsCommand;
import codes.snowy.dupeJS.lifesteal.WithdrawCommand;
import codes.snowy.dupeJS.kits.commands.KitCommand;
import codes.snowy.dupeJS.kits.commands.KitEditorCommand;
import codes.snowy.dupeJS.kits.KitGUI;
import codes.snowy.dupeJS.kits.KitListener;
import codes.snowy.dupeJS.kits.KitManager;
import codes.snowy.dupeJS.teleporter.TeleportManager;
import codes.snowy.dupeJS.staff.vanish.VanishCommand;
import codes.snowy.dupeJS.afk.AFKRewardTask;
import codes.snowy.dupeJS.kits.KitCooldownManager;
import codes.snowy.dupeJS.warp.WarpCommand;
import codes.snowy.dupeJS.warp.WarpDatabase;
import codes.snowy.dupeJS.warp.WarpGUI;
import codes.snowy.dupeJS.warp.WarpGUIListener;
import codes.snowy.dupeJS.warp.WarpManager;
import codes.snowy.dupeJS.warp.WarpAdminCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.event.ItemListener;
import java.io.File;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import codes.snowy.dupeJS.items.ItemManager;
import codes.snowy.dupeJS.items.commands.AdminSaveCommand;
import codes.snowy.dupeJS.items.commands.GiveCustomCommand;
import codes.snowy.dupeJS.teams.TeamPvPListener;

public final class DupeJS extends JavaPlugin {

    DupeManager dupeManager;
    LifestealManager lifestealmanager;
    CrushPlusManager crushPlusManager;
    HomeManager homeManager;
    TeleportManager teleportManager;
    BundleManager bundleManager;
    StaffChatManager staffChatManager;
    VanishManager vanishManager;
    AFKManager afkManager;
    AFKRewardTask afkRewardTask;
    ShardShopGUI shardShopGUI;
    PlayerManager playerManager;
    private Config config;
    private Language language;
    private DatabaseHelper dbHelper;
    private static DupeJS instance;
    KitManager kitManager;
    KitGUI kitGUI;
    private KitCooldownManager kitCooldownManager;
    MissionManager missionManager;
    MissionGUI missionGUI;
    RewardSystem rewardSystem;
    MissionDatabase missionDatabase;
    CommandCompletions commandCompletions;
    WarpManager warpManager;
    WarpDatabase warpDatabase;
    TeamManager teamManager;
    private ItemManager itemManager;
    private TeamListener teamListener;
    private TeamCommand teamCommand;
    private TeamAdminCommand teamAdminCommand;

    public static DupeJS getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {


        DupeJS.instance = this;

        loadConfig();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Logger.INSTANCE.log("PlaceholderAPI has been detected and is enabled", "success");
            new PlaceholderHandler(this).register();
        } else {
            Logger.INSTANCE.log("PlaceholderAPI has not been detected or is not enabled", "error");
            Logger.INSTANCE.log("DupeJS will not work properly without PlaceholderAPI", "error");
            Logger.INSTANCE.log("Auto disabling DupeJS... contact @snowyjs", "error");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        /*

            Registering Managers

        */

        homeManager = new HomeManager();
        teleportManager = new TeleportManager(this);
        dupeManager = new DupeManager();
        lifestealmanager = new LifestealManager();
        crushPlusManager = new CrushPlusManager(this);
        dbHelper = new DatabaseHelper();
        afkManager = new AFKManager();
        shardShopGUI = new ShardShopGUI(afkManager);
        kitManager = new KitManager();
        kitCooldownManager = new KitCooldownManager();
        kitGUI = new KitGUI(kitManager, kitCooldownManager);
        rewardSystem = new RewardSystem(this);
        missionDatabase = new MissionDatabase();
        missionManager = new MissionManager(missionDatabase);
        missionGUI = new MissionGUI(missionManager, rewardSystem);
        warpDatabase = new WarpDatabase();
        warpManager = new WarpManager(warpDatabase);
        WarpGUI warpGUI = new WarpGUI(warpManager, teleportManager);
        playerManager = new PlayerManager();
        itemManager = new ItemManager();
        teamManager = new TeamManager();
        teamListener = new TeamListener(teamManager);
        teamCommand = new TeamCommand(teamManager, teamListener);
        teamAdminCommand = new TeamAdminCommand(teamManager);

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("help");
        Logger.INSTANCE.log("Loaded the Command Manager", "success");

        Logger.INSTANCE.log("Loaded Command Completions", "success");
        commandCompletions = new CommandCompletions(kitManager, warpManager, teamManager);
        commandCompletions.register(manager);

        // Injecting into Vault Economy
        DupeyEconomy.register();


        /*

            Commands

        */

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
        manager.registerCommand(new HealCommand());
        Logger.INSTANCE.log("Loaded the Heal Command", "success");
        manager.registerCommand(new SpawnCommand());
        Logger.INSTANCE.log("Loaded the Spawn Command", "success");
        manager.registerCommand(new SetSpawnCommand());
        Logger.INSTANCE.log("Loaded the SetSpawn Command", "success");
        manager.registerCommand(new TpaCommand());
        Logger.INSTANCE.log("Loaded the Tpa Command", "success");
        manager.registerCommand(new TpaHereCommand());
        Logger.INSTANCE.log("Loaded the TpaHere Command", "success");
        manager.registerCommand(new TpaAcceptCommand());
        Logger.INSTANCE.log("Loaded the TpaAccept Command", "success");
        manager.registerCommand(new TpaCancelCommand());
        Logger.INSTANCE.log("Loaded the TpaCancel Command", "success");
        manager.registerCommand(new TpaDenyCommand());
        Logger.INSTANCE.log("Loaded the TpaDeny Command", "success");
        manager.registerCommand(new DupeRechargeCommand(dupeManager));
        Logger.INSTANCE.log("Loaded the DupeRecharge Command", "success");
        manager.registerCommand(new DiscordCommand());
        Logger.INSTANCE.log("Loaded the Discord Command", "success");
        manager.registerCommand(new StoreCommand());
        Logger.INSTANCE.log("Loaded the Store Command", "success");
        manager.registerCommand(new AFKAdminCommand(afkManager));
        Logger.INSTANCE.log("Loaded the AFKAdmin Command", "success");
        manager.registerCommand(new AdminCommand());
        Logger.INSTANCE.log("Loaded the Admin Command", "success");
        manager.registerCommand(new DonorCommand());
        Logger.INSTANCE.log("Loaded the Donor Command", "success");
        manager.registerCommand(new ShardShopCommand(shardShopGUI));
        Logger.INSTANCE.log("Loaded the ShardShop Command", "success");
        manager.registerCommand(new KitCommand(kitManager, kitGUI, kitCooldownManager));
        Logger.INSTANCE.log("Loaded the Kit Command", "success");
        manager.registerCommand(new KitEditorCommand(kitManager, kitGUI));
        Logger.INSTANCE.log("Loaded the Kit Editor Command", "success");
        manager.registerCommand(new MissionCommand(missionManager, missionGUI, rewardSystem));
        Logger.INSTANCE.log("Loaded the Mission Command", "success");
        manager.registerCommand(new WarpCommand(warpGUI, warpManager, teleportManager));
        Logger.INSTANCE.log("Loaded the Warp Command", "success");
        manager.registerCommand(new WarpAdminCommand(warpDatabase));
        Logger.INSTANCE.log("Loaded the WarpAdmin Command", "success");
        manager.registerCommand(new AFKCommand(warpManager, teleportManager));
        Logger.INSTANCE.log("Loaded the AFK Command", "success");
        manager.registerCommand(new KoTHCommand(warpManager, teleportManager));
        Logger.INSTANCE.log("Loaded the KoTH Command", "success");
        manager.registerCommand(new codes.snowy.dupeJS.store.StoreCommand());
        Logger.INSTANCE.log("Loaded the Store Command", "success");
        manager.registerCommand(new EconomyCommand());
        Logger.INSTANCE.log("Loaded the Economy Command", "success");
        manager.registerCommand(new PayCommand());
        Logger.INSTANCE.log("Loaded the Pay Command", "success");
        manager.registerCommand(new BalanceCommand());
        Logger.INSTANCE.log("Loaded the Balance Command", "success");
        manager.registerCommand(new RechargeAllDupeCommand(dupeManager));
        Logger.INSTANCE.log("Loaded the RechargeAllDupe Command", "success");
        manager.registerCommand(new ForceResetMissionsCommand(missionManager));
        Logger.INSTANCE.log("Loaded the ForceResetMissions Command", "success");
        manager.registerCommand(new AdminShardShopCommand());
        Logger.INSTANCE.log("Loaded the AdminShardShop Command", "success");
        manager.registerCommand(new AdminSaveCommand(itemManager));
        Logger.INSTANCE.log("Loaded the AdminSave Command", "success");
        manager.registerCommand(new GiveCustomCommand(itemManager));
        Logger.INSTANCE.log("Loaded the GiveCustom Command", "success");
        manager.registerCommand(new AnnounceCommand());
        Logger.INSTANCE.log("Loaded the Announce Command", "success");
        manager.registerCommand(new ShoutCommand());
        Logger.INSTANCE.log("Loaded the Shout Command", "success");
        manager.registerCommand(teamCommand);
        Logger.INSTANCE.log("Loaded the Team Command", "success");
        manager.registerCommand(teamAdminCommand);
        Logger.INSTANCE.log("Loaded the TeamAdmin Command", "success");

        /*
        
            Listeners

        */

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

        getServer().getPluginManager().registerEvents(new ShardShopListener(afkManager), this);
        Logger.INSTANCE.log("Loaded the ShardShop Listener", "success");

        getServer().getPluginManager().registerEvents(new KitListener(kitManager, kitCooldownManager), this);
        Logger.INSTANCE.log("Loaded the Kit Listener", "success");

        if (afkRewardTask == null) {
            afkRewardTask = new AFKRewardTask(this, afkManager);
            Logger.INSTANCE.log("AFK Reward Task has been initialized", "success");
        }
        afkRewardTask.startAFKTimer(this, afkManager);
        Logger.INSTANCE.log("AFK Reward Task has been started", "success");

        getServer().getPluginManager().registerEvents(new MissionGUIListener(missionManager, rewardSystem), this);
        Logger.INSTANCE.log("Loaded the MissionGUI Listener", "success");
        
        getServer().getPluginManager().registerEvents(new MissionListener(missionManager), this);
        Logger.INSTANCE.log("Loaded the Mission Listener", "success");

        getServer().getPluginManager().registerEvents(new RewardSystemListener(), this);
        Logger.INSTANCE.log("Loaded the RewardSystem Listener", "success");

        getServer().getPluginManager().registerEvents(new WarpGUIListener(warpManager, teleportManager), this);
        Logger.INSTANCE.log("Loaded the WarpGUI Listener", "success");

        getServer().getPluginManager().registerEvents(new PlayerListener(playerManager, dbHelper), this);
        Logger.INSTANCE.log("Loaded the Player Listener", "success");

        getServer().getPluginManager().registerEvents(new SpawnerShopListener(shardShopGUI), this);
        Logger.INSTANCE.log("Loaded the SpawnerShop Listener", "success");

        getServer().getPluginManager().registerEvents(teamListener, this);
        Logger.INSTANCE.log("Loaded the Team Listener", "success");

        getServer().getPluginManager().registerEvents(new TeamPvPListener(teamManager), this);

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
            Logger.INSTANCE.log("Failed to load config file on start-up", "error");
            Logger.INSTANCE.log("Auto disabling DupeJS... contact @snowyjs", "error");
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
