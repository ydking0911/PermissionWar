package com.yd.permissionwar.manager;

import com.yd.permissionwar.PermissionWar;
import com.yd.permissionwar.util.ConfigUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class MoneyManager {

    private PermissionWar plugin;
    private Economy economy;
    private BukkitTask moneyTask;
    private ConfigUtil configUtil;

    private double moneyPerSecond;

    public MoneyManager(PermissionWar plugin, ConfigUtil configUtil) {
        this.plugin = plugin;
        this.configUtil = configUtil;
        this.economy = plugin.getEconomy();
    }


}
