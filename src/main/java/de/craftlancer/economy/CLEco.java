package de.craftlancer.economy;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.craftlancer.core.CLPlugin;
import de.craftlancer.currencyhandler.CurrencyHandler;
import de.craftlancer.economy.chestshop.ChestShopListener;
import de.craftlancer.economy.commands.MoneyCommandHandler;

/*
 * 1 Globale Währung, Physisch repräsentiert durch Gold
 * 1 Nugget = 1, 1 Goldbarren = 9, 1 Goldblock = 81
 * 
 * Verwendung:
 * Shops, Handel, Nutzung von Stadtgebäuden
 * 
 * Geld kann nur verwendet, wenn es auch im Inventar ist
 * Geld wird nur zwischen 2 Inventaren transferiert
 * 
 * 
 * 
 * "Verschwundenes Geld" wird auf den virtuellen Serveraccount überwiesen
 * und um 3:10 an alle aktiven Spieler zu gleichen Teilen ausgeschüttet.
 * Dabei werden Zeitgleich alle Geld Itemstacks auf dem Boden zerstört.
 */
public class CLEco extends JavaPlugin implements CLPlugin, Economy
{
    private int server;
    public String MONEY_NAME = ChatColor.GOLD + "Münze";
    public String CURRENCY_NAME = "Taler";
    public ItemStack SAMPLE_NUGGET;
    public ItemStack SAMPLE_INGOT;
    public ItemStack SAMPLE_BLOCK;
    private static CLEco instance;
    
    protected Map<InventoryWrapper, Integer> oMap = new HashMap<InventoryWrapper, Integer>();
    
    {
        SAMPLE_NUGGET = new ItemStack(Material.GOLD_NUGGET, 1);
        ItemMeta meta = SAMPLE_NUGGET.getItemMeta();
        meta.setLore(Arrays.asList(new String[] { "1 " + CURRENCY_NAME }));
        meta.setDisplayName(MONEY_NAME);
        SAMPLE_NUGGET.setItemMeta(meta);
        
        SAMPLE_INGOT = new ItemStack(Material.GOLD_INGOT, 1);
        meta = SAMPLE_INGOT.getItemMeta();
        meta.setLore(Arrays.asList(new String[] { "9 " + CURRENCY_NAME }));
        meta.setDisplayName(MONEY_NAME);
        SAMPLE_INGOT.setItemMeta(meta);
        
        SAMPLE_BLOCK = new ItemStack(Material.GOLD_BLOCK, 1);
        meta = SAMPLE_BLOCK.getItemMeta();
        meta.setLore(Arrays.asList(new String[] { "81 " + CURRENCY_NAME }));
        meta.setDisplayName(MONEY_NAME);
        SAMPLE_BLOCK.setItemMeta(meta);
    }
    
    @Override
    public void onEnable()
    {
        instance = this;
        getServer().getPluginManager().registerEvents(new MoneyListener(this), this);
        getServer().getPluginManager().registerEvents(new ChestShopListener(this), this);
        
        if(getServer().getPluginManager().getPlugin("CurrencyHandler") != null)
            CurrencyHandler.registerCurrency("money", new CLEcoMoneyHandler(this));
        
        loadOverflow();
        new OverflowTask().runTaskTimer(this, 300L, 300L);
        getCommand("money").setExecutor(new MoneyCommandHandler(this));
    }
    
    @Override
    public void onDisable()
    {
        saveOverflow();
        getServer().getScheduler().cancelTasks(this);
    }
    
    public static CLEco getInstance()
    {
        return instance;
    }
    
    @Override
    public boolean isMoney(ItemStack i)
    {
        return i != null && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && (i.getItemMeta().getDisplayName().equals(MONEY_NAME) || i.getItemMeta().getDisplayName().equals("§6M�nze"));
    }
    
    private void loadOverflow()
    {
        File file = new File(getDataFolder(), "overflow.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        server = config.getInt("server", 0);
        
        for (String s : config.getKeys(false))
        {
            if (s.equals("server"))
                continue;
            oMap.put(new InventoryWrapper(s, InventoryType.valueOf(config.getString(s + ".type"))), config.getInt(s + ".overflow"));
        }
    }
    
    protected void saveOverflow()
    {
        File file = new File(getDataFolder(), "overflow.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        for (String s : config.getKeys(false))
            config.set(s, null);
        
        config.set("server", server);
        
        for (Entry<InventoryWrapper, Integer> e : oMap.entrySet())
        {
            config.set(e.getKey().getId() + ".type", e.getKey().getType().name());
            config.set(e.getKey().getId() + ".overflow", e.getValue());
        }
        
        try
        {
            config.save(file);
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }
    
    @Override
    public int getBalance(Inventory inventory)
    {
        int value = 0;
        
        for (ItemStack i : inventory.all(Material.GOLD_NUGGET).values())
            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equals(MONEY_NAME))
                value += i.getAmount();
        for (ItemStack i : inventory.all(Material.GOLD_INGOT).values())
            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equals(MONEY_NAME))
                value += i.getAmount() * 9;
        for (ItemStack i : inventory.all(Material.GOLD_BLOCK).values())
            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equals(MONEY_NAME))
                value += i.getAmount() * 81;
        
        return value;
    }
    
    public boolean hasBalance(Inventory inventory, int balance)
    {
        return getBalance(inventory) >= balance;
    }
    
    public void withdrawBalance(Inventory inventory, int balance)
    {
        if (!hasBalance(inventory, balance))
            return;
        
        int nugget = 0;
        int ingot = 0;
        int block = 0;
        
        for (ItemStack i : inventory.all(Material.GOLD_NUGGET).values())
            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equals(MONEY_NAME))
                nugget += i.getAmount();
        for (ItemStack i : inventory.all(Material.GOLD_INGOT).values())
            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equals(MONEY_NAME))
                ingot += i.getAmount();
        for (ItemStack i : inventory.all(Material.GOLD_BLOCK).values())
            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equals(MONEY_NAME))
                block += i.getAmount();
        
        int blockuse = 0;
        int ingotuse = 0;
        int nuggetuse = 0;
        
        for (; block > 0; block--)
        {
            if (balance < 81)
                break;
            
            blockuse++;
            balance -= 81;
        }
        
        for (; ingot > 0; ingot--)
        {
            if (balance < 9)
                break;
            
            ingotuse++;
            balance -= 9;
        }
        
        for (; nugget > 0; nugget--)
        {
            if (balance < 1)
                break;
            
            nuggetuse++;
            balance -= 1;
        }
        
        if (balance > 0)
            if (balance < 9 && ingot != 0)
            {
                
                ingotuse++;
                nuggetuse -= 9 - balance;
                balance = 0;
            }
            else if (balance < 80 && block != 0)
            {
                blockuse++;
                
                int rest = 81 - balance;
                
                while (rest > 9)
                {
                    ingotuse--;
                    rest -= 9;
                }
                
                nuggetuse -= rest;
                balance = 0;
            }
            else
                throw new RuntimeException("Not enough money in inventory, even though hasBalance() returned true!");
        
        int overflow = 0;
        
        if (nuggetuse < 0)
        {
            SAMPLE_NUGGET.setAmount(-nuggetuse);
            for (ItemStack i : inventory.addItem(SAMPLE_NUGGET).values())
                overflow += i.getAmount();
        }
        else if (nuggetuse > 0)
        {
            SAMPLE_NUGGET.setAmount(nuggetuse);
            inventory.removeItem(SAMPLE_NUGGET).values();
        }
        
        if (ingotuse < 0)
        {
            SAMPLE_INGOT.setAmount(-ingotuse);
            for (ItemStack i : inventory.addItem(SAMPLE_INGOT).values())
                overflow += i.getAmount() * 9;
        }
        else if (ingotuse > 0)
        {
            SAMPLE_INGOT.setAmount(ingotuse);
            inventory.removeItem(SAMPLE_INGOT).values();
        }
        
        if (blockuse < 0)
        {
            SAMPLE_BLOCK.setAmount(-blockuse);
            for (ItemStack i : inventory.addItem(SAMPLE_BLOCK).values())
                overflow += i.getAmount() * 81;
        }
        else if (blockuse > 0)
        {
            SAMPLE_BLOCK.setAmount(blockuse);
            inventory.removeItem(SAMPLE_BLOCK).values();
        }
        
        InventoryWrapper w = new InventoryWrapper(inventory);
        
        if (!oMap.containsKey(w))
            oMap.put(w, overflow);
        else
            oMap.put(w, oMap.get(w) + overflow);
    }
    
    public void depositBalance(Player p, int balance)
    {
        depositBalance(p.getInventory(), balance);
    }
    
    public void depositBalance(String p, int balance)
    {
        OfflinePlayer op = getServer().getOfflinePlayer(p);
        
        if (op.isOnline())
            depositBalance(op.getPlayer(), balance);
        else
        {
            InventoryWrapper w = new InventoryWrapper(p, InventoryType.PLAYER);
            if (!oMap.containsKey(w))
                oMap.put(w, balance);
            else
                oMap.put(w, oMap.get(w) + balance);
        }
    }
    
    public void depositBalance(Inventory inventory, int balance)
    {
        int block = balance / 81;
        int ingot = (balance - block * 81) / 9;
        int nugget = balance - block * 81 - ingot * 9;
        
        int overflow = 0;
        
        if (nugget > 0)
        {
            SAMPLE_NUGGET.setAmount(nugget);
            for (ItemStack i : inventory.addItem(SAMPLE_NUGGET).values())
                overflow += i.getAmount();
        }
        
        if (ingot > 0)
        {
            SAMPLE_INGOT.setAmount(ingot);
            for (ItemStack i : inventory.addItem(SAMPLE_INGOT).values())
                overflow += i.getAmount() * 9;
        }
        
        if (block > 0)
        {
            SAMPLE_BLOCK.setAmount(block);
            for (ItemStack i : inventory.addItem(SAMPLE_BLOCK).values())
                overflow += i.getAmount() * 81;
        }
        
        if (overflow == 0)
            return;
        
        InventoryWrapper w = new InventoryWrapper(inventory);
        
        if (!oMap.containsKey(w))
            oMap.put(w, overflow);
        else
            oMap.put(w, oMap.get(w) + overflow);
    }
    
    public void payBalance(Inventory sender, Inventory reciever, int balance)
    {
        withdrawBalance(sender, balance);
        depositBalance(reciever, balance);
    }
    
    class OverflowTask extends BukkitRunnable
    {
        long run = 0;
        
        @Override
        public void run()
        {
            run++;
            if (run % 60 == 0)
                saveOverflow();
            
            for (Entry<InventoryWrapper, Integer> e : new HashMap<InventoryWrapper, Integer>(oMap).entrySet())
            {
                Inventory i = e.getKey().getInventory();
                if (i == null)
                    continue;
                
                int value = e.getValue();
                oMap.remove(e.getKey());
                depositBalance(i, value);
            }
        }
    }
    
    public void addToServer(ItemStack item)
    {
        int balance = 0;
        
        if (!isMoney(item))
            return;
        
        switch (item.getType())
        {
            case GOLD_NUGGET:
                balance = item.getAmount();
                break;
            case GOLD_INGOT:
                balance = item.getAmount() * 9;
                break;
            case GOLD_BLOCK:
                balance = item.getAmount() * 81;
                break;
            default:
                break;
        }
        
        addToServer(balance);
    }
    
    public void addToServer(int balance)
    {
        server += balance;
    }
    
    public void removeFromServer(ItemStack item, int pickedUp)
    {
        int balance = 0;
        
        if (!isMoney(item))
            return;
        
        switch (item.getType())
        {
            case GOLD_NUGGET:
                balance = item.getAmount() - pickedUp;
                break;
            case GOLD_INGOT:
                balance = (item.getAmount() - pickedUp) * 9;
                break;
            case GOLD_BLOCK:
                balance = (item.getAmount() - pickedUp) * 81;
                break;
            default:
                break;
        }
        
        removeFromServer(balance);
    }
    
    public void removeFromServer(int balance)
    {
        server -= balance;
    }
    
    public int getServerBalance()
    {
        return server;
    }

    @Override
    public String getCurrencyName()
    {
        return CURRENCY_NAME;
    }
}
