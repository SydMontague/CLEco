package de.craftlancer.economy;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class MoneyTask extends BukkitRunnable
{
    private CLEco plugin;
    
    public MoneyTask(CLEco plugin)
    {
        this.plugin = plugin;
        long time = 1728000 - (System.currentTimeMillis() / 50) % 1728000;
        plugin.getLogger().info(time + " Ticks until the next Money output!");
        runTaskLater(plugin, time);
    }
    
    @Override
    public void run()
    {
        Set<String> ex = new HashSet<String>();
        ex.add("SydMontague");
        ex.add("Pashjn");
        ex.add("zwilling89");
        
        Set<String> rewarded = new HashSet<String>();
        
        for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers().clone())
            if ((p.isOnline() || p.getLastPlayed() + 86400000 > System.currentTimeMillis()) && !ex.contains(p.getName()))
                rewarded.add(p.getName());
        
        int money = plugin.getServerBalance();
        int moneyperuser = money / rewarded.size();
        
        for (String p : rewarded)
        {
            plugin.depositBalance(p, moneyperuser);
            plugin.removeFromServer(moneyperuser);
        }
        
        plugin.getLogger().info(moneyperuser + "Taler pro Spieler verteilt, " + money + " insgesamt.");
    }
    
}
