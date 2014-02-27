package de.craftlancer.economy;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import de.craftlancer.core.Utils;

public class InventoryWrapper
{
    private InventoryType type;
    private String id;
    
    public InventoryWrapper(Inventory inv)
    {
        type = inv.getType();
        switch (type)
        {
            case CHEST:
                id = Utils.getLocationString(((Chest) inv.getHolder()).getLocation());
                break;
            case PLAYER:
                id = ((Player) inv.getHolder()).getName();
                break;
            default:
                throw new IllegalArgumentException("The InventoryType " + type + " is not supported!");
        }
    }
    
    public InventoryWrapper(String s, InventoryType valueOf)
    {
        id = s;
        type = valueOf;
    }
    
    public String getId()
    {
        return id;
    }
    
    public InventoryType getType()
    {
        return type;
    }
    
    public Inventory getInventory()
    {
        switch (type)
        {
            case CHEST:
            {
                BlockState b = Utils.parseLocation(id).getBlock().getState();
                if (b instanceof Chest)
                    return ((Chest) b).getBlockInventory();
                break;
            }
            case PLAYER:
            {
                Player p = Bukkit.getPlayerExact(id);
                if (p != null)
                    return p.getInventory();
                break;
            }
            default:
                throw new IllegalArgumentException("The InventoryType " + type + " is not supported!");
        }
        
        return null;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof InventoryWrapper))
            return false;
        InventoryWrapper other = (InventoryWrapper) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (type != other.type)
            return false;
        return true;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
}
