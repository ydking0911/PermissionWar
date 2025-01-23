package com.yd.permissionwar.command;

import com.yd.permissionwar.PermissionWar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GameEndCommand implements CommandExecutor {

    private PermissionWar plugin;

    public GameEndCommand(PermissionWar plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.isGameRunning()) {
            sender.sendMessage("게임이 진행 중이 아닙니다.");
            return true;
        }
        plugin.endGame();
        sender.sendMessage("게임을 강제로 종료시켰습니다.");
        return true;
    }
}