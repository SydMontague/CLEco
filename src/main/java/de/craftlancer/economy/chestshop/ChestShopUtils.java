package de.craftlancer.economy.chestshop;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;

/**
 * Everything here is copied from ChestShop in order to add CLEco functionality to ChestShop.
 */
public class ChestShopUtils
{
    public static int getAmountOfAffordableItems(BigDecimal walletMoney, double pricePerItem) {
        return (int) Math.floor(walletMoney.doubleValue() / pricePerItem);
    }

    public static ItemStack[] getItems(ItemStack[] stock, Inventory inventory) {
        List<ItemStack> toReturn = new LinkedList<ItemStack>();

        ItemStack[] neededItems = InventoryUtil.mergeSimilarStacks(stock);

        for (ItemStack item : neededItems) {
            int amount = InventoryUtil.getAmount(item, inventory);

            ItemStack clone = item.clone();
            clone.setAmount(amount > item.getAmount() ? item.getAmount() : amount);

            toReturn.add(clone);
        }

        return toReturn.toArray(new ItemStack[toReturn.size()]);
    }

    public static ItemStack[] getCountedItemStack(ItemStack[] stock, int numberOfItems) {
        int left = numberOfItems;
        LinkedList<ItemStack> stacks = new LinkedList<ItemStack>();

        for (ItemStack stack : stock) {
            int count = stack.getAmount();
            ItemStack toAdd;

            if (left > count) {
                toAdd = stack;
                left -= count;
            } else {
                ItemStack clone = stack.clone();

                clone.setAmount(left);
                toAdd = clone;
                left = 0;
            }

            boolean added = false;

            for (ItemStack iStack : stacks) {
                if (MaterialUtil.equals(toAdd, iStack)) {
                    iStack.setAmount(iStack.getAmount() + toAdd.getAmount());
                    added = true;
                    break;
                }
            }

            if (!added) {
                stacks.add(toAdd);
            }

            if (left <= 0) {
                break;
            }
        }

        return stacks.toArray(new ItemStack[stacks.size()]);
    }
    
    public static boolean itemsFitInInventory(ItemStack[] items, Inventory inventory) {
        for (ItemStack item : items) {
            if (!InventoryUtil.fits(item, inventory)) {
                return false;
            }
        }

        return true;
    }
}
