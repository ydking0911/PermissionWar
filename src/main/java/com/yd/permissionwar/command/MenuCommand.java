package com.yd.permissionwar.command;

import com.yd.permissionwar.PermissionWar;
import com.yd.permissionwar.listener.MenuOpenListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand implements CommandExecutor {

    private PermissionWar plugin;

    public MenuCommand(PermissionWar plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다!");
            return true;
        }
        Player player = (Player) sender;

        MenuOpenListener.openMainMenu(player);
        return true;
    }
}