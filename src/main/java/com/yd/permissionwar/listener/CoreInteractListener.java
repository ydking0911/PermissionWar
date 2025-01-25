package com.yd.permissionwar.listener;

import com.yd.permissionwar.PermissionWar;
import com.yd.permissionwar.manager.CoreManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CoreInteractListener implements Listener {

    private PermissionWar plugin;
    private CoreManager coreManager;

    public CoreInteractListener(PermissionWar plugin) {
        this.plugin = plugin;
        this.coreManager = plugin.getCoreManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            Block block = event.getClickedBlock();

            // 중앙코어
            if (coreManager.isMainCore(block)) {
                if (coreManager.isMainCoreGold()) {
                    Player player = event.getPlayer();
                    // 이미 네더의 별을 받은 상태인지 확인
                    if (!player.getInventory().contains(Material.NETHER_STAR)) {
                        coreManager.giveNetherStar(player);
                        player.sendMessage(ChatColor.GOLD + "네더의 별을 획득했습니다!");
                        coreManager.setMainCoreBackToGlass();
                    }
                }
            } else if (coreManager.isRedCore(block) || coreManager.isBlueCore(block)) {
                // 레드/블루 코어
                Player player = event.getPlayer();
                // 플레이어 인벤토리에 네더의 별이 있는지?
                if (player.getInventory().contains(Material.NETHER_STAR)) {
                    // 네더의 별 하나 제거
                    player.getInventory().removeItem(new ItemStack(Material.NETHER_STAR, 1));

                    // 팀 점수 1점 증가
                    String team = coreManager.isRedCore(block) ? "RED" : "BLUE";
                    plugin.getTeamManager().addScore(team, 1);

                    // 스코어보드 업데이트
                    plugin.getScoreboardManager().updateAllPlayersScoreboard();
                    event.getPlayer().sendMessage(ChatColor.GREEN + "팀 점수가 1점 올라갔습니다!");
                }
            }
        }
    }
}



// 레드팀코어    중앙코어    블루팀코어
