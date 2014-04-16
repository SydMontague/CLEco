package de.craftlancer.economy;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import de.craftlancer.currencyhandler.Handler;

public class CLEcoMoneyHandler implements Handler
{
    private Economy economy;
    
    public CLEcoMoneyHandler(CLEco plugin)
    {
        this.economy = plugin;
    }
    
    @Override
    public boolean hasCurrency(Object holder, Object amount)
    {
        if (!checkInputHolder(holder))
            return false;
        
        if (!checkInputObject(amount))
            return false;
        
        if (holder == null)
            return economy.getServerBalance() >= (Integer) amount;
        
        Inventory inventory = getInventory(holder);
        return economy.hasBalance(inventory, (Integer) amount);
    }
    
    @Override
    public void withdrawCurrency(Object holder, Object amount)
    {
        if (!checkInputHolder(holder))
            return;
        
        if (!checkInputObject(amount))
            return;
        
        if (holder == null)
        {
            economy.removeFromServer((Integer) amount);
            return;
        }
        
        Inventory inventory = getInventory(holder);
        economy.withdrawBalance(inventory, (Integer) amount);
    }
    
    @Override
    public void giveCurrency(Object holder, Object amount)
    {
        if (!checkInputHolder(holder))
            return;
        
        if (!checkInputObject(amount))
            return;
        
        if (holder == null)
        {
            economy.addToServer((Integer) amount);
            return;
        }
        
        Inventory inventory = getInventory(holder);
        economy.depositBalance(inventory, (Integer) amount);
    }
    
    @Override
    public void setCurrency(Object holder, Object amount) throws UnsupportedOperationException
    {
        if (!checkInputHolder(holder))
            return;
        
        if (!checkInputObject(amount))
            return;
        
        if (holder == null)
        {
            int change = (Integer) amount - economy.getServerBalance();
            
            if (change < 0)
                economy.removeFromServer(-change);
            else if (change > 0)
                economy.addToServer(change);
        }
        
        Inventory inventory = getInventory(holder);
        int change = (Integer) amount - economy.getBalance(inventory);
        
        if (change < 0)
            economy.withdrawBalance(inventory, -change);
        else if (change > 0)
            economy.depositBalance(inventory, change);
    }
    
    @Override
    public String getFormatedString(Object value)
    {
        if (!checkInputObject(value))
            return "INVALID INPUT OBJECT";
        
        return value.toString() + " " + getCurrencyName();
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
        return obj == null || getInventory(obj) != null;
    }
}
