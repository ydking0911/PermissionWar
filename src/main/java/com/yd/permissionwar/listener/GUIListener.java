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
import org.bukkit.inventory.meta.SkullMeta;

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
            // 랜덤 플레이어 잠그기
            Player randomOpponent = lockManager.getRandomOpponent(player);
            if (randomOpponent == null) {
                player.sendMessage(ChatColor.RED + "잠글 수 있는 상대가 없습니다.");
            } else {
                openLockPermGUI(player, randomOpponent);
            }
        } else {
            // 특정 플레이어 머리 클릭
            ItemStack clickedItem = player.getOpenInventory().getItem(slot);
            if (clickedItem != null && clickedItem.getType() == Material.PLAYER_HEAD) {
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

        try {
            if (slot == 26) {
                // 랜덤 권한 잠그기
                lockManager.lockRandomPermission(player, target);
            } else {
                // 특정 권한 잠그기
                ItemStack clickedItem = player.getOpenInventory().getItem(slot);
                if (clickedItem == null || !clickedItem.hasItemMeta()) return;

                // 아이템의 DisplayName = 순수 권한 이름 (ex: "점프")
                String permName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
                if (permName == null || permName.isEmpty()) {
                    player.sendMessage(ChatColor.RED + "잘못된 권한입니다.");
                    return;
                }

                // LockManager에서 처리 (출금 + 잠금)
                lockManager.lockPermission(player, target, permName);
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "권한 잠금 중 오류가 발생했습니다.");
            plugin.getLogger().severe("Permission lock error: " + e.getMessage());
        }
        player.closeInventory();
    }

    private void handleUnlockPermissionGUIClick(Player player, int slot) {
        if (slot == 26) {
            // 랜덤 권한 해제
            lockManager.unlockRandomPermission(player);
        } else {
            // 특정 권한 해제
            ItemStack clickedItem = player.getOpenInventory().getItem(slot);
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            // DisplayName = 순수 권한 이름
            String permName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            if (permName == null || permName.isEmpty()) {
                player.sendMessage(ChatColor.RED + "잘못된 권한입니다.");
                return;
            }

            lockManager.unlockPermission(player, permName);
        }
        player.closeInventory();
    }

    private void openOpponentGUI(Player player) {
        String playerTeam = teamManager.getTeam(player);
        Inventory inv = Bukkit.createInventory(null, 27, playerTeam.equals("RED") ? "블루팀GUI" : "레드팀GUI");

        // 상대 팀 플레이어 목록
        List<Player> opponents = getTeamPlayers(playerTeam.equals("RED") ? "BLUE" : "RED");
        for (int i = 0; i < Math.min(opponents.size(), 26); i++) {
            inv.setItem(i, createHeadItem(opponents.get(i)));
        }

        inv.setItem(26, createShellItem("랜덤 플레이어 잠그기"));
        player.openInventory(inv);
    }

    private void openLockPermGUI(Player locker, Player target) {
        Inventory inv = Bukkit.createInventory(null, 27, "권한 잠그기");
        String[] permissions = { "앉기", "점프", "때리기", "이동하기", "달리기", "NPC상호작용하기", "블럭캐기", "블럭설치하기", "버리기" };

        for (int i = 0; i < permissions.length; i++) {
            double cost = configUtil.getDouble("lock-cost." + permissions[i], 10.0);
            
            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta meta = paper.getItemMeta();

            // 권한 이름만 표시
            meta.setDisplayName(ChatColor.YELLOW + permissions[i]);

            // 비용은 Lore로 표시
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "비용: " + cost);
            meta.setLore(lore);

            paper.setItemMeta(meta);
            inv.setItem(i, paper);
        }

        // 26번 슬롯 = "랜덤 권한 잠그기"
        inv.setItem(26, createShellItem("랜덤 권한 잠그기"));

        // 이 GUI를 열 때, '어느 플레이어에게 잠글지'를 저장
        PermGUIHolder.setTarget(locker.getUniqueId(), target);

        locker.openInventory(inv);
    }

    private void openUnlockGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "권한 해제");
        Set<String> lockedPermissions = lockManager.getLockedPermissions(player);

        int index = 0;
        for (String perm : lockedPermissions) {
            double cost = configUtil.getDouble("unlock-cost." + perm, 5.0);

            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta meta = paper.getItemMeta();

            meta.setDisplayName(ChatColor.YELLOW + perm);

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "해제 비용: " + cost);
            meta.setLore(lore);

            paper.setItemMeta(meta);
            inv.setItem(index++, paper);
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
        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(player.getName());
            skullMeta.setDisplayName(ChatColor.YELLOW + player.getName());
            head.setItemMeta(skullMeta);
        }
        return head;
    }

    private ItemStack createShellItem(String displayName) {
        ItemStack shell = new ItemStack(Material.NAUTILUS_SHELL);
        ItemMeta meta = shell.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + displayName);
        shell.setItemMeta(meta);
        return shell;
    }

    // 누가 누구를 대상으로 잠금을 거는지 -> 기억하는 내부 클래스
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
