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
    protected void execute(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!checkSender(sender))
            sender.sendMessage("Du hast keine Berechtigung für diesen Befehl!");
        else if (args.length < 2)
            sender.sendMessage("Nicht genügend Argumente.");
        else if (!Bukkit.getServer().getOfflinePlayer(args[1]).isOnline())
            sender.sendMessage(args[1] + " ist nicht online!");
        else
            sender.sendMessage(args[1] + "'s Kontostand: " + ((CLEco) plugin).getBalance(Bukkit.getServer().getOfflinePlayer(args[1]).getPlayer().getInventory()) + " " + getPlugin().CURRENCY_NAME);
    }
    
    @Override
    public void help(CommandSender sender)
    {
        sender.sendMessage("Zeige den Kontostand eines Spieler an.");
        sender.sendMessage("/money show <Spieler>");
    }
    
}
