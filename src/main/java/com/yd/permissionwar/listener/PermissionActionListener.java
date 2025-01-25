package com.yd.permissionwar.listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.yd.permissionwar.PermissionWar;
import com.yd.permissionwar.manager.LockManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;

public class PermissionActionListener implements Listener {

    private final PermissionWar plugin;
    private final LockManager lockManager;

    public PermissionActionListener(PermissionWar plugin) {
        this.plugin = plugin;
        this.lockManager = plugin.getLockManager();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (lockManager.isPermissionLocked(player, "이동하기")) {
            player.sendMessage(ChatColor.RED + "이동이 제한되었습니다!");
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (lockManager.isPermissionLocked(player, "블럭캐기")) {
            player.sendMessage(ChatColor.RED + "블럭을 캘 수 없습니다!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (lockManager.isPermissionLocked(player, "블럭설치하기")) {
            player.sendMessage(ChatColor.RED + "블럭을 설치할 수 없습니다!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (lockManager.isPermissionLocked(player, "버리기")) {
            player.sendMessage(ChatColor.RED + "아이템을 버릴 수 없습니다!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (lockManager.isPermissionLocked(player, "앉기")) {
            player.sendMessage(ChatColor.RED + "앉기가 제한되었습니다!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        if (lockManager.isPermissionLocked(player, "점프")) {
            player.sendMessage(ChatColor.RED + "점프가 제한되었습니다!");
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        if (lockManager.isPermissionLocked(player, "때리기")) {
            player.sendMessage(ChatColor.RED + "때리기가 제한되었습니다!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (lockManager.isPermissionLocked(player, "NPC상호작용하기")) {
            player.sendMessage(ChatColor.RED + "NPC와 상호작용이 제한되었습니다!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (lockManager.isPermissionLocked(player, "NPC상호작용하기")) {
            player.sendMessage(ChatColor.RED + "NPC와 상호작용이 제한되었습니다!");
            event.setCancelled(true);
        }
    }
}

