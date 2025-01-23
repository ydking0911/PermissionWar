package com.yd.permissionwar.manager;

import com.yd.permissionwar.PermissionWar;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class LockManager {

    private PermissionWar plugin;
    private Map<UUID, Set<String>> lockedPermissions = new HashMap<>();
    private List<String> possiblePermissions = Arrays.asList(
            "앉기", "점프", "때리기", "이동하기", "달리기", "NPC상호작용하기", "블럭캐기", "블럭설치하기", "버리기"
    );

    public LockManager(PermissionWar plugin) {
        this.plugin = plugin;
    }

    // 권한 잠그기
    public boolean lockPermission(Player locker, Player target, String permissionName) {
        // config에서 비용 확인
        double cost = plugin.getConfig().getDouble("lock-cost."+permissionName, 10.0);
        if (!withdrawMoney(locker, cost)) {
            locker.sendMessage(ChatColor.RED + "돈이 부족합니다! (필요 금액: "+cost+")");
            return false;
        }

        // 타겟 플레이어 잠긴 권한에 추가
        lockedPermissions.putIfAbsent(target.getUniqueId(), new HashSet<>());
        lockedPermissions.get(target.getUniqueId()).add(permissionName);

        locker.sendMessage("상대의 [" + permissionName + "] 권한을 잠갔습니다!");
        target.sendMessage("당신의 [" + permissionName + "] 권한이 잠겼습니다!");

        return true;
    }

    // 권한 해제
    public boolean unlockPermission(Player player, String permissionName) {
        Set<String> lockedSet = lockedPermissions.getOrDefault(player.getUniqueId(), new HashSet<>());
        if (!lockedSet.contains(permissionName)) {
            player.sendMessage(ChatColor.RED + "해제할 권한이 잠겨있지 않습니다.");
            return false;
        }

        double cost = plugin.getConfig().getDouble("unlock-cost." + permissionName, 5.0);
        if (!withdrawMoney(player, cost)) {
            player.sendMessage(ChatColor.RED + "돈이 부족합니다! (필요 금액: " + cost + ")");
            return false;
        }
        lockedSet.remove(permissionName);
        player.sendMessage("[" + permissionName + "] 권한이 해제되었습니다.");
        return true;
    }

    private boolean withdrawMoney(Player player, double amount) {
        if (plugin.getEconomy().getBalance(player) < amount) {
            return false;
        }
        plugin.getEconomy().withdrawPlayer(player, amount);
        return true;
    }

    // 랜덤 권한 잠그기 (타겟 한 명에게)
    public boolean lockRandomPermission(Player locker, Player target) {
        // 잠글 수 있는 권한 중, 타겟이 아직 잠기지 않은 권한만 추려서 랜덤
        Set<String> lockedSet = lockedPermissions.getOrDefault(target.getUniqueId(), new HashSet<>());
        List<String> unlockeds = new ArrayList<>();
        for (String perm : possiblePermissions) {
            if (!lockedSet.contains(perm)) {
                unlockeds.add(perm);
            }
        }
        if (unlockeds.isEmpty()) {
            locker.sendMessage(ChatColor.RED + "해당 플레이어에게 잠글 수 있는 권한이 없습니다!");
            return false;
        }

        String randomPerm = unlockeds.get(new Random().nextInt(unlockeds.size()));
        return lockPermission(locker, target, randomPerm);
    }

    // 랜덤 플레이어 선택
    public Player getRandomOpponent(Player locker) {
        // 팀 다르면 상대, 혹은 그냥 전체에서 랜덤
        List<Player> candidates = new ArrayList<>();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (p.equals(locker)) continue;
            // locker와 다른 팀
            if (!plugin.getTeamManager().getTeam(locker).equalsIgnoreCase(plugin.getTeamManager().getTeam(p))) {
                candidates.add(p);
            }
        }
        if (candidates.isEmpty()) return null;
        return candidates.get(new Random().nextInt(candidates.size()));
    }

    // 랜덤 권한 해제(자신의 잠긴 권한 중 하나 해제)
    public boolean unlockRandomPermission(Player player) {
        Set<String> lockedSet = lockedPermissions.getOrDefault(player.getUniqueId(), new HashSet<>());
        if (lockedSet.isEmpty()) {
            player.sendMessage(ChatColor.RED + "잠겨있는 권한이 없습니다.");
            return false;
        }
        List<String> lockedList = new ArrayList<>(lockedSet);
        String randomPerm = lockedList.get(new Random().nextInt(lockedList.size()));

        return unlockPermission(player, randomPerm);
    }

    // 지금 잠겨있는 권한들 반환
    public Set<String> getLockedPermissions(Player player) {
        return lockedPermissions.getOrDefault(player.getUniqueId(), new HashSet<>());
    }

    // 모든 잠금 초기화
    public void resetLocks() {
        lockedPermissions.clear();
    }

}
