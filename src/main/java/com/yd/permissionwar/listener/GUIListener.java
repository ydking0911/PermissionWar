package com.yd.permissionwar.listener;

import com.yd.permissionwar.PermissionWar;
import com.yd.permissionwar.manager.LockManager;
import com.yd.permissionwar.manager.TeamManager;
import com.yd.permissionwar.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GUIListener implements Listener {

    private final PermissionWar plugin;
    private final LockManager lockManager;
    private final TeamManager teamManager;
    private final ConfigUtil configUtil;

    public GUIListener(PermissionWar plugin) {
        this.plugin = plugin;
        this.lockManager = plugin.getLockManager();
        this.teamManager = plugin.getTeamManager();
        this.configUtil = plugin.getConfigUtil();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        Inventory inv = event.getInventory();
        if (inv == null || event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        String title = event.getView().getTitle();
        int slot = event.getRawSlot();


        switch (title) {
            case "등급전쟁":
                event.setCancelled(true);
                handleMainMenuClick(player, slot);
                break;
            case "레드팀GUI":
            case "블루팀GUI":
                event.setCancelled(true);
                handleTeamGUIClick(player, title, slot);
                break;
            case "권한 잠그기":
                event.setCancelled(true);
                handleLockPermissionGUIClick(player, slot);
                break;
            case "권한 해제":
                event.setCancelled(true);
                handleUnlockPermissionGUIClick(player, slot);
                break;
        }
    }

    private void handleMainMenuClick(Player player, int slot) {
        if (slot == 11) {
            openOpponentGUI(player);
        } else if (slot == 15) {
            openUnlockGUI(player);
        }
    }

    private void handleTeamGUIClick(Player player, String title, int slot) {
        if (slot == 26) {
            Player randomOpponent = lockManager.getRandomOpponent(player);
            if (randomOpponent == null) {
                player.sendMessage(ChatColor.RED + "잠글 수 있는 상대가 없습니다.");
            } else {
                openLockPermGUI(player, randomOpponent);
            }
        } else {
            ItemStack clickedItem = player.getOpenInventory().getItem(slot);
            if (clickedItem.getType() == Material.PLAYER_HEAD) {
                String targetName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
                Player target = Bukkit.getPlayerExact(targetName);
                if (target != null) {
                    openLockPermGUI(player, target);
                } else {
                    player.sendMessage(ChatColor.RED + "플레이어를 찾을 수 없습니다: " + targetName);
                }
            }
        }
    }

    private void handleLockPermissionGUIClick(Player player, int slot) {
        Player target = PermGUIHolder.getTarget(player.getUniqueId());
        if (target == null) {
            player.sendMessage(ChatColor.RED + "타겟을 찾을 수 없습니다.");
            return;
        }

        if (slot == 26) {
            lockManager.lockRandomPermission(player, target);
        } else {
            ItemStack clickedItem = player.getOpenInventory().getItem(slot);
            String permName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            double cost = configUtil.getDouble("lock-cost." + permName, 10.0);
            if (plugin.getEconomy().getBalance(player) < cost) {
                player.sendMessage(ChatColor.RED + "돈이 부족합니다! 필요 금액: " + cost);
                return;
            }
            plugin.getEconomy().withdrawPlayer(player, cost);
            lockManager.lockPermission(player, target, permName);
        }
        player.closeInventory();
    }

    private void handleUnlockPermissionGUIClick(Player player, int slot) {
        if (slot == 26) {
            lockManager.unlockRandomPermission(player);
        } else {
            ItemStack clickedItem = player.getOpenInventory().getItem(slot);
            String permName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            double cost = configUtil.getDouble("unlock-cost." + permName, 5.0);
            if (plugin.getEconomy().getBalance(player) < cost) {
                player.sendMessage(ChatColor.RED + "돈이 부족합니다! 필요 금액: " + cost);
                return;
            }
            plugin.getEconomy().withdrawPlayer(player, cost);
            lockManager.unlockPermission(player, permName);
        }
        player.closeInventory();
    }

    private void openOpponentGUI(Player player) {
        String playerTeam = teamManager.getTeam(player);
        Inventory inv = Bukkit.createInventory(null, 27, playerTeam.equals("RED") ? "블루팀GUI" : "레드팀GUI");

        List<Player> opponents = getTeamPlayers(playerTeam.equals("RED") ? "BLUE" : "RED");
        for (int i = 0; i < Math.min(opponents.size(), 26); i++) {
            inv.setItem(i, createHeadItem(opponents.get(i)));
        }

        inv.setItem(26, createShellItem("랜덤 플레이어 잠그기"));
        player.openInventory(inv);
    }

    private void openLockPermGUI(Player locker, Player target) {
        String lockerTeam = teamManager.getTeam(locker);
        Inventory inv = Bukkit.createInventory(null, 27, "권한 잠그기");

        String[] permissions = {"앉기", "점프", "때리기", "이동하기", "달리기", "NPC상호작용하기", "블럭캐기", "블럭설치하기", "버리기"};
        for (int i = 0; i < permissions.length; i++) {
            double cost = configUtil.getDouble("lock-cost." + permissions[i], 10.0);
            inv.setItem(i, createItem(Material.PAPER, permissions[i], ChatColor.YELLOW + permissions[i] + " (" + cost + ")"));
        }

        inv.setItem(26, createShellItem("랜덤 권한 잠그기"));
        PermGUIHolder.setTarget(locker.getUniqueId(), target);
        locker.openInventory(inv);
    }

    private void openUnlockGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "권한 해제");
        Set<String> lockedPermissions = lockManager.getLockedPermissions(player);

        int index = 0;
        for (String perm : lockedPermissions) {
            double cost = configUtil.getDouble("unlock-cost." + perm, 5.0);
            inv.setItem(index++, createItem(Material.PAPER, perm, ChatColor.YELLOW + perm + " (" + cost + ")"));
        }

        inv.setItem(26, createShellItem("랜덤 권한 해제"));
        player.openInventory(inv);
    }

    private List<Player> getTeamPlayers(String teamName) {
        List<Player> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (teamManager.getTeam(p).equalsIgnoreCase(teamName)) {
                players.add(p);
            }
        }
        return players;
    }

    private ItemStack createHeadItem(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + player.getName());
        head.setItemMeta(meta);
        return head;
    }

    private ItemStack createItem(Material material, String name, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createShellItem(String displayName) {
        ItemStack shell = new ItemStack(Material.NAUTILUS_SHELL);
        ItemMeta meta = shell.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + displayName);
        shell.setItemMeta(meta);
        return shell;
    }


    private static class PermGUIHolder {
        private static final Map<UUID, UUID> lockerTargetMap = new HashMap<>();

        public static void setTarget(UUID locker, Player target) {
            lockerTargetMap.put(locker, target.getUniqueId());
        }

        public static Player getTarget(UUID locker) {
            UUID targetUUID = lockerTargetMap.get(locker);
            return targetUUID == null ? null : Bukkit.getPlayer(targetUUID);
        }
    }
}
