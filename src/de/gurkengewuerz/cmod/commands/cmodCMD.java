package de.gurkengewuerz.cmod.commands;

import com.google.common.base.Splitter;
import de.gurkengewuerz.cmod.CMod;
import de.gurkengewuerz.cmod.Variables;
import de.gurkengewuerz.cmod.manager.Flags;
import de.gurkengewuerz.cmod.manager.Settings;
import de.gurkengewuerz.cmod.manager.Zone;
import de.gurkengewuerz.cmod.manager.ZoneManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 *
 * @author gurkengewuerz.de
 */
public class cmodCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String string, String[] args) {
        if (cmd.getName().equalsIgnoreCase("cmod")) {
            if (cs instanceof Player) {
                Player p = (Player) cs;
                if (args.length == 0) {
                    p.sendMessage(ChatColor.GOLD + "Original CMod by robin0van0der0v! ");
                    p.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Revision " + CMod.version + " by " + "Gurkengewuerz");
                }
                if (args.length >= 1) {
                    Settings setting = Settings.getSett(p);
                    switch (args[0].toLowerCase()) {
                        case "create":
                            if (!p.hasPermission(Variables.PERMISSION_CREATE)) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Dir Fehlen die Permissions");
                                return true;
                            }
                            Settings settings = Settings.getSett(p);
                            if (args.length == 2) {
                                String name = args[1];
                                if (ZoneManager.getZone(name) == null) {
                                    Location[] c = {settings.getBorder1(), settings.getBorder2()};
                                    if ((c[0] == null) || (c[1] == null)) {
                                        p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Setzte deine Region zuerst");
                                        return true;
                                    }
                                    int y1 = (int) c[0].getY();
                                    int y2 = (int) c[1].getY();
                                    Zone zone = ZoneManager.add(name, c);
                                    zone.Add("o:" + p.getName());

                                    CMod.SendZoneInfo(p, zone);
                                } else {
                                    p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Die Zone mit dem Namen '" + name + "' existiert bereits");
                                }
                            } else {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "/cmod create <Zone>");
                            }

                            break;

                        case "delete":
                            if (!p.hasPermission(Variables.PERMISSION_DEFINE)) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Dir Fehlen die Permissions");
                                return true;
                            }
                            if (args.length == 2) {
                                String name = args[1];
                                if (ZoneManager.getZone(name) != null) {
                                    Zone zone = ZoneManager.getZone(name);
                                    ZoneManager.delete(zone);
                                    File file = zone.getSaveFile();
                                    file.delete();
                                    p.sendMessage(Variables.cmodprefix + ChatColor.AQUA + "Die Zone " + ChatColor.GOLD + zone.getName() + ChatColor.AQUA + " wurde gelöscht");
                                } else {
                                    p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Die angegebende Zone exisiert nicht!");
                                }
                            } else {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "/cmod delete <Zone>");
                            }
                            break;

                        case "allow":
                            if (!p.hasPermission(Variables.PERMISSION_ALLOW)) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Dir Fehlen die Permissions");
                                return true;
                            }
                            if (args.length == 3) {
                                String name = args[1];
                                String player = args[2];
                                if (ZoneManager.getZone(name) != null) {
                                    Zone zone = ZoneManager.getZone(name);
                                    if (!zone.getAllowed().contains(player)) {
                                        zone.Add(player);
                                        p.sendMessage(Variables.cmodprefix + ChatColor.AQUA + "Der Spieler " + ChatColor.WHITE + player + ChatColor.AQUA + " wurde zur Zone " + ChatColor.GOLD + zone.getName() + ChatColor.AQUA + " hinzugefügt");
                                    } else {
                                        p.sendMessage(Variables.cmodprefix + ChatColor.AQUA + "Der Spieler ist bereits in der Region Member");
                                    }
                                } else {
                                    p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Diese Zone existiert nicht");
                                }
                            } else {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "/cmod allow <Zone> <Spieler>");
                            }

                            break;

                        case "disallow":
                            if (!p.hasPermission(Variables.PERMISSION_DISALLOW)) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Dir Fehlen die Permissions");
                                return true;
                            }
                            if (args.length == 3) {
                                String zoneName = args[1];
                                String target = args[2];
                                if (ZoneManager.getZone(zoneName) != null) {
                                    Zone zone = ZoneManager.getZone(zoneName);
                                    if (zone.getAllowed().contains(target)) {
                                        zone.Remove(target);
                                        p.sendMessage(Variables.cmodprefix + ChatColor.AQUA + "Der Spieler " + ChatColor.WHITE + target + ChatColor.AQUA + " wurde zur Zone " + ChatColor.GOLD + zone.getName() + ChatColor.AQUA + " entfernt");
                                    } else {
                                        p.sendMessage(Variables.cmodprefix + ChatColor.AQUA + "Der Spieler ist kein Member von der Region");
                                    }
                                } else {
                                    p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Diese Zone existiert nicht");
                                }
                            } else {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "/cmod disallow <Zone> <Spieler>");
                            }
                            break;

                        case "change":
                            if (!p.hasPermission(Variables.PERMISSION_OWNER_CHANGE)) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Dir Fehlen die Permissions");
                                return true;
                            }
                            if (args.length == 3) {
                                String name = args[1];
                                String player = args[2];
                                if (ZoneManager.getZone(name) != null) {
                                    Zone zone = ZoneManager.getZone(name);
                                    if (!zone.getAllowed().contains(player)) {
                                        if (zone.changeOwner(player)) {
                                            p.sendMessage(Variables.cmodprefix + ChatColor.AQUA + "Der Spieler " + ChatColor.WHITE + player + ChatColor.AQUA + " wurde in der Zone " + ChatColor.GOLD + zone.getName() + ChatColor.AQUA + " als Inhaber gesetzt");
                                        } else {
                                            p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Fehler");
                                        }
                                    } else {
                                        p.sendMessage(Variables.cmodprefix + ChatColor.AQUA + "Der Spieler ist bereits in der Region Member, entferne ihn zuerst");
                                    }
                                } else {
                                    p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Diese Zone existiert nicht");
                                }
                            } else {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "/cmod change <Zone> <Spieler>");
                            }

                            break;

                        case "list":
                            if (!p.hasPermission(Variables.PERMISSION_LIST)) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Dir Fehlen die Permissions");
                                return true;
                            }
                            ArrayList<Zone> zones = ZoneManager.getZones();
                            String list = "";
                            int countid = 1;
                            for (Zone zone : zones) {
                                if (countid == zones.size()) {
                                    list = list + "{\"text\":\"" + zone.getName() + "\",\"color\":\"dark_aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/cmod info " + zone.getName() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Bekomme Infos zu " + zone.getName() + "\",\"color\":\"gold\"}]}}}";
                                } else {
                                    list = list + "{\"text\":\"" + zone.getName() + "\",\"color\":\"dark_aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/cmod info " + zone.getName() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Bekomme Infos zu " + zone.getName() + "\",\"color\":\"gold\"}]}}},{'text':', ','color':'aqua'},";
                                }
                                countid++;
                            }
                            p.sendMessage(Variables.cmodprefix + ChatColor.GREEN + "=== " + ChatColor.AQUA + "Zonen (" + ChatColor.WHITE + zones.size() + ChatColor.AQUA + ")" + ChatColor.GREEN + " ===");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + p.getName() + " [\"\"," + list + "]");
                            break;

                        case "flag":
                            if (!p.hasPermission(Variables.PERMISSION_FLAG)) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Dir Fehlen die Permissions");
                                return true;
                            }
                            boolean foundFlag = false;
                            if (args.length >= 3) {
                                String name = args[1];
                                String flag = args[2];
                                if (ZoneManager.getZone(name) != null) {
                                    Zone zone = ZoneManager.getZone(name);
                                    String text = "";
                                    if (args.length >= 3) {
                                        for (int i = 3; i < args.length; i++) {
                                            text = text + args[i] + " ";
                                        }
                                        if (text.endsWith(" ")) {
                                            text = text.substring(0, text.length() - 1);
                                        }
                                    }
                                    foundFlag = false;
                                    Flags[] arrayOfFlags;
                                    int j = (arrayOfFlags = Flags.values()).length;
                                    for (int i = 0; i < j; i++) {
                                        Flags flag2 = arrayOfFlags[i];
                                        String Sflag = String.valueOf(flag2);
                                        if (!foundFlag) {
                                            if (Sflag.equalsIgnoreCase(flag)) {
                                                if (text.length() > 0) {
                                                    if (Sflag.equalsIgnoreCase("WELCOME")) {
                                                        zone.setWelcome(text);
                                                        p.sendMessage(Variables.cmodprefix + ChatColor.WHITE + flag2.getName() + ChatColor.AQUA + " Text wurde geändert");
                                                        foundFlag = true;
                                                    }
                                                    if (Sflag.equalsIgnoreCase("FAREWELL")) {
                                                        zone.setFarewell(text);
                                                        p.sendMessage(Variables.cmodprefix + ChatColor.WHITE + flag2.getName() + ChatColor.AQUA + " Text wurde geändert");
                                                        foundFlag = true;
                                                    }
                                                } else {
                                                    zone.setFlag(flag2.getId(), !zone.hasFlag(flag2));
                                                    if (zone.hasFlag(flag2)) {
                                                        p.sendMessage(Variables.cmodprefix + "Flag " + ChatColor.WHITE + flag2.getName() + ChatColor.AQUA + " wurde " + ChatColor.GREEN + "angeschaltet");
                                                        foundFlag = true;
                                                    } else {
                                                        p.sendMessage(Variables.cmodprefix + "Flag " + ChatColor.WHITE + flag2.getName() + ChatColor.AQUA + " wurde " + ChatColor.RED + "ausgeschaltet");
                                                        foundFlag = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Die Zone exisitert nicht");
                                }
                                if (!foundFlag) {
                                    p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Unbekannte Flag: " + ChatColor.AQUA + flag);
                                    p.sendMessage(Variables.cmodprefix + ChatColor.AQUA + "Bekannte Flags: " + ChatColor.WHITE + "protection, welcome, farewell, pvp, creeper, tnt, explosion, fire, restriction, godmode und interact");
                                }
                            } else {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "/cmod flag <Zone> <Flag>");
                            }
                            break;

                        case "expand":
                            if (!p.hasPermission(Variables.PERMISSION_EXPAND)) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Dir Fehlen die Permissions");
                                return true;
                            }
                            if (args.length == 3) {
                                BlockFace dir;
                                if (setting.getBorder1() == null || setting.getBorder2() == null) {
                                    p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Setzte voher deine Punkte");
                                    return true;
                                }
                                Location[] newerBorders = {setting.getBorder1(), setting.getBorder2()};
                                Integer size;
                                try {
                                    size = Integer.parseInt(args[1]);
                                } catch (Exception e) {
                                    p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Es muss eine Zahl angegeben werden.");
                                    return true;
                                }

                                if (args[2].equalsIgnoreCase("up")) {
                                    dir = BlockFace.UP;
                                } else if (args[2].equalsIgnoreCase("down")) {
                                    dir = BlockFace.DOWN;
                                } else if (args[2].equalsIgnoreCase("north")) {
                                    dir = BlockFace.NORTH;
                                } else if (args[2].equalsIgnoreCase("east")) {
                                    dir = BlockFace.EAST;
                                } else if (args[2].equalsIgnoreCase("south")) {
                                    dir = BlockFace.SOUTH;
                                } else if (args[2].equalsIgnoreCase("west")) {
                                    dir = BlockFace.WEST;
                                } else {
                                    p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Keine richtige Richtung angegeben: " + ChatColor.WHITE + "up, down, north, east, south, west");
                                    return true;
                                }
                                if (dir == BlockFace.UP) {
                                    int i = setting.getBorder2().getBlockY() + size;
                                    if (i > setting.getBorder2().getWorld().getMaxHeight()) {
                                        i = setting.getBorder2().getWorld().getMaxHeight();
                                        size = size - (i - setting.getBorder2().getWorld().getMaxHeight());
                                    }
                                    Location cur = setting.getBorder2().clone();
                                    cur.setY(i);
                                    newerBorders[1] = cur;
                                } else if (dir == BlockFace.DOWN) {
                                    int i = setting.getBorder1().getBlockY() - size;
                                    if (i < 0) {
                                        i = 0;
                                        size = size - Math.abs(i);
                                    }
                                    Location cur = setting.getBorder1().clone();
                                    cur.setY(i);
                                    newerBorders[0] = cur;
                                } else if (dir == BlockFace.NORTH) {
                                    int i = setting.getBorder1().getBlockX() - size;
                                    Location cur = setting.getBorder1().clone();
                                    cur.setX(i);
                                    newerBorders[0] = cur;
                                } else if (dir == BlockFace.EAST) {
                                    int i = setting.getBorder1().getBlockZ() - size;
                                    Location cur = setting.getBorder1().clone();
                                    cur.setZ(i);
                                    newerBorders[0] = cur;
                                } else if (dir == BlockFace.SOUTH) {
                                    int i = setting.getBorder2().getBlockX() + size;
                                    Location cur = setting.getBorder2().clone();
                                    cur.setX(i);
                                    newerBorders[1] = cur;
                                } else if (dir == BlockFace.WEST) {
                                    int i = setting.getBorder2().getBlockZ() + size;
                                    Location cur = setting.getBorder2().clone();
                                    cur.setZ(i);
                                    newerBorders[1] = cur;
                                }
                                setting.setBorder(1, newerBorders[0]);
                                setting.setBorder(2, newerBorders[1]);
                                p.sendMessage(Variables.cmodprefix + ChatColor.AQUA + "Expanded " + ChatColor.WHITE + size + " " + dir);
                            } else {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "/cmod expand <Größe> <Richtung>");
                            }
                            break;

                        case "fill":
                            if (setting.getBorder1() == null || setting.getBorder2() == null) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Setzte voher deine Punkte");
                                return true;
                            }
                            if (args.length == 2) {
                                MaterialData finalMatData = parseMatData(args[1], ":");
                                if (finalMatData == null) {
                                    p.sendMessage(ChatColor.RED + "Falsche Eingabe: " + ChatColor.DARK_PURPLE + args[1] + ChatColor.RED + ", Format: <MaterialName>[:data]");
                                    return true;
                                }
                                ArrayList<Block> blocklist = new ArrayList<>();
                                int minX = setting.getBorder1().getBlockX();
                                int minY = setting.getBorder1().getBlockY();
                                int minZ = setting.getBorder1().getBlockZ();
                                int maxX = setting.getBorder2().getBlockX();
                                int maxY = setting.getBorder2().getBlockY();
                                int maxZ = setting.getBorder2().getBlockZ();
                                World w = setting.getBorder1().getWorld();
                                for (int x = minX; x <= maxX; x++) {
                                    for (int z = minZ; z <= maxZ; z++) {
                                        for (int y = minY; y <= maxY; y++) {
                                            blocklist.add(w.getBlockAt(x, y, z));
                                        }
                                    }
                                }
                                blocklist.stream().forEach(b -> {
                                    b.setType(finalMatData.getItemType());
                                    b.setData(finalMatData.getData());
                                });
                                p.sendMessage(Variables.cmodprefix + ChatColor.WHITE + blocklist.size() + ChatColor.AQUA + " Blöcke wurden ersetzt");
                            } else {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "/cmod fill <Material:Data>");
                            }
                            break;

                        case "replace":
                            if (setting.getBorder1() == null || setting.getBorder2() == null) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Setzte voher deine Punkte");
                                return true;
                            }
                            if (args.length == 3) {
                                MaterialData finalMatData = parseMatData(args[2], ":");
                                if (finalMatData == null) {
                                    p.sendMessage(ChatColor.RED + "Falsche Eingabe: " + ChatColor.DARK_PURPLE + args[2] + ChatColor.RED + ", Format: <MaterialName>[:data]");
                                    return true;
                                }
                                MaterialData oldMatData = parseMatData(args[1], ":");
                                if (oldMatData == null) {
                                    p.sendMessage(ChatColor.RED + "Falsche Eingabe: " + ChatColor.DARK_PURPLE + args[1] + ChatColor.RED + ", Format: <MaterialName>[:data]");
                                    return true;
                                }
                                ArrayList<Block> blocklist = new ArrayList<>();
                                int minX = setting.getBorder1().getBlockX();
                                int minY = setting.getBorder1().getBlockY();
                                int minZ = setting.getBorder1().getBlockZ();
                                int maxX = setting.getBorder2().getBlockX();
                                int maxY = setting.getBorder2().getBlockY();
                                int maxZ = setting.getBorder2().getBlockZ();
                                World w = setting.getBorder1().getWorld();
                                for (int x = minX; x <= maxX; x++) {
                                    for (int z = minZ; z <= maxZ; z++) {
                                        for (int y = minY; y <= maxY; y++) {
                                            blocklist.add(w.getBlockAt(x, y, z));
                                        }
                                    }
                                }
                                final int[] count = {0};
                                blocklist.stream().forEach(b -> {
                                    if (b.getType().equals(oldMatData.getItemType())) {
                                        //if (b.getData() == oldMatData.getData()) {
                                            count[0]++;
                                            b.setType(finalMatData.getItemType());
                                            b.setData(finalMatData.getData());
                                        //}
                                    }
                                });
                                p.sendMessage(Variables.cmodprefix + ChatColor.WHITE + count[0] + ChatColor.AQUA + " Blöcke wurden ersetzt");
                            } else {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "/cmod replace <Material:Data> <Material:Data>");
                            }
                            break;

                        case "walls":
                            if (setting.getBorder1() == null || setting.getBorder2() == null) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Setzte voher deine Punkte");
                                return true;
                            }
                            if (args.length == 2) {
                                MaterialData fillMatData = parseMatData(args[1], ":");
                                if (fillMatData == null) {
                                    p.sendMessage(ChatColor.RED + "Falsche Eingabe: " + ChatColor.DARK_PURPLE + args[1] + ChatColor.RED + ", Format: <MaterialName>[:data]");
                                    return true;
                                }
                                int minX = setting.getBorder1().getBlockX();
                                int minY = setting.getBorder1().getBlockY();
                                int minZ = setting.getBorder1().getBlockZ();
                                int maxX = setting.getBorder2().getBlockX();
                                int maxY = setting.getBorder2().getBlockY();
                                int maxZ = setting.getBorder2().getBlockZ();
                                World w = setting.getBorder1().getWorld();

                                ArrayList<Block> blocklist = new ArrayList<>();

                                for (int x = minX; x <= maxX; x++) {
                                    for (int y = minY; y <= maxY; y++) {
                                        blocklist.add(w.getBlockAt(x, y, minZ));
                                        blocklist.add(w.getBlockAt(x, y, maxZ));
                                    }
                                }

                                for (int z = minZ; z <= maxZ; z++) {
                                    for (int y = minY; y <= maxY; y++) {
                                        blocklist.add(w.getBlockAt(minX, y, z));
                                        blocklist.add(w.getBlockAt(maxX, y, z));
                                    }
                                }

                                final MaterialData finalMatData = fillMatData;

                                blocklist.stream().forEach(b -> {
                                    b.setType(finalMatData.getItemType());
                                    b.setData(finalMatData.getData());
                                });

                                p.sendMessage(Variables.cmodprefix + ChatColor.WHITE + blocklist.size() + ChatColor.AQUA + " Blöcke wurden ersetzt");
                            } else {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "/cmod walls <Material:Data>");
                            }
                            break;

                        case "del":
                            if (setting.getBorder1() == null || setting.getBorder2() == null) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Setzte voher deine Punkte");
                                return true;
                            }
                            MaterialData finalMatData = new MaterialData(Material.AIR);
                            ArrayList<Block> blocklist = new ArrayList<>();
                            int minX = setting.getBorder1().getBlockX();
                            int minY = setting.getBorder1().getBlockY();
                            int minZ = setting.getBorder1().getBlockZ();
                            int maxX = setting.getBorder2().getBlockX();
                            int maxY = setting.getBorder2().getBlockY();
                            int maxZ = setting.getBorder2().getBlockZ();
                            World w = setting.getBorder1().getWorld();
                            for (int x = minX; x <= maxX; x++) {
                                for (int z = minZ; z <= maxZ; z++) {
                                    for (int y = minY; y <= maxY; y++) {
                                        blocklist.add(w.getBlockAt(x, y, z));
                                    }
                                }
                            }
                            blocklist.stream().forEach(b -> {
                                b.setType(finalMatData.getItemType());
                                b.setData(finalMatData.getData());
                            });
                            p.sendMessage(Variables.cmodprefix + ChatColor.WHITE + blocklist.size() + ChatColor.AQUA + " Blöcke wurden ersetzt");
                            break;

                        case "order":
                            if (!p.hasPermission(Variables.PERMISSION_ORDER)) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Dir Fehlen die Permissions");
                                return true;
                            }
                            if (args.length == 3) {
                                String zoneName = args[1];
                                int order = 0;
                                try {
                                    order = Integer.parseInt(args[2]);
                                } catch (Exception e) {
                                    p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Es muss eine Zahl angegeben werden.");
                                    return true;
                                }
                                if (ZoneManager.getZone(zoneName) != null) {
                                    Zone zone = ZoneManager.getZone(zoneName);
                                    zone.setOrder(order);
                                    p.sendMessage(Variables.cmodprefix + ChatColor.GREEN + " " + ChatColor.AQUA + "Order " + ChatColor.GREEN + "für die Zone " + ChatColor.LIGHT_PURPLE + zoneName + ChatColor.GREEN + " wurde auf " + ChatColor.WHITE + order + ChatColor.GREEN + " gesetzt");
                                } else {
                                    p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Diese Zone existiert nicht");
                                }
                            } else {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "/cmod order <Zone> <Nummer>");
                            }
                            break;

                        case "help":
                            if (!p.hasPermission(Variables.PERMISSION_INFO)) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Dir Fehlen die Permissions");
                                return true;
                            }
                            p.sendMessage(Variables.cmodprefix + ChatColor.AQUA + "=== " + ChatColor.WHITE + " CMod Kommandos" + ChatColor.AQUA + " ===");
                            p.sendMessage(ChatColor.RED + "/cmod" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Zeigt die Version an");
                            p.sendMessage(ChatColor.RED + "/cmod help" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Zeigt die Hilfe an");
                            p.sendMessage(ChatColor.RED + "/cmod change <Zone> <Spieler>" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Ändert den Inhaber der Zone");
                            p.sendMessage(ChatColor.RED + "/cmod allow <Zone> <Spieler>" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Fügt einen Spieler der Zone hinzu");
                            p.sendMessage(ChatColor.RED + "/cmod disallow <Zone> <Spieler>" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Entfernt einen Spieler von einer Zone");
                            p.sendMessage(ChatColor.RED + "/cmod order <Zone> <Nummer>" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Bestimmt die Priorität einer Zone");
                            p.sendMessage(ChatColor.RED + "/cmod flag <Zone> <Flag> [Text]" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Verändert die Einstellung einer Zone");
                            p.sendMessage(ChatColor.RED + "/cmod info [Zone]" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Zeigt Informationen über eine Zone an");
                            p.sendMessage(ChatColor.RED + "/cmod tp <Zone>" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Teleportiert dich zu der Zone");
                            p.sendMessage(ChatColor.RED + "/cmod expand <Größe> <Richtung>" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Erweitert die ausgewählte Region");
                            p.sendMessage(ChatColor.RED + "/cmod fill <Material:Data>" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Füllt die ausgewählte Region");
                            p.sendMessage(ChatColor.RED + "/cmod walls <Material:Data>" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Erstellt Wände in der ausgewählte Region");
                            p.sendMessage(ChatColor.RED + "/cmod del" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Löscht die Blöcke der ausgewählte Region");
                            p.sendMessage(ChatColor.RED + "/cmod replace <VonMaterial:Data> <ZuMaterial:Data>" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Ersetzt das Material der ausgewählten Region");
                            p.sendMessage(ChatColor.RED + "/cmod tools" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Gibt dir die Tools für CMod");
                            break;

                        case "info":
                            if (!p.hasPermission(Variables.PERMISSION_INFO)) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Dir Fehlen die Permissions");
                                return true;
                            }
                            if (args.length == 2) {
                                String zoneName = args[1];
                                if (ZoneManager.getZone(zoneName) != null) {
                                    Zone zone = ZoneManager.getZone(zoneName);
                                    CMod.SendZoneInfo(p, zone);
                                } else {
                                    p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Diese Zone existiert nicht");
                                }
                            } else {
                                if (ZoneManager.getZone(p.getLocation()) != null) {
                                    Zone zone = ZoneManager.getZone(p.getLocation());
                                    CMod.SendZoneInfo(p, zone);
                                }
                            }
                            break;

                        case "tools":
                            if (!p.hasPermission(Variables.PERMISSION_CHECK) && !p.hasPermission(Variables.PERMISSION_DEFINE)) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Dir Fehlen die Permissions");
                                return true;
                            }
                            if (p.hasPermission(Variables.PERMISSION_CHECK)) {
                                p.getInventory().addItem(new ItemStack(Material.WOOD_SWORD, 1));
                            }

                            if (p.hasPermission(Variables.PERMISSION_DEFINE)) {
                                p.getInventory().addItem(new ItemStack(Material.WOOD_SPADE, 1));
                            }
                            p.sendMessage(Variables.cmodprefix + ChatColor.GREEN + "Bitte sehr :)");
                            break;

                        case "tp":
                            if (!p.hasPermission(Variables.PERMISSION_TP)) {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Dir Fehlen die Permissions");
                                return true;
                            }
                            if (args.length == 2) {
                                String zoneName = args[1];
                                if (ZoneManager.getZone(zoneName) != null) {
                                    Zone zone = ZoneManager.getZone(zoneName);
                                    p.teleport(zone.getBorder1());
                                    p.sendMessage(Variables.cmodprefix + ChatColor.GREEN + "Du wurdest erfolgreich zu der Zone " + ChatColor.LIGHT_PURPLE + zoneName + ChatColor.GREEN + " teleportiert");
                                } else {
                                    p.sendMessage(Variables.cmodprefix + ChatColor.RED + "Diese Zone existiert nicht");
                                }
                            } else {
                                p.sendMessage(Variables.cmodprefix + ChatColor.RED + "/cmod tp <Zone>");
                            }
                            break;

                        default:
                            p.chat("/cmod help");
                            break;
                    }
                }
            } else {
                cs.sendMessage(ChatColor.GOLD + "Original CMod by robin0van0der0v! ");
                cs.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Revision " + CMod.version + " by " + "Gurkengewuerz");
                cs.sendMessage(ChatColor.RED + "Dieser Befehl kann nur als Spieler ausgeführt werden");
            }
        }

        return true;
    }

    public static MaterialData parseMatData(String materialName, String delimiter) {
        materialName = materialName.toUpperCase();
        if ((materialName == null) || (materialName.isEmpty())) {
            return null;
        }
        materialName = materialName.trim();
        MaterialData result;
        try {
            if (materialName.contains(delimiter)) {
                Iterator<String> itr = Splitter.on(delimiter).split(materialName).iterator();
                if (Material.getMaterial((String) itr.next()) != null) {
                    result = new MaterialData(Material.getMaterial((String) itr.next()), Byte.parseByte((String) itr.next()));
                } else {
                    return null;
                }
            } else {
                if (Material.getMaterial(materialName) != null) {
                    result = new MaterialData(Material.getMaterial(materialName), (byte) -1);
                } else {
                    return null;
                }
            }
        } catch (NumberFormatException ex) {
            return null;
        }
        return result;
    }

}
