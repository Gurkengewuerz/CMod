package de.gurkengewuerz.cmod.manager;

import de.gurkengewuerz.cmod.Variables;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author gurkengewuerz.de
 */
public class ZoneManager {

    private static ArrayList<Zone> Zones = new ArrayList();

    public static ArrayList<Zone> getZones() {
        return Zones;
    }

    public static Zone add(String a, Location[] c) {
        Zone zone = new Zone(a);
        zone.setSave(true);

        zone.setBorder(1, c[0]);
        zone.setBorder(2, c[1]);
        zone.setCreationDate(System.currentTimeMillis());
        Zones.add(zone);
        return zone;
    }

    public static void add(Zone a) {
        Zones.add(a);
    }

    public static void delete(Zone a) {
        Zones.remove(a);
    }

    public static Zone getZone(String a) {
        for (Zone b : Zones) {
            if (b.getName().equalsIgnoreCase(a)) {
                return b;
            }
        }
        return null;
    }

    public static Zone getZone(Location a) {
        int X = (int) a.getX();
        int Y = (int) a.getY();
        int Z = (int) a.getZ();

        Integer lastZOrder = Integer.MIN_VALUE;
        Zone foundZone = null;
        for (Zone zone : Zones) {
            if (zone.getWorld() == a.getWorld()) {
                if ((X >= zone.getBorder1().getX())
                        && (X <= zone.getBorder2().getX())
                        && (Y >= zone.getBorder1().getY())
                        && (Y <= zone.getBorder2().getY())
                        && (Z >= zone.getBorder1().getZ())
                        && (Z <= zone.getBorder2().getZ())) {
                    int thisZOrder = zone.getOrder();
                    if (thisZOrder > lastZOrder) {
                        lastZOrder = thisZOrder;
                        foundZone = zone;
                    }
                }
            }
        }
        return foundZone;
    }

    public static ArrayList<Zone> getZones(Location loc) {
        int X = (int) loc.getX();
        int Y = (int) loc.getY();
        int Z = (int) loc.getZ();

        ArrayList<Zone> found = new ArrayList();
        for (Zone zone : Zones) {
            if (zone.getWorld() == loc.getWorld()) {
                if ((X >= zone.getBorder1().getX())
                        && (X <= zone.getBorder2().getX())
                        && (Y >= zone.getBorder1().getY())
                        && (Y <= zone.getBorder2().getY())
                        && (Z >= zone.getBorder1().getZ())
                        && (Z <= zone.getBorder2().getZ())) {
                    found.add(zone);
                }
            }
        }
        return found;
    }

    public static Zone getZone(World world, int x, int y, int z) {
        return getZone(new Location(world, x, y, z));
    }

    public static boolean checkPermission(Zone zone, Player player, Flags flag) {
        if (checkGeneral(zone, player)) {
            return true;
        }
        return !zone.hasFlag(flag);
    }

    private static boolean checkGeneral(Zone zone, Player player) {
        if (player.hasPermission(Variables.PERMISSION_OWNER_IGNORE)) {
            return true;
        }
        if (zone.getOwners().contains(player.getName())) {
            return true;
        }
        return zone.getAllowed().contains(player.getName());
    }

    public static String canBuildZone(Player p, Location[] border) {
        if (checkOwnsPermission(p)) {
            return "max";
        }
        return checkSizePermission(p, border);
    }

    public static boolean checkOwnsPermission(Player p) {
        Settings sett = Settings.getSett(p);

        return (sett.getOwnedZones() >= -1);
    }

    public static String checkSizePermission(Player p, Location[] border) {
        Settings sett = Settings.getSett(p);

        Location min = new Location(border[0].getWorld(), Math.min(border[0].getX(), border[1].getX()),
                Math.min(border[0].getY(), border[1].getY()), Math.min(border[0].getZ(), border[1].getZ()));
        Location max = new Location(border[1].getWorld(), Math.max(border[0].getX(), border[1].getX()),
                Math.max(border[0].getY(), border[1].getY()), Math.max(border[0].getZ(), border[1].getZ()));

        int sizeX = max.getBlockX() - min.getBlockX() + 1;
        int sizeZ = max.getBlockZ() - min.getBlockZ() + 1;
        int sizeY = max.getBlockY() - min.getBlockY() + 1;
        Vector maxSize = new Vector(-1, -1, -1);
        if (((sizeX > maxSize.getX()) && (maxSize.getX() != -1.0D))
                || ((sizeZ > maxSize.getZ()) && (maxSize.getZ() != -1.0D)) || ((sizeY > maxSize.getY()) && (maxSize.getY() != -1.0D))) {
            return "size:(" + sizeX + ", " + sizeY + ", " + sizeZ + ")";
        }
        return "";
    }
}
