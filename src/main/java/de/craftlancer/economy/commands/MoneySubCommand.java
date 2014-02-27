package de.craftlancer.economy.commands;

import de.craftlancer.core.command.SubCommand;
import de.craftlancer.economy.CLEco;

public abstract class MoneySubCommand extends SubCommand
{
    public MoneySubCommand(String permission, CLEco plugin, boolean console)
    {
        super(permission, plugin, console);
    }
    
    @Override
    public CLEco getPlugin()
    {
        return (CLEco) super.getPlugin();
    }
}
