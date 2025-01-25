package com.yd.permissionwar.manager;

import com.yd.permissionwar.PermissionWar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CoreManager {

    private PermissionWar plugin;

    private Location mainCoreLocation;
    private Location redCoreLocation;
    private Location blueCoreLocation;

    private boolean isMainCoreGold = false;

    public CoreManager(PermissionWar plugin) {
        this.plugin = plugin;
    }

    public void setMainCore(Block block) {
        mainCoreLocation = block.getLocation();
        block.setType(Material.YELLOW_STAINED_GLASS);
        isMainCoreGold = false;
        Bukkit.getLogger().info("메인 코어가 설정되었습니다!");

    }

    public void scheduleMainCoreGoldTask() {
        // 메인코어 설정되지 않았을 시 취소
        if (mainCoreLocation == null) {
            Bukkit.getLogger().warning("메인 코어가 설정되지 않았습니다!");
            return;
        }

        // 게임 시작 시 5분 후 금블럭으로 변경
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.isGameRunning() || mainCoreLocation == null) {
                    cancel();
                    return;
                }
                mainCoreLocation.getBlock().setType(Material.GOLD_BLOCK);
                isMainCoreGold = true;
                Bukkit.getLogger().info("메인 코어가 금 블록으로 변경되었습니다!");
            }
        }.runTaskLater(plugin, 5 * 60 * 20L); // 5분 (5*60초)
    }


    public void setMainCoreBackToGlass() {
        if (mainCoreLocation != null) {
            mainCoreLocation.getBlock().setType(Material.YELLOW_STAINED_GLASS);
            isMainCoreGold = false;
        }
        // 5분후 다시 금블럭으로 변환 예약
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.isGameRunning() || mainCoreLocation == null) {
                    cancel();
                    return;
                }
                mainCoreLocation.getBlock().setType(Material.GOLD_BLOCK);
                isMainCoreGold = true;
                Bukkit.getLogger().info("메인 코어가 금 블록으로 다시 변경되었습니다!");
            }
        }.runTaskLater(plugin, 5 * 60 * 20L);
    }

    public void setRedCore(Block block) {
        redCoreLocation = block.getLocation();
        block.setType(Material.RED_STAINED_GLASS);
        Bukkit.getLogger().info("레드팀 코어가 설정되었습니다!");
    }

    public void setBlueCore(Block block) {
        blueCoreLocation = block.getLocation();
        block.setType(Material.BLUE_STAINED_GLASS);
        Bukkit.getLogger().info("블루팀 코어가 설정되었습니다!");
    }

    public void resetCores() {
        if (mainCoreLocation != null) {
            mainCoreLocation.getBlock().setType(Material.AIR);
            mainCoreLocation = null;
        }
        if (redCoreLocation != null) {
            redCoreLocation.getBlock().setType(Material.AIR);
            redCoreLocation = null;
        }
        if (blueCoreLocation != null) {
            blueCoreLocation.getBlock().setType(Material.AIR);
            blueCoreLocation = null;
        }
        isMainCoreGold = false;
    }

    public boolean isMainCore(Block block) {
        if (mainCoreLocation == null) return false;
        return mainCoreLocation.equals(block.getLocation());
    }

    public boolean isRedCore(Block block) {
        if (redCoreLocation == null) return false;
        return redCoreLocation.equals(block.getLocation());
    }

    public boolean isBlueCore(Block block) {
        if (blueCoreLocation == null) return false;
        return blueCoreLocation.equals(block.getLocation());
    }

    public boolean isMainCoreGold() {
        return isMainCoreGold;
    }

    // 코어 우클릭 -> 네더의 별 지급
    public void giveNetherStar(Player player) {
        player.getInventory().addItem(new ItemStack(Material.NETHER_STAR, 1));
    }

    public Location getMainCoreLocation() {
        return mainCoreLocation;
    }

    public Location getRedCoreLocation() {
        return redCoreLocation;
    }

    public Location getBlueCoreLocation() {
        return blueCoreLocation;
    }

}
