package com.yd.permissionwar.command;

import com.yd.permissionwar.PermissionWar;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor {

    private PermissionWar plugin;

    public CommandManager(PermissionWar plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.YELLOW + "========== 권한전쟁 명령어 목록 ==========");
        sender.sendMessage("/레드팀 참가, /레드팀 나가기");
        sender.sendMessage("/블루팀 참가, /블루팀 나가기");
        sender.sendMessage("/코어설정 [메인 | 블루 | 레드 | 초기화] - 게임시작 명령어 입력 전 필수 설정사항");
        sender.sendMessage("/수표 지급 [플레이어] [금액]");
        sender.sendMessage("/게임시작, /게임종료");
        sender.sendMessage("/메뉴 - (SHIFT + 손바꾸기)로 가능");
        return true;
    }

}
