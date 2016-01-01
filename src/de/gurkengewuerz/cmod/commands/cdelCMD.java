package de.gurkengewuerz.cmod.commands;

import de.gurkengewuerz.cmod.CMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author gurkengewuerz.de
 */
public class cdelCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String string, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(ChatColor.GOLD + "Original CMod by robin0van0der0v! " + ChatColor.YELLOW + ChatColor.ITALIC + "Revision " + CMod.version + " by " + CMod.RainbowString("Gurkengewuerz", ""));
            cs.sendMessage(ChatColor.RED + "Dieser Befehl kann nur als Spieler ausgeführt werden");
            return true;
        }
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("cdel")) {
            String arg = "";
            for (int i = 1; i < args.length; i++) {
                arg = arg + args[i] + " ";
            }
            p.chat("/cmod del " + arg);
        }
        return true;
    }

}
