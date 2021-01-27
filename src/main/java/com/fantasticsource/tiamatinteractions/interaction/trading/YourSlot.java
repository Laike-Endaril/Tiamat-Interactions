package com.fantasticsource.tiamatinteractions.interaction.trading;

import com.fantasticsource.mctools.inventory.slot.FilteredSlot;
import net.minecraft.entity.player.EntityPlayer;

public class YourSlot extends FilteredSlot
{
    private final ContainerTrade container;

    public YourSlot(ContainerTrade container, int index, int x, int y, int u, int v)
    {
        super(container.inventory, index, x, y, ContainerTrade.TEXTURE, 256, 256, u, v, false, 64, stack -> false);
        this.container = container;
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();
        container.update(slotNumber);
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn)
    {
        return false;
    }
}
