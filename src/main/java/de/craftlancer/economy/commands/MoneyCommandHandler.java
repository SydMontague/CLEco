package de.craftlancer.economy.commands;

import de.craftlancer.core.command.CommandHandler;
import de.craftlancer.economy.CLEco;

public class MoneyCommandHandler extends CommandHandler
{
    public MoneyCommandHandler(CLEco plugin)
    {
        super(plugin);
        registerSubCommand("take", new MoneyTakeCommand("cleco.commands.take", plugin, true), "remove");
        registerSubCommand("give", new MoneyGiveCommand("cleco.commands.give", plugin, true), "add");
        registerSubCommand("show", new MoneyShowCommand("cleco.commands.show", plugin, true));
    }
}
