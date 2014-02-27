package de.craftlancer.economy.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;

import de.craftlancer.core.Utils;
import de.craftlancer.economy.CLEco;

public class MoneyTakeCommand extends MoneySubCommand
{
    
    public MoneyTakeCommand(String permission, CLEco plugin, boolean console)
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
            int amount = Integer.parseInt(args[2]);
            Inventory inv = Bukkit.getServer().getOfflinePlayer(args[1]).getPlayer().getInventory();
            if (!((CLEco) plugin).hasBalance(inv, amount))
                sender.sendMessage("Dieser Spieler hat nicht soviel Geld!");
            else
            {
                ((CLEco) plugin).withdrawBalance(inv, amount);
                sender.sendMessage("Du hast " + args[1] + " " + args[2] + " " + getPlugin().CURRENCY_NAME + " abgezogen.");
            }
        }
    }
    
    @Override
    public void help(CommandSender sender)
    {
        sender.sendMessage("Entferne Geld aus dem Inventar eines Spielers.");
        sender.sendMessage("/money take <Spieler> <Betrag>");
    }
    
}
