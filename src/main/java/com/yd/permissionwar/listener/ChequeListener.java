package com.yd.permissionwar.listener;

import com.yd.permissionwar.PermissionWar;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ChequeListener implements Listener {

    private PermissionWar plugin;
    private Economy economy;

    public ChequeListener(PermissionWar plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomy();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.PAPER) return;

        // 수표 아이템 확인
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() || !meta.hasLore()) return;

        String displayName = meta.getDisplayName();
        if (!displayName.contains("수표")) return;

        // 우클릭만 처리
        if (!event.getAction().name().contains("RIGHT_CLICK")) return;

        // 금액 파싱
        double amount = parseAmountFromMeta(meta.getLore());
        if (amount <= 0) {
            event.getPlayer().sendMessage(ChatColor.RED + "유효하지 않은 수표입니다.");
            return;
        }

        // 돈 추가
        economy.depositPlayer(event.getPlayer(), amount);
        event.getPlayer().sendMessage(ChatColor.GREEN.toString() + amount + "원이 지급되었습니다.");

        // 수표 아이템 제거
        item.setAmount(item.getAmount() - 1);

        event.setCancelled(true); // 이벤트 취소하여 추가 동작 방지
    }

    private double parseAmountFromMeta(List<String> lore) {
        for (String line : lore) {
            String strippedLine = ChatColor.stripColor(line); // 색상 코드 제거
            if (strippedLine.startsWith("오른쪽 클릭 시 ")) {
                String amountStr = strippedLine.replace("오른쪽 클릭 시 ", "").replace("원이 지급됩니다.", "");
                try {
                    return Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }
}
