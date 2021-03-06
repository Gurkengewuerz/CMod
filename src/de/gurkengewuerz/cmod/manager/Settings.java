package de.gurkengewuerz.cmod.manager;

import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author gurkengewuerz.de
 */
public class Settings {

    private Player p;
    private Location border1;
    private Location border2;
    private static HashMap<String, Settings> setts = new HashMap();

    public Settings(Player arg0, Location arg1, Location arg2) {
        this.p = arg0;
        this.border1 = arg1;
        this.border2 = arg2;
    }

    public static Settings getSett(Player p) {
        if (!setts.containsKey(p.getName())) {
            Settings sett = new Settings(p, null, null);
            setts.put(p.getName(), sett);
            return sett;
        }
        Settings sett = (Settings) setts.get(p.getName());
        sett.setPlayer(p);
        return sett;
    }

    public Player getPlayer() {
        return this.p;
    }

    public Location getBorder1() {
        if ((this.border1 != null) && (this.border2 != null)) {
            World world = this.border1.getWorld();
            int x0 = this.border1.getBlockX();
            int y0 = this.border1.getBlockY();
            int z0 = this.border1.getBlockZ();

            int x1 = this.border2.getBlockX();
            int y1 = this.border2.getBlockY();
            int z1 = this.border2.getBlockZ();

            return new Location(world, Math.min(x0, x1), Math.min(y0, y1), Math.min(z0, z1));
        }
        return this.border1;
    }

    public Location getBorder2() {
        if ((this.border1 != null) && (this.border2 != null)) {
            int x0 = this.border1.getBlockX();
            int y0 = this.border1.getBlockY();
            int z0 = this.border1.getBlockZ();

            World world = this.border2.getWorld();
            int x1 = this.border2.getBlockX();
            int y1 = this.border2.getBlockY();
            int z1 = this.border2.getBlockZ();

            return new Location(world, Math.max(x0, x1), Math.max(y0, y1), Math.max(z0, z1));
        }
        return this.border2;
    }

    public int getOwnedZones() {
        int owned = 0;
        Iterator localIterator2;
        for (Iterator localIterator1 = ZoneManager.getZones().iterator(); localIterator1.hasNext(); localIterator2.hasNext()) {
            Zone zone = (Zone) localIterator1.next();
            localIterator2 = zone.getOwners().iterator();
            String s = (String) localIterator2.next();
            if (s.equalsIgnoreCase(this.p.getName())) {
                owned++;
            }
        }
        return owned;
    }

    public void setPlayer(Player a) {
        this.p = a;
    }

    public void setBorder(int a, Location b) {
        if (a == 1) {
            this.border1 = b;
        } else if (a == 2) {
            this.border2 = b;
        }
    }
}
