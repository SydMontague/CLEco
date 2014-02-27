package de.craftlancer.economy.chestshop;

import java.math.BigDecimal;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType;
import com.Acrobot.ChestShop.Events.Economy.AccountCheckEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAmountEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyCheckEvent;

import de.craftlancer.economy.CLEco;

public class ChestShopListener implements Listener
{
    private CLEco plugin;
    
    public ChestShopListener(CLEco plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBuyItemCheck(PreTransactionEvent event)
    {
        if ((event.isCancelled() && event.getTransactionOutcome() != TransactionOutcome.CLIENT_DOES_NOT_HAVE_ENOUGH_MONEY) || event.getTransactionType() != TransactionType.BUY)
            return;
        
        ItemStack[] stock = event.getStock();
        Inventory ownerInventory = event.getOwnerInventory();
        
        if (!plugin.hasBalance(event.getClientInventory(), (int) event.getPrice()))
        {
            event.setCancelled(TransactionOutcome.CLIENT_DOES_NOT_HAVE_ENOUGH_MONEY);
            return;
        }
        
        if (!InventoryUtil.hasItems(stock, ownerInventory))
            event.setCancelled(TransactionOutcome.NOT_ENOUGH_STOCK_IN_CHEST);
        
        if (!ChestShopUtils.itemsFitInInventory(stock, event.getClientInventory()))
            event.setCancelled(TransactionOutcome.NOT_ENOUGH_SPACE_IN_INVENTORY);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onSellItemCheck(PreTransactionEvent event)
    {
        if ((event.isCancelled() && event.getTransactionOutcome() != TransactionOutcome.SHOP_DOES_NOT_HAVE_ENOUGH_MONEY) || event.getTransactionType() != TransactionType.SELL)
            return;
        
        ItemStack[] stock = event.getStock();
        Inventory clientInventory = event.getClientInventory();
        
        if (!plugin.hasBalance(event.getOwnerInventory(), (int) event.getPrice()))
        {
            event.setCancelled(TransactionOutcome.SHOP_DOES_NOT_HAVE_ENOUGH_MONEY);
            return;
        }
        
        if (!InventoryUtil.hasItems(stock, clientInventory))
            event.setCancelled(TransactionOutcome.NOT_ENOUGH_STOCK_IN_INVENTORY);
        
        if (!ChestShopUtils.itemsFitInInventory(stock, event.getOwnerInventory()))
            event.setCancelled(TransactionOutcome.NOT_ENOUGH_SPACE_IN_INVENTORY);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCurrAmount(CurrencyAmountEvent e)
    {
        e.setAmount(new BigDecimal(1000000));
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCurrAmount(CurrencyCheckEvent e)
    {
        e.hasEnough(true);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCurrAmount(AccountCheckEvent e)
    {
        e.hasAccount(true);
    }
    
    @EventHandler
    public void onBuyTransaction(TransactionEvent e)
    {
        if (e.getTransactionType() != TransactionType.BUY)
            return;
        
        plugin.payBalance(e.getClientInventory(), e.getOwnerInventory(), (int) e.getPrice());
    }
    
    @EventHandler
    public void onSellTransaction(TransactionEvent e)
    {
        if (e.getTransactionType() != TransactionType.SELL)
            return;
        
        plugin.payBalance(e.getOwnerInventory(), e.getClientInventory(), (int) e.getPrice());
    }
    
}
