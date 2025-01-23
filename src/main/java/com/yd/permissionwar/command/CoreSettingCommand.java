package com.yd.permissionwar.command;

import com.yd.permissionwar.PermissionWar;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoreSettingCommand implements CommandExecutor {

    private PermissionWar plugin;

    public CoreSettingCommand(PermissionWar plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*
            /코어설정 메인
            /코어설정 블루
            /코어설정 레드
            /코어설정 초기화
         */

        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("/코어설정 [메인 | 블루 | 레드 | 초기화]");
            return true;
        }

        Block targetBlock = player.getTargetBlockExact(5);
        if (args[0].equalsIgnoreCase("메인")) {
            if (targetBlock == null) {
                player.sendMessage(ChatColor.RED + "블록을 보고 있어야 합니다.");
                return true;
            }
            plugin.getCoreManager().setMainCore(targetBlock);
            player.sendMessage("메인 코어가 설정되었습니다!");
        } else if (args[0].equalsIgnoreCase("블루")) {
            if (targetBlock == null) {
                player.sendMessage(ChatColor.RED + "블록을 보고 있어야 합니다!");
                return true;
            }
            plugin.getCoreManager().setBlueCore(targetBlock);
            player.sendMessage("블루팀 코어가 설정되었습니다!");
        } else if (args[0].equalsIgnoreCase("레드")) {
            if (targetBlock == null) {
                player.sendMessage(ChatColor.RED + "블록을 보고 있어야 합니다!");
                return true;
            }
            plugin.getCoreManager().setRedCore(targetBlock);
            player.sendMessage("레드팀 코어가 설정되었습니다!");
        } else if (args[0].equalsIgnoreCase("초기화")) {
            plugin.getCoreManager().resetCores();
            player.sendMessage("모든 코어 설정이 초기화되었습니다!");
        }
        return true;
    }

}
