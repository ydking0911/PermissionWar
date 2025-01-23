package com.yd.permissionwar.manager;

import com.yd.permissionwar.PermissionWar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {

    private PermissionWar plugin;

    public ScoreboardManager(PermissionWar plugin) {
        this.plugin = plugin;
    }

    public void updateScoreboard(Player player) {
        Scoreboard board;
        Objective obj;

        if (player.getScoreboard() == null || player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
        } else {
            board = player.getScoreboard();
        }

        obj = board.getObjective("PermissionWar");
        if (obj == null) {
            obj = board.registerNewObjective("PermissionWar", "dummy", ChatColor.GOLD + "권한전쟁");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        for (String entry : board.getEntries()) {
            board.resetScores(entry);
        }

        obj.getScore(" ").setScore(4);

        // 팀 (레드팀/블루팀)
        String teamName = plugin.getTeamManager().getTeam(player);
        if (teamName.equalsIgnoreCase("RED")) {
            teamName = ChatColor.RED + "레드팀";
        } else if (teamName.equalsIgnoreCase("BLUE")) {
            teamName = ChatColor.BLUE + "블루팀";
        } else {
            teamName = ChatColor.GRAY + "팀 없음";
        }

        obj.getScore(ChatColor.WHITE + "팀: " + teamName).setScore(3);

        // 팀 점수
        int teamScore = 0;
        if (plugin.getTeamManager().getTeam(player).equalsIgnoreCase("RED")) {
            teamScore = plugin.getTeamManager().getRedTeamScore();
        } else if (plugin.getTeamManager().getTeam(player).equalsIgnoreCase("BLUE")) {
            teamScore = plugin.getTeamManager().getBlueTeamScore();
        }
        obj.getScore(ChatColor.WHITE + "팀 점수: " + ChatColor.YELLOW + teamScore).setScore(2);

        // 돈
        double money = plugin.getEconomy().getBalance(player);
        obj.getScore(ChatColor.WHITE + "보유 돈: " + ChatColor.YELLOW + (int) money).setScore(1);

        player.setScoreboard(board);
    }

    // 모든 플레이어 스코어보드 갱신
    public void updateAllPlayersScoreboard() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            updateScoreboard(p);
        }
    }

    // 게임 종료 시 스코어보드 해제
    public void clearScoreboard() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

}
