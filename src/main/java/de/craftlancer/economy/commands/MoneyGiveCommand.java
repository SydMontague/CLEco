package de.craftlancer.economy.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.craftlancer.core.Utils;
import de.craftlancer.economy.CLEco;

public class MoneyGiveCommand extends MoneySubCommand
{
    public MoneyGiveCommand(String permission, CLEco plugin, boolean console)
    {
        super(permission, plugin, console);
    }
    
    @Override
    protected void execute(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!checkSender(sender))
            sender.sendMessage("Du hast keine Berechtigung für diesen Befehl!");
        else if (args.length < 3)
            sender.sendMessage("Nicht genügend Argumente.");
        else if (!Bukkit.getServer().getOfflinePlayer(args[1]).isOnline())
            sender.sendMessage(args[1] + " ist nicht online!");
        else if (!Utils.isInt(args[2]))
            sender.sendMessage(args[2] + " ist keine Zahl!");
        else
        {
            ((CLEco) plugin).depositBalance(Bukkit.getServer().getOfflinePlayer(args[1]).getPlayer().getInventory(), Integer.parseInt(args[2]));
            sender.sendMessage("Du hast " + args[1] + " " + args[2] + " " + getPlugin().CURRENCY_NAME + " gegeben.");
        }
        
    }
    
    @Override
    public void help(CommandSender sender)
    {
        sender.sendMessage("Gebe einen Spieler Geld.");
        sender.sendMessage("/money give <Spieler> <Betrag>");
    }
    
}
