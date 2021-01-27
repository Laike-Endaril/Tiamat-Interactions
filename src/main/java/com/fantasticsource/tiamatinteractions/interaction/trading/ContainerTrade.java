package com.fantasticsource.tiamatinteractions.interaction.trading;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import com.fantasticsource.mctools.items.ItemMatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

import java.util.ArrayList;

import static com.fantasticsource.tiamatinteractions.TiamatInteractions.MODID;

public class ContainerTrade extends Container
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/trade.png");

    public final EntityPlayer player;
    public final World world;

    public final int playerInventoryStartIndex, cargoInventorySize, hotbarStartIndex;
    public final int fullInventoryStart, fullInventoryEnd;

    public final InventoryTrade inventory;

    protected final ArrayList<ItemStack> previous = new ArrayList<>();


    public ContainerTrade(EntityPlayer player, World world)
    {
        this.player = player;
        this.world = world;


        inventory = new InventoryTrade(this);
        for (ItemStack stack : inventory.stackList) previous.add(MCTools.cloneItemStack(stack));


        //Slot indices
        cargoInventorySize = player.inventory.mainInventory.size() - 9;

        hotbarStartIndex = 18;
        playerInventoryStartIndex = hotbarStartIndex + 9;

        fullInventoryStart = hotbarStartIndex;
        fullInventoryEnd = fullInventoryStart + 9 + cargoInventorySize - 1;


        //Your slots
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new YourSlot(this, x, 8 + x * 18, 8, 240, 0));
        }


        //My slots
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new MySlot(this, x + 9, 8 + x * 18, 44, 240, 0));
        }


        //Inventory
        for (int i = 0; i < 27; i++)
        {
            addSlotToContainer(new Slot(player.inventory, 9 + i, 8 + (i % 9) * 18, 84 + (i / 9) * 18));
        }


        //Hotbar
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 142));
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer ignored)
    {
        super.onContainerClosed(player);

        if (!player.world.isRemote)
        {
            clearContainer(player, player.world, inventory);
            Trading.TradeData data = Trading.TRADE_DATA.remove(player);
            if (data != null)
            {
                EntityPlayerMP other = data.playerBesides((EntityPlayerMP) player);
                Trading.TRADE_DATA.remove(other);
                other.closeScreen();
            }
        }
    }

    @Override
    protected void clearContainer(EntityPlayer player, World world, IInventory inventory)
    {
        if (!(player instanceof EntityPlayerMP)) return;

        if (!player.isEntityAlive())
        {
            for (int i = 9; i < inventory.getSizeInventory(); i++)
            {
                player.dropItem(inventory.removeStackFromSlot(i), false);
            }
        }
        else
        {
            for (int i = 9; i < inventory.getSizeInventory(); i++)
            {
                MCTools.give((EntityPlayerMP) player, inventory.removeStackFromSlot(i));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer ignored)
    {
        return player.isEntityAlive() && player.world == world;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer ignored, int index)
    {
        Slot slot = inventorySlots.get(index);
        if (slot == null) return ItemStack.EMPTY;

        ItemStack itemstack1 = slot.getStack();
        if (itemstack1.isEmpty()) return ItemStack.EMPTY;


        ItemStack itemstack = itemstack1.copy();

        if (slot instanceof FilteredSlot) //Your slots (blocked for me)
        {
            return ItemStack.EMPTY;
        }
        else if (index < 18) //From my slots
        {
            //To inventory or hotbar
            if (!mergeItemStack(itemstack1, fullInventoryStart, fullInventoryEnd + 1, false)) return ItemStack.EMPTY;
        }
        else if (index >= fullInventoryStart && index <= fullInventoryEnd) //From inventory or hotbar
        {
            //To my slots
            if (!mergeItemStack(itemstack1, 9, 18, false)) return ItemStack.EMPTY;
        }
        else
        {
            throw new IllegalStateException("Unsupported custom inventory detected!");
        }


        if (itemstack1.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else slot.onSlotChanged();

        if (itemstack1.getCount() == itemstack.getCount()) return ItemStack.EMPTY;

        ItemStack itemstack2 = slot.onTake(player, itemstack1);

        if (index == 0) player.dropItem(itemstack2, false);


        return new ItemStack(Items.BOW);
    }

    public void update(int slot)
    {
        if (player instanceof EntityPlayerMP)
        {
            ItemStack newStack = inventory.stackList.get(slot);
            if (!ItemMatcher.stacksMatch(newStack, previous.get(slot)))
            {
                if (slot < 9) syncSlot(slot);
                else
                {
                    Trading.TradeData data = Trading.TRADE_DATA.get(player);
                    EntityPlayerMP other = data.playerBesides((EntityPlayerMP) player);
                    ContainerTrade otherContainer = (ContainerTrade) other.openContainer;
                    otherContainer.inventorySlots.get(slot - 9).putStack(newStack);

                    data.p1Locked = false;
                    data.p2Locked = false;
                    data.p1Ready = false;
                    data.p2Ready = false;
                    data.sendUpdates();
                }

                previous.set(slot, MCTools.cloneItemStack(newStack));
            }
        }
    }

    protected void syncSlot(int slotIndex)
    {
        ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(windowId, slotIndex, inventorySlots.get(slotIndex).getStack()));
    }


    public static class InterfaceTrade implements IInteractionObject
    {
        private final World world;

        public InterfaceTrade(World world)
        {
            this.world = world;
        }

        public String getName()
        {
            return "Trade";
        }

        public boolean hasCustomName()
        {
            return false;
        }

        public ITextComponent getDisplayName()
        {
            return new TextComponentString(getName());
        }

        public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
        {
            return new ContainerTrade(playerIn, world);
        }

        public String getGuiID()
        {
            return MODID + ":bag";
        }
    }
}
