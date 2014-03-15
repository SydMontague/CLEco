package de.craftlancer.economy.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.craftlancer.economy.CLEco;

public class MoneyShowCommand extends MoneySubCommand
{
    
    public MoneyShowCommand(String permission, CLEco plugin, boolean console)
    {
        super(permission, plugin, console);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!checkSender(sender))
            return "Du hast keine Berechtigung für diesen Befehl!";
        if (args.length < 2)
            return "Nicht genügend Argumente.";
        if (!Bukkit.getServer().getOfflinePlayer(args[1]).isOnline())
            return args[1] + " ist nicht online!";
        
        return args[1] + "'s Kontostand: " + ((CLEco) plugin).getBalance(Bukkit.getServer().getOfflinePlayer(args[1]).getPlayer().getInventory()) + " " + getPlugin().CURRENCY_NAME;
    }
    
    @Override
    public void help(CommandSender sender)
    {
        sender.sendMessage("Zeige den Kontostand eines Spieler an.");
        sender.sendMessage("/money show <Spieler>");
    }
    
}
