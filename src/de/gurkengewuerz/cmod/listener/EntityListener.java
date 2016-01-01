package de.gurkengewuerz.cmod.listener;

import de.gurkengewuerz.cmod.Variables;
import de.gurkengewuerz.cmod.manager.Flags;
import de.gurkengewuerz.cmod.manager.Zone;
import de.gurkengewuerz.cmod.manager.ZoneManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 *
 * @author gurkengewuerz.de
 */
public class EntityListener implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        Entity entity = e.getEntity();
        if (entity == null) {
            return;
        }
        Location loc = e.getLocation();
        if (loc == null) {
            return;
        }
        Zone zone = ZoneManager.getZone(loc);
        if (zone != null) {
            if ((zone.hasFlag(Flags.EXPLOSION)) || (zone.hasFlag(Flags.PROTECTION)) || (((entity instanceof Creeper)) && (zone.hasFlag(Flags.CREEPER))) || (((entity instanceof TNTPrimed)) && (zone.hasFlag(Flags.TNT)))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if ((e.isCancelled()) || (e.getEntityType() != EntityType.ENDERMAN)) {
            return;
        }
        Block b = e.getBlock();
        Zone zone = ZoneManager.getZone(b.getLocation());
        if ((zone != null) && (zone.hasFlag(Flags.PROTECTION))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        Entity defender = e.getEntity();
        Zone zone = ZoneManager.getZone(defender.getLocation());
        if (zone != null) {
            if ((e instanceof EntityDamageByEntityEvent)) {
                Entity damager = ((EntityDamageByEntityEvent) e).getDamager();
                if (((defender instanceof ItemFrame)) && (zone.hasFlag(Flags.PROTECTION))) {
                    boolean damagerIsOp = false;
                    if ((damager instanceof Player)) {
                        Player pDmg = (Player) damager;
                        damagerIsOp = ZoneManager.checkPermission(zone, pDmg, Flags.PROTECTION);
                    }
                    if (!damagerIsOp) {
                        e.setCancelled(true);
                        return;
                    }
                }
                if (((damager instanceof Snowball)) && (zone.hasFlag(Flags.PVP))) {
                    e.setCancelled(true);
                    return;
                }
                if (((damager instanceof FishHook)) && (zone.hasFlag(Flags.PVP))) {
                    e.setCancelled(true);
                    return;
                }
                if (((damager instanceof Egg)) && (zone.hasFlag(Flags.PVP))) {
                    e.setCancelled(true);
                    return;
                }
                if (((damager instanceof EnderPearl)) && (zone.hasFlag(Flags.PVP))) {
                    EnderPearl ep = (EnderPearl) damager;
                    if (ep.getShooter() != defender) {
                        e.setCancelled(true);
                    }
                }
            }
            if (((defender instanceof Player)) && (zone.hasFlag(Flags.GODMODE))) {
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onPaintingBreak(PaintingBreakEvent e) {
        Painting painting = e.getPainting();
        if ((e.getCause() == PaintingBreakEvent.RemoveCause.ENTITY) && (((PaintingBreakByEntityEvent) e).getRemover().getType() == EntityType.PLAYER)) {
            Zone zone = ZoneManager.getZone(painting.getLocation());
            if (zone != null) {
                Player p = (Player) ((PaintingBreakByEntityEvent) e).getRemover();
                if (!ZoneManager.checkPermission(zone, p, Flags.PROTECTION)) {
                    e.setCancelled(true);
                    p.sendMessage(Variables.STRING_PROTECTED);
                }
            }
        }
    }

    @EventHandler
    public void onPaintingPlace(PaintingPlaceEvent e) {
        Player p = e.getPlayer();
        Painting painting = e.getPainting();

        Zone zone = ZoneManager.getZone(painting.getLocation());
        if ((zone != null) && (!ZoneManager.checkPermission(zone, p, Flags.PROTECTION))) {
            e.setCancelled(true);
            p.sendMessage(Variables.STRING_PROTECTED);
        }
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent e) {
        Player p = e.getPlayer();
        Location loc = e.getBlock().getLocation();

        Zone zone = ZoneManager.getZone(loc);
        if ((zone != null) && (!ZoneManager.checkPermission(zone, p, Flags.PROTECTION))) {
            e.setCancelled(true);
            p.sendMessage(Variables.STRING_PROTECTED);
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        Entity entity = e.getRightClicked();
        if ((p == null) || (entity == null)) {
            return;
        }
        if (((entity instanceof Painting)) || ((entity instanceof ItemFrame))) {
            Zone zone = ZoneManager.getZone(entity.getLocation());
            if ((zone != null) && (!ZoneManager.checkPermission(zone, p, Flags.PROTECTION))) {
                e.setCancelled(true);
                p.sendMessage(Variables.STRING_PROTECTED);
            }
        }
    }
}
