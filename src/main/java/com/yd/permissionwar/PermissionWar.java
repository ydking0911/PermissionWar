package com.yd.permissionwar;

import com.yd.permissionwar.command.*;
import com.yd.permissionwar.listener.*;
import com.yd.permissionwar.manager.*;
import com.yd.permissionwar.util.ConfigUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;


public final class PermissionWar extends JavaPlugin {

    private static PermissionWar instance;

    private TeamManager teamManager;
    private CoreManager coreManager;
    private LockManager lockManager;
    private ScoreboardManager scoreboardManager;
    private MoneyManager moneyManager;
    private ConfigUtil configUtil;

    private Economy economy;

    private boolean isGameRunning = false;
    private int gameTime = 40 * 60; // 40분 (2400초)

    @Override
    public void onEnable() {
        instance = this;

        if (!setupEconomy()) {
            getLogger().severe("vault 연동 실패. Vault 플러그인을 확인해주세요.");
            getServer().getPluginManager().disablePlugin(this);
        }

        configUtil = new ConfigUtil(this);
        teamManager = new TeamManager(this);
        coreManager = new CoreManager(this);
        lockManager = new LockManager(this);
        scoreboardManager = new ScoreboardManager(this);
        moneyManager = new MoneyManager(this, configUtil);

        getServer().getPluginManager().registerEvents(new MenuOpenListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new CoreInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new ChequeListener(this), this);
        getServer().getPluginManager().registerEvents(new PermissionActionListener(this), this);

        getCommand("명령어").setExecutor(new CommandManager(this));
        getCommand("레드팀").setExecutor(new RedCommand(this));
        getCommand("블루팀").setExecutor(new BlueCommand(this));
        getCommand("코어설정").setExecutor(new CoreSettingCommand(this));
        getCommand("수표").setExecutor(new ChequeCommand(this));
        getCommand("게임시작").setExecutor(new GameStartCommand(this));
        getCommand("게임종료").setExecutor(new GameEndCommand(this));
        getCommand("메뉴").setExecutor(new MenuCommand(this));

        getLogger().info("권한전쟁 플러그인 활성화됨.");
    }

    @Override
    public void onDisable() {
        // 플러그인 비활성화 시 처리
        if (isGameRunning) {
            endGame();
        }
        getLogger().info("권한전쟁 플러그인이 비활성화됨.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault 플러그인이 서버에 설치되지 않았습니다.");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().severe("Vault 연동 실패: 경제 플러그인을 찾을 수 없습니다.");
            return false;
        }
        economy = rsp.getProvider();
        getLogger().info("Vault 연동 성공!");
        return true;
    }

    public static PermissionWar getInstance() {
        return instance;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    public LockManager getLockManager() {
        return lockManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public Economy getEconomy() {
        return economy;
    }

    public ConfigUtil getConfigUtil() {
        return configUtil;
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public void setGameRunning(boolean isGameRunning) {
        this.isGameRunning = isGameRunning;
    }

    public int getGameTime() {
        return gameTime;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    private BukkitTask gameTimerTask;

    public void startGameTimer() {
        gameTimerTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (isGameRunning) {
                getServer().getOnlinePlayers().forEach(player -> {
                    getEconomy().depositPlayer(player, 1);
                    scoreboardManager.updateScoreboard(player);
                });

                gameTime--;

                if (gameTime <= 0) {
                    endGame();
                } else {
                    int minutes = gameTime / 60;
                    int seconds = gameTime % 60;
                    String timeString = String.format(ChatColor.GOLD + "남은 시간: %02d:%02d", minutes, seconds);
                    getServer().getOnlinePlayers().forEach(p -> p.sendActionBar(timeString));
                }
            }
        }, 20L, 20L);
    }

    public void startGame() {
        this.isGameRunning = true;
        this.gameTime = 40 * 60;
        teamManager.resetTeamScore();
        scoreboardManager.updateAllPlayersScoreboard();
        getLogger().info("권한전쟁 게임 시작");
        startGameTimer();
    }

    public void endGame() {
        this.isGameRunning = false;
        this.gameTime = 40 * 60;

        teamManager.resetTeams();
        coreManager.resetCores();
        lockManager.resetLocks();
        scoreboardManager.clearScoreboard();

        if (gameTimerTask != null) {
            gameTimerTask.cancel();
            getServer().getOnlinePlayers().forEach(player -> {
                getEconomy().withdrawPlayer(player, getEconomy().getBalance(player));
                scoreboardManager.updateScoreboard(player);
            });
        }
        getLogger().info("권한전쟁 게임 종료");
    }




























}
