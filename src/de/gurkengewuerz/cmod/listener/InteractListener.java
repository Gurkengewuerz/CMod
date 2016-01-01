package de.gurkengewuerz.cmod.listener;

import de.gurkengewuerz.cmod.CMod;
import de.gurkengewuerz.cmod.Variables;
import de.gurkengewuerz.cmod.manager.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author gurkengewuerz.de
 */
public class InteractListener implements Listener {

    @EventHandler
    public static void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getClickedBlock() == null) {
            return;
        }
        Location loc = e.getClickedBlock().getLocation();
        if (p.getItemInHand() == null) {
            return;
        }
        if (p.getItemInHand().getType().equals(Material.WOOD_SPADE)) {
            if (p.hasPermission(Variables.PERMISSION_DEFINE)) {
                if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    Settings sett = Settings.getSett(p);
                    if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        sett.setBorder(2, loc);
                        p.sendMessage(Variables.cmodprefix + ChatColor.DARK_AQUA + "Second Point set.");
                        e.setCancelled(true);
                    }

                    if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        sett.setBorder(1, loc);
                        p.sendMessage(Variables.cmodprefix + ChatColor.DARK_AQUA + "First Point set.");
                        e.setCancelled(true);
                    }
                }
            }
        }
        if (p.getItemInHand().getType().equals(Material.WOOD_SWORD)) {
            if (p.hasPermission(Variables.PERMISSION_CHECK)) {
                    Settings sett = Settings.getSett(p);
                    if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        CMod.sendInfo(p, loc);
                        e.setCancelled(true);
                    }
            }
        }
    }
}
