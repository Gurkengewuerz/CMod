package de.gurkengewuerz.cmod.listener;

import de.gurkengewuerz.cmod.Variables;
import de.gurkengewuerz.cmod.manager.Flags;
import de.gurkengewuerz.cmod.manager.Zone;
import de.gurkengewuerz.cmod.manager.ZoneManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author gurkengewuerz.de
 */
public class PlayerEvent implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (e.getPlayer().isDead()) {
            return;
        }
        Location f = e.getFrom();
        Location t = e.getTo();
        if (f.getBlockX() == t.getBlockX() && f.getBlockY() == t.getBlockY() && f.getBlockZ() == t.getBlockZ()) {
            return;
        }
        Zone fzone = ZoneManager.getZone(f);
        Zone tzone = ZoneManager.getZone(t);
        if (tzone != null) {
            if (tzone.hasFlag(Flags.WELCOME)) {
                if (ZoneManager.checkPermission(tzone, p, Flags.RESTRICTION)) {
                    if (tzone != fzone) {
                        String s = tzone.getWelcome();
                        sendWFM(p, s);
                    }
                }
            }
            if (tzone.hasFlag(Flags.RESTRICTION)) {
                if (!ZoneManager.checkPermission(tzone, p, Flags.RESTRICTION)) {
                    p.setVelocity(t.getDirection().normalize().multiply(-3).add(t.getDirection()));
                }
            }
        }

        if (tzone == null) {
            if (fzone != null) {
                if (fzone.hasFlag(Flags.FAREWELL)) {
                    if (ZoneManager.checkPermission(fzone, p, Flags.RESTRICTION)) {
                        String s = fzone.getFarewell();
                        sendWFM(p, s);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getClickedBlock() == null) {
            return;
        }
        Location loc = e.getClickedBlock().getLocation();
        if (p.getItemInHand() == null) {
            return;
        }
        Zone zone = ZoneManager.getZone(loc);
        if ((zone != null) && (!ZoneManager.checkPermission(zone, p, Flags.INTERACT))) {
            e.setCancelled(true);
            p.sendMessage(Variables.STRING_PROTECTED);
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlockClicked().getRelative(e.getBlockFace());

        Zone zone = ZoneManager.getZone(b.getLocation());
        if ((zone != null) && (!ZoneManager.checkPermission(zone, p, Flags.PROTECTION))) {
            e.setCancelled(true);
            p.sendMessage(Variables.STRING_PROTECTED);
        }
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlockClicked().getRelative(e.getBlockFace());

        Zone zone = ZoneManager.getZone(b.getLocation());
        if ((zone != null) && (!ZoneManager.checkPermission(zone, p, Flags.PROTECTION))) {
            e.setCancelled(true);
            p.sendMessage(Variables.STRING_PROTECTED);
        }
    }

    private void sendWFM(Player p, String s) {
        p.sendMessage(ChatColor.DARK_GRAY + "> " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', s));
    }
}
