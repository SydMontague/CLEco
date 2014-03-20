package de.craftlancer.economy;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import de.craftlancer.currencyhandler.Handler;

public class CLEcoMoneyHandler implements Handler<Object, Integer>
{
    private Economy economy;
    
    public CLEcoMoneyHandler(CLEco plugin)
    {
        this.economy = plugin;
    }
    
    @Override
    public boolean hasCurrency(Object holder, Integer amount)
    {
        if (holder == null)
            return economy.getServerBalance() >= amount;
        
        Inventory inventory = getInventory(holder);
        return economy.hasBalance(inventory, amount);
    }
    
    @Override
    public void withdrawCurrency(Object holder, Integer amount)
    {
        if (holder == null)
        {
            economy.removeFromServer(amount);
            return;
        }
        
        Inventory inventory = getInventory(holder);
        economy.withdrawBalance(inventory, amount);
    }
    
    @Override
    public void giveCurrency(Object holder, Integer amount)
    {
        if (holder == null)
        {
            economy.addToServer(amount);
            return;
        }
        
        Inventory inventory = getInventory(holder);
        economy.depositBalance(inventory, amount);
    }
    
    @Override
    public void setCurrency(Object holder, Integer amount) throws UnsupportedOperationException
    {
        if (holder == null)
        {
            int change = amount - economy.getServerBalance();
            
            if (change < 0)
                economy.removeFromServer(-change);
            else if (change > 0)
                economy.addToServer(change);
        }
        
        Inventory inventory = getInventory(holder);
        int change = amount - economy.getBalance(inventory);
        
        if (change < 0)
            economy.withdrawBalance(inventory, -change);
        else if (change > 0)
            economy.depositBalance(inventory, change);
    }
    
    @Override
    public String getFormatedString(Integer value)
    {
        return value + " " + getCurrencyName();
    }
    
    @Override
    public String getCurrencyName()
    {
        return economy.getCurrencyName();
    }
    
    @Override
    public boolean checkInputObject(Object obj)
    {
        return obj instanceof Integer;
    }
    
    private static Inventory getInventory(Object obj)
    {
        if (obj instanceof Inventory)
            return (Inventory) obj;
        if (obj instanceof InventoryHolder)
            return ((InventoryHolder) obj).getInventory();
        
        return null;
    }
    
    @Override
    public boolean checkInputHolder(Object obj)
    {
        return getInventory(obj) != null;
    }
}
