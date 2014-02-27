package de.craftlancer.economy;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import de.craftlancer.wayofshadows.event.ShadowLockpickEvent;

public class MoneyListener implements Listener
{
    private CLEco plugin;
    
    public MoneyListener(CLEco plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e)
    {
        if (plugin.isMoney(e.getItemInHand()))
            e.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemCraft(PrepareItemCraftEvent e)
    {
        Recipe recipe = e.getRecipe();
        
        switch (recipe.getResult().getType())
        {
            case GOLD_NUGGET:
                for (ItemStack i : e.getInventory().getMatrix())
                {
                    if (i == null || i.getType() == Material.AIR)
                        continue;
                    
                    if (i.getType() == Material.GOLD_INGOT && plugin.isMoney(i))
                    {
                        plugin.SAMPLE_NUGGET.setAmount(9);
                        e.getInventory().setResult(plugin.SAMPLE_NUGGET);
                    }
                }
                break;
            case GOLD_INGOT:
            {
                int length = 0;
                for (ItemStack i : e.getInventory().getMatrix())
                    if (i != null && i.getType() != Material.AIR)
                        length++;
                
                if (length == 1)
                    for (ItemStack i : e.getInventory().getMatrix())
                    {
                        if (i == null || i.getType() == Material.AIR)
                            continue;
                        
                        if (i.getType() == Material.GOLD_BLOCK && plugin.isMoney(i))
                        {
                            plugin.SAMPLE_INGOT.setAmount(9);
                            e.getInventory().setResult(plugin.SAMPLE_INGOT);
                        }
                    }
                else if (length == 9)
                {
                    boolean money = false;
                    int j = 0;
                    for (ItemStack i : e.getInventory().getMatrix())
                    {
                        if (j >= 9)
                            continue;
                        
                        j++;
                        
                        if (plugin.isMoney(i) && !money)
                            money = true;
                        
                        if (i == null || i.getType() != Material.GOLD_NUGGET || (!plugin.isMoney(i) && money))
                        {
                            e.getInventory().setResult(null);
                            return;
                        }
                    }
                    
                    if (money)
                    {
                        plugin.SAMPLE_INGOT.setAmount(1);
                        e.getInventory().setResult(plugin.SAMPLE_INGOT);
                    }
                }
            }
                break;
            case GOLD_BLOCK:
                int length = 0;
                for (ItemStack i : e.getInventory().getMatrix())
                    if (i != null && i.getType() != Material.AIR)
                        length++;
                
                if (length == 9)
                {
                    boolean money = false;
                    int j = 0;
                    for (ItemStack i : e.getInventory().getMatrix())
                    {
                        if (j >= 9)
                            continue;
                        
                        j++;
                        
                        if (plugin.isMoney(i) && !money)
                            money = true;
                        
                        if (i == null || i.getType() != Material.GOLD_INGOT || (!plugin.isMoney(i) && money))
                        {
                            e.getInventory().setResult(null);
                            return;
                        }
                    }
                    
                    if (money)
                    {
                        plugin.SAMPLE_BLOCK.setAmount(1);
                        e.getInventory().setResult(plugin.SAMPLE_BLOCK);
                    }
                }
                break;
            default:
                for (ItemStack i : e.getInventory().getMatrix())
                    if (plugin.isMoney(i))
                        e.getInventory().setResult(null);
                break;
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent e)
    {
        ItemStack i = e.getEntity().getItemStack();
        if (plugin.isMoney(i))
            plugin.addToServer(i);
    }
    
    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e)
    {
        ItemStack i = e.getItem().getItemStack();
        if (plugin.isMoney(i))
            plugin.removeFromServer(i, e.getRemaining());
    }
    
    @EventHandler
    public void onItemPickup(InventoryPickupItemEvent e)
    {
        ItemStack i = e.getItem().getItemStack();
        if (plugin.isMoney(i))
            plugin.removeFromServer(i, 0);
    }
    
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e)
    {
        for (Entity ee : e.getChunk().getEntities())
            if (ee.getType() == EntityType.DROPPED_ITEM && plugin.isMoney(((Item) ee).getItemStack()))
                ee.remove();
    }
    
    @EventHandler
    public void onLogin(PlayerJoinEvent e)
    {
        for (ItemStack i : e.getPlayer().getInventory())
            if (i != null && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equalsIgnoreCase("§6M�nze"))
            {
                e.getPlayer().getInventory().remove(i);
                
                ItemMeta meta = i.getItemMeta();
                meta.setDisplayName(plugin.MONEY_NAME);
                i.setItemMeta(meta);
                e.getPlayer().getInventory().addItem(i);
            }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLockpick(ShadowLockpickEvent e)
    {
        if (plugin.isMoney(e.getPlayer().getItemInHand()))
            e.setCancelled(true);
    }
}
