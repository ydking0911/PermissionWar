package com.yd.permissionwar.command;

import com.yd.permissionwar.PermissionWar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GameStartCommand implements CommandExecutor {

    private PermissionWar plugin;

    public GameStartCommand(PermissionWar plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (plugin.isGameRunning()) {
            sender.sendMessage("이미 게임이 진행중입니다.");
            return true;
        }
        plugin.startGame();
        sender.sendMessage("게임을 시작했습니다!");
        return true;
    }
}
