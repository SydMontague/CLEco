package de.craftlancer.economy;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface Economy
{
    public boolean isMoney(ItemStack item);
    
    public int getBalance(Inventory inventory);
    
    public boolean hasBalance(Inventory inventory, int balance);
    
    public void withdrawBalance(Inventory inventory, int balance);
    
    public void depositBalance(Player p, int balance);
    
    public void depositBalance(String p, int balance);
    
    public void depositBalance(Inventory p, int balance);
    
    public void payBalance(Inventory sender, Inventory reciever, int balance);
    
    public String getCurrencyName();
    
    public void addToServer(ItemStack item);
    
    public void addToServer(int balance);
    
    public void removeFromServer(ItemStack item, int pickedUp);
    
    public void removeFromServer(int balance);
    
    public int getServerBalance();
    
}
