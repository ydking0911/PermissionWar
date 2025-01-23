package com.yd.permissionwar.util;

import com.yd.permissionwar.PermissionWar;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigUtil {

    private final PermissionWar plugin;
    private final File configFile;
    private FileConfiguration config;

    public ConfigUtil(PermissionWar plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!plugin.getDataFolder().exists()) {
            if (!plugin.getDataFolder().mkdirs()) {
                plugin.getLogger().severe("데이터 폴더를 생성하지 못했습니다.");
                return;
            }
        }

        if (!configFile.exists()) {
            try {
                plugin.saveDefaultConfig();
                plugin.getLogger().info("config.yml 파일 생성됨.");
            } catch (Exception e) {
                plugin.getLogger().severe("config.yml 파일 생성 실패: " + e.getMessage());
            }
        }
        reloadConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public void saveConfig() {
        plugin.saveConfig();
    }


    public Object get(String path) {
        return config.get(path);
    }

    public Object get(String path, Object def) {
        return config.get(path, def);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public void set(String path, Object value) {
        config.set(path, value);
    }
}