package de.gurkengewuerz.cmod.manager;

import de.gurkengewuerz.cmod.CMod;
import de.gurkengewuerz.cmod.Variables;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 *
 * @author gurkengewuerz.de
 */
public class PluginLoader {

    public static HashMap<String, Boolean> pvp = new HashMap<>();
    public static double V_Create;
    public static double V_Delete;
    public static double V_Allow;
    public static double V_Disallow;

    private static void load() {
        File file = new File(Variables.dataFolder + "saves/");
        if (!file.exists()) {
            file.mkdirs();
        }

        loadUpdate();
    }

    private static Vector getVector(String value) {
        Vector vector = new Vector(0, 0, 0);
        String[] split = value.split(", ");
        try {
            vector.setX(Double.parseDouble(split[0].substring(1)));
            vector.setY(Double.parseDouble(split[1]));
            vector.setZ(Double.parseDouble(split[2].substring(0, split[2].length() - 1)));
        } catch (Exception e) {
            return null;
        }
        return vector;
    }

    private static ItemStack getItemStack(String value) {
        ItemStack item = null;
        String[] split = value.split(", ");
        try {
            ItemStack stack = new ItemStack(0);
            stack.setType(Material.matchMaterial(split[0].substring(1)));
            stack.setAmount(Integer.parseInt(split[1]));
            stack.setDurability(Short.parseShort(split[2].substring(0, split[2].length() - 1)));
            item = stack;
        } catch (Exception e) {
            return null;
        }
        return item;
    }

    private static boolean loadZone(File file) {
        boolean failed = true;
        try {
            Configuration saveFile = new Configuration(file);
            saveFile.load();

            Zone zone = new Zone(file.getName().replace(".yml", ""));
            zone.setSave(false);
            try {
                zone.setWorld(CMod.getInstance().getServer().getWorld(saveFile.getString("world", "world")));
            } catch (NullPointerException npe) {
                System.err.println(ChatColor.RED + "Zone '" + ChatColor.AQUA + zone.getName() + ChatColor.RED + "' besitzt eine unbekannte Welt: " + ChatColor.YELLOW + saveFile.getString("world", "world"));
                failed = false;
            }
            zone.setOrder(saveFile.getInt("Order", 0));
            zone.setWelcome(saveFile.getString("welcome", ""));
            zone.setFarewell(saveFile.getString("farewell", ""));
            zone.setBorder(1, new Location(zone.getWorld(), saveFile.getInt("border1.x", 0), saveFile.getInt("border1.y", 0), saveFile.getInt("border1.z", 0)));
            zone.setBorder(2, new Location(zone.getWorld(), saveFile.getInt("border2.x", 0), saveFile.getInt("border2.y", 0), saveFile.getInt("border2.z", 0)));
            Flags[] arrayOfFlags;
            int j = (arrayOfFlags = Flags.values()).length;
            for (int i = 0; i < j; i++) {
                Flags flag = arrayOfFlags[i];

                zone.setFlag(flag.getId(), saveFile.getBoolean("flag." + flag.toString(), false));
            }
            zone.setParent(saveFile.getString("parent-zone", ""));
            zone.setCreationDate(saveFile.getLong("creation-date", 0L));

            zone.setAllowed((ArrayList) saveFile.getStringList("allowed"));

            zone.setSave(true);
            ZoneManager.add(zone);

            return true;
        } catch (Exception e) {
            if (failed) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void loadUpdate() {
        try {
            int loaded = 0;
            File dir = new File(Variables.dataFolder + "saves/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File[] arrayOfFile;
            int j = (arrayOfFile = dir.listFiles()).length;
            System.out.println(Arrays.toString(arrayOfFile));
            for (int i = 0; i < j; i++) {
                File file = arrayOfFile[i];
                if ((file.getName().endsWith(".yml"))
                        && (loadZone(file))) {
                    loaded++;
                }
            }
            System.out.println("CMOD: Loaded " + loaded + " zones");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
