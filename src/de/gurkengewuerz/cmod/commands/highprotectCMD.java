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
public class highprotectCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String string, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(ChatColor.GOLD + "Original CMod by robin0van0der0v! ");
            cs.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Revision " + CMod.version + " by " + "Gurkengewuerz");
            cs.sendMessage(ChatColor.RED + "Dieser Befehl kann nur als Spieler ausgeführt werden");
            return true;
        }
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("highprotect")) {
            String arg = "";
            for (int i = 0; i < args.length; i++) {
                arg = arg + args[i] + " ";
            }
            p.chat("/cmod expand 255 up");
            p.chat("/cmod expand 255 down");
            p.chat("/cmod create " + arg);
        }
        return true;
    }
}
