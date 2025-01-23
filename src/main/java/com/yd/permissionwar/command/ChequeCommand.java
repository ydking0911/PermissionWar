package com.yd.permissionwar.command;

import com.yd.permissionwar.PermissionWar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ChequeCommand implements CommandExecutor {

    private PermissionWar plugin;

    public ChequeCommand(PermissionWar plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /수표 지급 [플레이어] [금액]
        if (args.length < 3) {
            sender.sendMessage("사용법: /수표 지급 [플레이어] [금액]");
            return true;
        }
        if (!args[0].equalsIgnoreCase("지급")) {
            sender.sendMessage("사용법: /수표 지급 [플레이어] [금액]");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null) {
            sender.sendMessage("해당 플레이어를 찾을 수 없습니다.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("금액은 숫자로 입력해주세요.");
            return true;
        }

        // 수표 아이템 생성
        ItemStack cheque = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = cheque.getItemMeta();
        meta.setDisplayName("§a수표");
        meta.setLore(Arrays.asList(ChatColor.WHITE + "오른쪽 클릭 시 " + ChatColor.YELLOW + amount + ChatColor.WHITE + "원이 지급됩니다."));
        cheque.setItemMeta(meta);

        // 실제로 타겟 온라인 상태일 때만 지급
        if (target.isOnline()) {
            target.getPlayer().getInventory().addItem(cheque);
            sender.sendMessage("해당 플레이어에게 수표를 지급했습니다.");
            target.getPlayer().sendMessage(ChatColor.GREEN.toString() + amount + "원이 적힌 수표가 지급되었습니다.");
        } else {
            sender.sendMessage("해당 플레이어는 오프라인입니다. 온라인 상태에서 다시 시도해주세요.");
        }

        return true;
    }
}