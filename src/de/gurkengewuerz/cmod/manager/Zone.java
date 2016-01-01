package de.gurkengewuerz.cmod.manager;

import de.gurkengewuerz.cmod.Variables;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author gurkengewuerz.de
 */
public class Zone {

    private String name = "";
    private World world = null;
    private Location border1 = null;
    private Location border2 = null;
    private long creationDate = 0L;
    private String welcome = "";
    private String farewell = "";
    private boolean cansave = true;
    private boolean[] flags = new boolean[Flags.values().length];
    private ArrayList<String> allowed = new ArrayList();
    private String parent = "";
    private YamlConfiguration saveFile;
    private int Order = 0;

    public Zone(String s) {
        this.name = s;
        this.saveFile = YamlConfiguration.loadConfiguration(getSaveFile());
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<String> getOwners() {
        ArrayList owners = new ArrayList();
        for (String a : this.allowed) {
            if (a.startsWith("o:")) {
                owners.add(a.substring(2));
            }
        }
        return owners;
    }

    public int getOrder() {
        return this.Order;
    }

    public void setOrder(int z) {
        this.Order = z;
        setProperty("Order", this.Order);
    }

    public Location getBorder1() {
        return this.border1;
    }

    public Location getBorder2() {
        return this.border2;
    }

    public ArrayList<String> getAllowed() {
        return this.allowed;
    }

    public boolean hasFlag(Flags a) {
        boolean flag = this.flags[a.getId()];
        if ((getParent() != null) && (!flag)) {
            flag = getParent().hasFlag(a);
        }
        return flag;
    }

    public String getWelcome() {
        if ((getParent() != null)
                && (this.welcome.equals(""))) {
            return getParent().getWelcome();
        }
        return this.welcome;
    }

    public String getFarewell() {
        if ((getParent() != null)
                && (this.farewell.equals(""))) {
            return getParent().getFarewell();
        }
        return this.farewell;
    }

    public World getWorld() {
        return this.world;
    }

    public ArrayList<Flags> getFlags() {
        ArrayList list = new ArrayList();
        Flags[] arrayOfFlags;
        int j = (arrayOfFlags = Flags.values()).length;
        for (int i = 0; i < j; i++) {
            Flags flag = arrayOfFlags[i];
            if (hasFlag(flag)) {
                list.add(flag);
            }
        }
        return list;
    }

    public Zone getParent() {
        Zone parent = null;
        if (this.parent.length() > 0) {
            parent = ZoneManager.getZone(this.parent);
            if (parent == null) {
                setParent("");
            }
        }
        return parent;
    }

    public boolean canSave() {
        return this.cansave;
    }

    public File getSaveFile() {
        File file = new File(Variables.dataFolder + "saves/" + this.name + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("CMOD: FAILED TO CREATE FILE");
            }
        }
        return file;
    }

    public long getCreationDate() {
        return this.creationDate;
    }

    public void setBorder(int a, Location b) {
        if (a == 1) {
            this.border1 = b;
            setProperty("border1.x", b.getBlockX());
            setProperty("border1.y", b.getBlockY());
            setProperty("border1.z", b.getBlockZ());
        } else if (a == 2) {
            this.border2 = b;
            setProperty("border2.x", b.getBlockX());
            setProperty("border2.y", b.getBlockY());
            setProperty("border2.z", b.getBlockZ());
        }
        setWorld(b.getWorld());
    }

    public void setWorld(World a) {
        this.world = a;
        setProperty("world", a.getName());
    }

    public void setAllowed(ArrayList<String> a) {
        this.allowed = a;
        setProperty("allowed", a);
    }

    public void setFlag(Integer a, boolean b) {
        this.flags[a] = b;
        setProperty("flag." + Flags.values()[a].toString(), b);
    }

    public void setWelcome(String a) {
        this.welcome = a;
        setProperty("welcome", a);
    }

    public void setFarewell(String a) {
        this.farewell = a;
        setProperty("farewell", a);
    }

    public boolean Add(String a) {
        if (!this.allowed.contains(a)) {
            this.allowed.add(a);
            setProperty("allowed", this.allowed);
            return true;
        }
        return false;
    }

    public boolean Remove(String a) {
        if (this.allowed.contains(a)) {
            this.allowed.remove(a);
            setProperty("allowed", this.allowed);
            return true;
        }
        return false;
    }

    public void resetOwners() {
        for (String s : getOwners()) {
            s = "o:" + s;
            Remove(s);
        }
    }

    public boolean changeOwner(String a) {
        if (getAllowed().contains(a)) {
            return false;
        } else {
            resetOwners();
            Add("o:" + a);
            return true;
        }
    }

    public void setParent(String a) {
        this.parent = a;
        setProperty("parent-zone", this.parent);
    }

    public void setSave(boolean a) {
        this.cansave = a;
    }

    public void setCreationDate(Long a) {
        this.creationDate = a;
        setProperty("creation-date", this.creationDate);
    }

    private void setProperty(String key, Object value) {
        if (canSave()) {
            try {
                this.saveFile.load(getSaveFile());
                this.saveFile.set(key, value);
                this.saveFile.save(getSaveFile());
            } catch (IOException | InvalidConfigurationException e) {
                System.err.println("Couldn't set property '" + key + "' for zone '" + getName() + "'");
            }
        }
    }
}
