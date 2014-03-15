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
    protected String execute(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!checkSender(sender))
            return "Du hast keine Berechtigung für diesen Befehl!";
        if (args.length < 3)
            return "Nicht genügend Argumente.";
        if (!Bukkit.getServer().getOfflinePlayer(args[1]).isOnline())
            return args[1] + " ist nicht online!";
        if (!Utils.isInt(args[2]))
            return args[2] + " ist keine Zahl!";
        
        ((CLEco) plugin).depositBalance(Bukkit.getServer().getOfflinePlayer(args[1]).getPlayer().getInventory(), Integer.parseInt(args[2]));
        return "Du hast " + args[1] + " " + args[2] + " " + getPlugin().CURRENCY_NAME + " gegeben.";
    }
    
    @Override
    public void help(CommandSender sender)
    {
        sender.sendMessage("Gebe einen Spieler Geld.");
        sender.sendMessage("/money give <Spieler> <Betrag>");
    }
    
}
