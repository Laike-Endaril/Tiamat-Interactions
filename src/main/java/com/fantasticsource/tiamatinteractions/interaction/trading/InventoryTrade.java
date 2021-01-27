package com.fantasticsource.tiamatinteractions.interaction.trading;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryTrade implements IInventory
{
    public final NonNullList<ItemStack> stackList;
    public final ContainerTrade container;

    public InventoryTrade(ContainerTrade containerTrade)
    {
        container = containerTrade;
        stackList = NonNullList.withSize(18, ItemStack.EMPTY);
    }

    public int getSizeInventory()
    {
        return stackList.size();
    }

    public boolean isEmpty()
    {
        for (ItemStack itemstack : stackList)
        {
            if (!itemstack.isEmpty()) return false;
        }

        return true;
    }

    public ItemStack getStackInSlot(int index)
    {
        return index >= getSizeInventory() ? ItemStack.EMPTY : stackList.get(index);
    }

    public String getName()
    {
        return "faerunutils.trade";
    }

    public boolean hasCustomName()
    {
        return false;
    }

    public ITextComponent getDisplayName()
    {
        return (hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName()));
    }

    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(stackList, index);
    }

    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndSplit(stackList, index, count);
    }

    public void setInventorySlotContents(int index, ItemStack stack)
    {
        stackList.set(index, stack);
    }

    public int getInventoryStackLimit()
    {
        return 64;
    }

    public void markDirty()
    {
    }

    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return true;
    }

    public void openInventory(EntityPlayer player)
    {
    }

    public void closeInventory(EntityPlayer player)
    {
    }

    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    public int getField(int id)
    {
        return 0;
    }

    public void setField(int id, int value)
    {
    }

    public int getFieldCount()
    {
        return 0;
    }

    public void clear()
    {
        stackList.clear();
    }
}