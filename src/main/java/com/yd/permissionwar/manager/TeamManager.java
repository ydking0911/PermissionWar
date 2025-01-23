package com.yd.permissionwar.manager;

import com.yd.permissionwar.PermissionWar;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamManager {

    private PermissionWar plugin;
    private Map<UUID, String> playerTeams = new HashMap<>();
    private int redTeamScore = 0;
    private int blueTeamScore = 0;

    public TeamManager(PermissionWar plugin) {
        this.plugin = plugin;
    }

    public void joinTeam(Player player, String teamName) {
        String currentTeam = playerTeams.get(player.getUniqueId());

        // 기존에 다른 팀에 속해 있으면 제거
        if (currentTeam != null && !currentTeam.equalsIgnoreCase(teamName)) {
            leaveTeam(player, currentTeam);
        }
        playerTeams.put(player.getUniqueId(), teamName.toUpperCase());

        if (teamName.equalsIgnoreCase("RED")) {
            player.setDisplayName(ChatColor.RED + player.getName());
            player.sendMessage(ChatColor.RED + "레드팀에 참가하였습니다!");
        } else {
            player.setDisplayName(ChatColor.BLUE + player.getName());
            player.sendMessage(ChatColor.BLUE + "블루팀에 참가하였습니다!");
        }
        
        plugin.getScoreboardManager().updateScoreboard(player);
    }
    
    public void leaveTeam(Player player, String teamName) {
        playerTeams.remove(player.getUniqueId());
        player.setDisplayName(ChatColor.WHITE + player.getName());
        player.sendMessage(teamName + " 팀에서 나갔습니다.");
        plugin.getScoreboardManager().updateScoreboard(player);
    }
    
    public String getTeam(Player player) {
        return playerTeams.getOrDefault(player.getUniqueId(), "없음");
    }

    public int getRedTeamScore() {
        return redTeamScore;
    }

    public int getBlueTeamScore() {
        return blueTeamScore;
    }

    public void addScore(String teamName, int amount) {
        if (teamName.equalsIgnoreCase("RED")) {
            redTeamScore += amount;
        } else if (teamName.equalsIgnoreCase("BLUE")) {
            blueTeamScore += amount;
        }
    }

    public void resetTeamScore() {
        redTeamScore = 0;
        blueTeamScore = 0;
    }

    public void resetTeams() {
        playerTeams.clear();
    }

    // 현재 팀 인원 목록 반환 메서드

}
