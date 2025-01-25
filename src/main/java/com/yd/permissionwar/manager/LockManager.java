package com.yd.permissionwar.manager;

import com.yd.permissionwar.PermissionWar;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LockManager {

    private PermissionWar plugin;
    private Map<UUID, Map<String, PermissionLock>> playerPermissions = new ConcurrentHashMap<>();

    public class PermissionLock {
        private boolean isLocked = false;
        private long lockedTimestamp = 0;
        private Player locker = null;

        public PermissionLock() {}

        public boolean isLocked() {
            return isLocked;
        }

        public long getLockedTimestamp() {
            return lockedTimestamp;
        }

        public Player getLocker() {
            return locker;
        }

        public void setLocked(boolean isLocked) {
            this.isLocked = isLocked;
        }

        public void setLockedTimestamp(long lockedTimestamp) {
            this.lockedTimestamp = lockedTimestamp;
        }

        public void setLocker(Player locker) {
            this.locker = locker;
        }
    }

    private List<String> possiblePermissions = Arrays.asList(
            "앉기", "점프", "때리기", "이동하기", "달리기", "NPC상호작용하기", "블럭캐기", "블럭설치하기", "버리기"
    );

    public LockManager(PermissionWar plugin) {
        this.plugin = plugin;
    }

    // 권한 잠그기
    public boolean lockPermission(Player locker, Player target, String permissionName) {
        // 1) config에서 비용 확인
        double cost = plugin.getConfig().getDouble("lock-cost." + permissionName, 10.0);

        // 2) 잔액 부족 시 false 반환
        if (!withdrawMoney(locker, cost)) {
            locker.sendMessage(ChatColor.RED + "돈이 부족합니다! (필요 금액: " + cost + ")");
            return false;
        }

        // 3) 잠금 정보 맵에, target의 UUID가 없으면 새로 넣음
        playerPermissions.putIfAbsent(target.getUniqueId(), new ConcurrentHashMap<>());
        Map<String, PermissionLock> targetPermissions = playerPermissions.get(target.getUniqueId());

        // 4) 해당 권한 이름이 아직 맵에 없으면 새 PermissionLock 생성
        targetPermissions.putIfAbsent(permissionName, new PermissionLock());

        // 5) 실제 잠금 설정
        PermissionLock lock = targetPermissions.get(permissionName);
        lock.setLocked(true);
        lock.setLockedTimestamp(System.currentTimeMillis());
        lock.setLocker(locker);

        // 디버그/로그
        plugin.getLogger().info(String.format("%s's %s permission locked by %s", target.getName(), permissionName, locker.getName()));

        // 달리기 예외 포션효과
        if (permissionName.equalsIgnoreCase("달리기")) {
            target.addPotionEffect(new PotionEffect(
                    PotionEffectType.SLOW,
                    999999,
                    1,
                    false,
                    false
            ));
        }

        // 플레이어 메시지
        locker.sendMessage("상대의 [" + permissionName + "] 권한을 잠갔습니다!");
        target.sendMessage("당신의 [" + permissionName + "] 권한이 잠겼습니다!");

        return true;
    }

    // 권한 해제
    public boolean unlockPermission(Player player, String permissionName) {
        // 1) 플레이어의 잠금 목록 가져오기
        Map<String, PermissionLock> targetPermissions = playerPermissions.get(player.getUniqueId());
        if (targetPermissions == null || !targetPermissions.containsKey(permissionName)) {
            player.sendMessage(ChatColor.RED + "해제할 권한이 잠겨있지 않습니다.");
            return false;
        }

        // 2) config에서 해제 비용 확인
        double cost = plugin.getConfig().getDouble("unlock-cost." + permissionName, 5.0);

        // 3) 잔액 부족 확인
        if (!withdrawMoney(player, cost)) {
            player.sendMessage(ChatColor.RED + "돈이 부족합니다! (필요 금액: " + cost + ")");
            return false;
        }

        // 4) 잠금 해제
        PermissionLock lock = targetPermissions.get(permissionName);
        lock.setLocked(false);

        if (permissionName.equalsIgnoreCase("달리기")) {
            player.removePotionEffect(PotionEffectType.SLOW);
        }

        player.sendMessage("[" + permissionName + "] 권한이 해제되었습니다.");
        return true;
    }

    // 랜덤 권한 잠그기 (타겟 한 명에게)
    public boolean lockRandomPermission(Player locker, Player target) {
        // 1) 타겟의 permission map 확보
        playerPermissions.putIfAbsent(target.getUniqueId(), new ConcurrentHashMap<>());
        Map<String, PermissionLock> targetPermissions = playerPermissions.get(target.getUniqueId());

        // 2) 아직 잠기지 않은 권한들만 추려서 랜덤
        List<String> unlockeds = new ArrayList<>();
        for (String perm : possiblePermissions) {
            if (!targetPermissions.containsKey(perm) || !targetPermissions.get(perm).isLocked()) {
                unlockeds.add(perm);
            }
        }
        // 3) 잠글 수 있는 권한이 없다면 실패
        if (unlockeds.isEmpty()) {
            locker.sendMessage(ChatColor.RED + "해당 플레이어에게 잠글 수 있는 권한이 없습니다!");
            return false;
        }

        // 4) 랜덤 권한 하나 선택 후 lockPermission() 호출
        String randomPerm = unlockeds.get(new Random().nextInt(unlockeds.size()));
        return lockPermission(locker, target, randomPerm);
    }

    // 랜덤 플레이어 선택 (remains the same as before)
    public Player getRandomOpponent(Player locker) {
        List<Player> candidates = new ArrayList<>();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (p.equals(locker)) continue;
            // 팀이 다를 때만 후보에 넣는 예시
            String lockerTeam = plugin.getTeamManager().getTeam(locker);
            String pTeam = plugin.getTeamManager().getTeam(p);
            if (!lockerTeam.equalsIgnoreCase(pTeam)) {
                candidates.add(p);
            }
        }
        if (candidates.isEmpty()) return null;
        return candidates.get(new Random().nextInt(candidates.size()));
    }

    // 랜덤 권한 해제(자신의 잠긴 권한 중 하나 해제)
    public boolean unlockRandomPermission(Player player) {
        playerPermissions.putIfAbsent(player.getUniqueId(), new ConcurrentHashMap<>());
        Map<String, PermissionLock> playerLocks = playerPermissions.get(player.getUniqueId());

        // 잠겨있는 권한들 추출
        List<String> lockedPerms = new ArrayList<>();
        for (Map.Entry<String, PermissionLock> entry : playerLocks.entrySet()) {
            if (entry.getValue().isLocked()) {
                lockedPerms.add(entry.getKey());
            }
        }

        if (lockedPerms.isEmpty()) {
            player.sendMessage(ChatColor.RED + "잠겨있는 권한이 없습니다.");
            return false;
        }

        // 랜덤 한 권한 해제
        String randomPerm = lockedPerms.get(new Random().nextInt(lockedPerms.size()));
        return unlockPermission(player, randomPerm);
    }

    // 지금 잠겨있는 권한들 반환
    public Set<String> getLockedPermissions(Player player) {
        Set<String> lockedPermissions = new HashSet<>();

        if (!playerPermissions.containsKey(player.getUniqueId())) {
            return lockedPermissions;
        }

        Map<String, PermissionLock> playerLocks = playerPermissions.get(player.getUniqueId());
        for (Map.Entry<String, PermissionLock> entry : playerLocks.entrySet()) {
            if (entry.getValue().isLocked()) {
                lockedPermissions.add(entry.getKey());
            }
        }
        return lockedPermissions;
    }

    // 모든 잠금 초기화
    public void resetLocks() {
        for (Map<String, PermissionLock> playerLocks : playerPermissions.values()) {
            for (PermissionLock lock : playerLocks.values()) {
                lock.setLocked(false);
            }
        }
    }

    public boolean isPermissionLocked(Player player, String permissionName) {
        Map<String, PermissionLock> targetPermissions = playerPermissions.get(player.getUniqueId());
        if (targetPermissions == null) return false;

        PermissionLock lock = targetPermissions.get(permissionName);
        return lock != null && lock.isLocked();
    }

    private boolean withdrawMoney(Player player, double amount) {
        if (plugin.getEconomy().getBalance(player) < amount) {
            return false;
        }
        plugin.getEconomy().withdrawPlayer(player, amount);
        return true;
    }

}
