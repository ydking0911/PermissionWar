package com.yd.permissionwar.listener;

import com.yd.permissionwar.PermissionWar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuOpenListener implements Listener {

    private PermissionWar plugin;

    public MenuOpenListener(PermissionWar plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent e) {
        Player player = e.getPlayer();
        if (player.isSneaking()) {
            e.setCancelled(true);
            openMainMenu(player);
        }
    }

    public static void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "등급전쟁");

        ItemStack lockItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta lockMeta = lockItem.getItemMeta();
        lockMeta.setDisplayName(ChatColor.RED + "권한 잠그기");
        lockItem.setItemMeta(lockMeta);
        inv.setItem(11, lockItem);

        ItemStack unlockItem = new ItemStack(Material.MILK_BUCKET);
        ItemMeta unlockMeta = unlockItem.getItemMeta();
        unlockMeta.setDisplayName(ChatColor.GREEN + "권한 해제");
        unlockItem.setItemMeta(unlockMeta);
        inv.setItem(15, unlockItem);

        player.openInventory(inv);
    }

}
