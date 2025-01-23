package com.yd.permissionwar.command;

import com.yd.permissionwar.PermissionWar;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RedCommand implements CommandExecutor {
    
    private PermissionWar plugin;
    
    public RedCommand(PermissionWar plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            sender.sendMessage("/레드팀 [참가 | 나가기]");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("참가")) {
            if (plugin.getTeamManager().getTeam(player).equalsIgnoreCase("BLUE")) {
                plugin.getTeamManager().leaveTeam(player, "블루팀");
            }
            plugin.getTeamManager().joinTeam(player, "RED");
        } else if (args[0].equalsIgnoreCase("나가기")) {
            if (plugin.getTeamManager().getTeam(player).equalsIgnoreCase("RED")) {
                plugin.getTeamManager().joinTeam(player, "레드팀");
            } else {
                player.sendMessage(ChatColor.RED + "레드팀에 속해 있지 않습니다.");
            }
        }
        return true;
    }

}
