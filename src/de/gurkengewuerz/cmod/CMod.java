package de.gurkengewuerz.cmod;

import de.gurkengewuerz.cmod.commands.cdelCMD;
import de.gurkengewuerz.cmod.commands.cfillCMD;
import de.gurkengewuerz.cmod.commands.cmodCMD;
import de.gurkengewuerz.cmod.commands.cwallsCMD;
import de.gurkengewuerz.cmod.commands.highprotectCMD;
import de.gurkengewuerz.cmod.commands.protectCMD;
import de.gurkengewuerz.cmod.listener.BlockListener;
import de.gurkengewuerz.cmod.listener.EntityListener;
import de.gurkengewuerz.cmod.listener.InteractListener;
import de.gurkengewuerz.cmod.listener.PlayerEvent;
import de.gurkengewuerz.cmod.manager.Flags;
import de.gurkengewuerz.cmod.manager.PluginLoader;
import de.gurkengewuerz.cmod.manager.Zone;
import de.gurkengewuerz.cmod.manager.ZoneManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author gurkengewuerz.de
 */
public class CMod extends JavaPlugin {

    public static String name;
    public static String displayname;
    public static String version;
    public static String description;
    public static CMod instance;

    @Override
    public void onEnable() {
        name = getDescription().getName();
        displayname = getDescription().getFullName();
        version = getDescription().getVersion();
        description = getDescription().getDescription();
        instance = this;
        Variables.dataFolder = CMod.getInstance().getDataFolder() + "/";
        PluginLoader.loadUpdate();
        registerEvents();
        registerCommands();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new InteractListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvent(), this);
        //getServer().getPluginManager().registerEvents(new JoinListener(), this);
    }

    private void registerCommands() {
        getCommand("cmod").setExecutor(new cmodCMD());
        getCommand("cdel").setExecutor(new cdelCMD());
        getCommand("cfill").setExecutor(new cfillCMD());
        getCommand("cwalls").setExecutor(new cwallsCMD());
        getCommand("protect").setExecutor(new protectCMD());
        getCommand("highprotect").setExecutor(new highprotectCMD());
    }

    public static String RainbowString(String str, String ctl) {
        if (ctl.equalsIgnoreCase("x")) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            int idx = i;
            if (idx % 6 == 0) {
                sb.append(ChatColor.RED);
            } else if (idx % 6 == 1) {
                sb.append(ChatColor.GOLD);
            } else if (idx % 6 == 2) {
                sb.append(ChatColor.YELLOW);
            } else if (idx % 6 == 3) {
                sb.append(ChatColor.GREEN);
            } else if (idx % 6 == 4) {
                sb.append(ChatColor.AQUA);
            } else if (idx % 6 == 5) {
                sb.append(ChatColor.LIGHT_PURPLE);
            }
            if (ctl.indexOf('b') >= 0) {
                sb.append(ChatColor.BOLD);
            }
            if (ctl.indexOf('i') >= 0) {
                sb.append(ChatColor.ITALIC);
            }
            if (ctl.indexOf('u') >= 0) {
                sb.append(ChatColor.UNDERLINE);
            }
            sb.append(str.charAt(i));
        }
        return sb.toString();
    }

    public static String GetDelimList(String[] arr, String delim) {
        StringBuilder buf = new StringBuilder();
        String[] arrayOfString = arr;
        int j = arr.length;
        for (int i = 0; i < j; i++) {
            String str = arrayOfString[i];
            if (buf.length() > 0) {
                buf.append(delim);
            }
            buf.append(str);
        }
        return buf.toString();
    }

    public static String LocString(Location loc) {
        if (loc == null) {
            return "NULL";
        }
        return loc.getWorld().getName() + " (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
    }

    public static void sendInfo(Player p, Location loc) {
        Zone zone = ZoneManager.getZone(loc);
        if (zone != null) {
            SendZoneInfo(p, zone);

            ArrayList<Zone> zones = ZoneManager.getZones(loc);
            if (zones.size() > 1) {
                StringBuilder sb = new StringBuilder();
                String zoneName = zone.getName();
                for (Zone z : zones) {
                    String overlapZoneName = z.getName();
                    if (!overlapZoneName.equalsIgnoreCase(zoneName)) {
                        if (sb.length() > 0) {
                            sb.append(", ");
                        }
                        sb.append(overlapZoneName);
                    }
                }
                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Überlappende Zone(n): " + ChatColor.GOLD + sb.toString());
            }
        } else {
            p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Keine Zone gefunden" + ChatColor.WHITE + " @ " + LocString(loc));
        }
    }

    public static void SendZoneInfo(Player p, Zone zone) {
        StringBuilder sbFlags = new StringBuilder();
        for (Flags flag : zone.getFlags()) {
            if (sbFlags.length() > 0) {
                sbFlags.append(", ");
            }
            sbFlags.append(flag.getName());
        }
        String flags = sbFlags.toString();

        String allowed = zone.getAllowed().toString();

        Location loc1 = zone.getBorder1();
        Location loc2 = zone.getBorder2();

        p.sendMessage(Variables.cmodprefix + ChatColor.AQUA + "=== " + ChatColor.GREEN + "Zone(n) Infos " + ChatColor.AQUA + " ===");
        p.sendMessage(ChatColor.GRAY + "Zonen Name: " + ChatColor.LIGHT_PURPLE + zone.getName() + ChatColor.GRAY + " (DX=" + Math.abs(loc1.getBlockX() - loc2.getBlockX()) + ", DY=" + Math.abs(loc1.getBlockY() - loc2.getBlockY()) + ", DZ=" + Math.abs(loc1.getBlockZ() - loc2.getBlockZ()) + "), " + ChatColor.GRAY + "Reihenfolge: " + ChatColor.YELLOW + zone.getOrder());
        p.sendMessage(ChatColor.GRAY + "Flags: " + ChatColor.YELLOW + flags);
        p.sendMessage(ChatColor.GRAY + "Member: " + ChatColor.YELLOW + allowed);
        p.sendMessage(ChatColor.GRAY + "Erstellt am: " + ChatColor.YELLOW + getRealDate(zone.getCreationDate()));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + p.getName() + " ['',{'text':'Löschen?: ','color':'gray'},{'text':'Lösche die Region!','color':'red','clickEvent':{'action':'run_command','value':'/cmod delete " + zone.getName() + "'},'hoverEvent':{'action':'show_text','value':{'text':'','extra':[{'text':'Lösche die Region " + zone.getName() + "','color':'gold'}]}}}]");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + p.getName() + " ['',{'text':'Teleport: ','color':'gray'},{'text':'Klick mich!','color':'white','clickEvent':{'action':'run_command','value':'/cmod tp " + zone.getName() + "'},'hoverEvent':{'action':'show_text','value':{'text':'','extra':[{'text':'Teleportiere dich zu " + LocString(loc1) + "','color':'gold'}]}}}]");
    }

    public static String getRealDate(Long timestamp) {
        if (timestamp < 0L) {
            timestamp = 0L;
        }
        Date d = new Date(timestamp);
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = DATE_FORMAT.format(d);
        return date;
    }

    public static CMod getInstance() {
        return instance;
    }
}
