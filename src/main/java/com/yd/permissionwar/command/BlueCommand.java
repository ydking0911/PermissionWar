package com.yd.permissionwar.command;

import com.yd.permissionwar.PermissionWar;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlueCommand implements CommandExecutor {

    private PermissionWar plugin;

    public BlueCommand(PermissionWar plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            sender.sendMessage("/블루팀 [참가 | 나가기]");
            return true;
        }

        if (args[0].equalsIgnoreCase("참가")) {
            if (plugin.getTeamManager().getTeam(player).equalsIgnoreCase("RED")) {
                plugin.getTeamManager().leaveTeam(player, "레드팀");
            }
            plugin.getTeamManager().joinTeam(player, "BLUE");
        } else if (args[0].equalsIgnoreCase("나가기")) {
            if (plugin.getTeamManager().getTeam(player).equalsIgnoreCase("BLUE")) {
                plugin.getTeamManager().leaveTeam(player, "블루팀");
            } else {
                player.sendMessage(ChatColor.RED + "블루팀에 속해있지 않습니다.");
            }
        }
        return true;
    }
}