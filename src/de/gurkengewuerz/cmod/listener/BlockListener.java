package de.gurkengewuerz.cmod.listener;

import de.gurkengewuerz.cmod.Variables;
import de.gurkengewuerz.cmod.manager.Flags;
import de.gurkengewuerz.cmod.manager.Zone;
import de.gurkengewuerz.cmod.manager.ZoneManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author gurkengewuerz.de
 */
public class BlockListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();

        Zone zone = ZoneManager.getZone(b.getLocation());
        if ((zone != null)) {
            if ((!ZoneManager.checkPermission(zone, p, Flags.PROTECTION))) {
                e.setCancelled(true);
                p.sendMessage(Variables.STRING_PROTECTED);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();

        Zone zone = ZoneManager.getZone(b.getLocation());
        if ((zone != null)) {
            if ((!ZoneManager.checkPermission(zone, p, Flags.PROTECTION))) {
                e.setCancelled(true);
                p.sendMessage(Variables.STRING_PROTECTED);
            }
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent e) {
        Zone zone = ZoneManager.getZone(e.getBlock().getLocation());
        Zone zoneIn = null;

        BlockFace dir = e.getDirection();
        for (Block b : e.getBlocks()) {
            Location loc = b.getRelative(dir).getLocation();
            Zone f = ZoneManager.getZone(loc);
            if (f != null) {
                zoneIn = f;
                break;
            }
        }
        if ((zone != zoneIn) && (zoneIn != null)) {
            if ((zoneIn.hasFlag(Flags.PROTECTION))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent e) {
        Block b = e.getBlock();

        Zone zone = ZoneManager.getZone(e.getBlock().getLocation());
        Zone zoneIn = ZoneManager.getZone(e.getRetractLocation().getBlock().getRelative(e.getDirection()).getLocation());
        if ((b.getType() == Material.PISTON_STICKY_BASE)) {
            if ((zone != zoneIn) && (zoneIn != null)) {
                if ((zoneIn.hasFlag(Flags.PROTECTION))) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent ev) {
        for (LivingEntity e : ev.getAffectedEntities()) {
            String name = e.getType().toString();
            if ((e instanceof Player)) {
                Player p = (Player) e;
                name = p.getName();
            }
            Location loc = e.getLocation();

            Zone zone = ZoneManager.getZone(loc);
            if ((zone != null) && (zone.hasFlag(Flags.PVP))) {
                boolean doCancel = false;
                for (PotionEffect eff : ev.getPotion().getEffects()) {
                    PotionEffectType effType = eff.getType();
                    if ((effType.equals(PotionEffectType.BLINDNESS))
                            || (effType.equals(PotionEffectType.CONFUSION))
                            || (effType.equals(PotionEffectType.HARM))
                            || (effType.equals(PotionEffectType.POISON))
                            || (effType.equals(PotionEffectType.SLOW))
                            || (effType.equals(PotionEffectType.SLOW_DIGGING))
                            || (effType.equals(PotionEffectType.WEAKNESS))) {
                        doCancel = true;
                        break;
                    }
                }
                if (doCancel) {
                    ev.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e) {
        Block b = e.getBlock();
        Zone zone = ZoneManager.getZone(b.getLocation());
        if (zone == null) {
            return;
        }
        if ((!zone.hasFlag(Flags.FIRE)) && (!zone.hasFlag(Flags.PROTECTION))) {
            return;
        }
        Player p = e.getPlayer();
        if (p == null) {
            e.setCancelled(true);
            return;
        }
        if (!ZoneManager.checkPermission(zone, p, Flags.PROTECTION)) {
            e.setCancelled(true);
            p.sendMessage(Variables.STRING_PROTECTED);
        }
    }

    @EventHandler
    public void onHangingingBreak(HangingBreakEvent e) {
        if (e.getCause() == HangingBreakEvent.RemoveCause.PHYSICS) {
            return;
        }
        Zone zone = ZoneManager.getZone(e.getEntity().getLocation());
        if ((zone != null) && (zone.hasFlag(Flags.PROTECTION))) {
            if ((e instanceof HangingBreakByEntityEvent)) {
                HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent) e;
                Entity removerEntity = entityEvent.getRemover();
                if ((removerEntity instanceof Player)) {
                    Player player = (Player) removerEntity;
                    if (!ZoneManager.checkPermission(zone, player, Flags.PROTECTION)) {
                        e.setCancelled(true);
                        player.sendMessage(Variables.STRING_PROTECTED);
                    }
                } else {
                    e.setCancelled(true);
                }
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHangingingBreakByEntity(HangingBreakByEntityEvent e) {
        Zone zone = ZoneManager.getZone(e.getEntity().getLocation());
        if ((zone != null) && (zone.hasFlag(Flags.PROTECTION))) {
            if ((e instanceof HangingBreakByEntityEvent)) {
                HangingBreakByEntityEvent entityEvent = e;
                Entity removerEntity = entityEvent.getRemover();
                if ((removerEntity instanceof Player)) {
                    Player player = (Player) removerEntity;
                    if (!ZoneManager.checkPermission(zone, player, Flags.PROTECTION)) {
                        e.setCancelled(true);
                        player.sendMessage(Variables.STRING_PROTECTED);
                    }
                } else {
                    e.setCancelled(true);
                }
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        Block b = e.getBlock();
        Material mat = b.getType();

        Zone zone = ZoneManager.getZone(b.getLocation());
        if ((zone != null)) {
            if ((zone.hasFlag(Flags.FIRE))) {
                e.setCancelled(true);
            }
        }
    }
}
