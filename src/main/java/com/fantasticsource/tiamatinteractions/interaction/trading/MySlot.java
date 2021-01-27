package com.fantasticsource.tiamatinteractions.interaction.trading;

import com.fantasticsource.mctools.inventory.slot.BetterSlot;

public class MySlot extends BetterSlot
{
    private final ContainerTrade container;

    public MySlot(ContainerTrade container, int index, int x, int y, int u, int v)
    {
        super(container.inventory, index, x, y, ContainerTrade.TEXTURE, 256, 256, u, v);
        this.container = container;
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();
        container.update(slotNumber);
    }
}
