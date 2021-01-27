package com.fantasticsource.tiamatinteractions.interaction.trading;

import com.fantasticsource.tiamatinteractions.Network;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class Trading
{
    public static final HashMap<EntityPlayerMP, TradeData> TRADE_DATA = new HashMap<>();

    public static void tryStart(EntityPlayerMP p1, EntityPlayerMP p2)
    {
        if (p1 == p2) p1.sendMessage(new TextComponentString(TextFormatting.RED + "You attempt to trade with yourself, but fail"));
        else if (p1.dimension != 0) p1.sendMessage(new TextComponentString(TextFormatting.RED + "You can only trade in town"));
        else if (p2 == null) p1.sendMessage(new TextComponentString(TextFormatting.RED + "They seem to have vanished"));
        else if (p2.world != p1.world || p2.getDistanceSq(p1) > 16) p1.sendMessage(new TextComponentString(TextFormatting.RED + p2.getName() + " is too far away to trade"));
        else
        {
            TradeData data2 = TRADE_DATA.get(p2);

            if (data2 == null)
            {
                TRADE_DATA.put(p1, new TradeData(p1, p2));
                p1.sendMessage(new TextComponentString(TextFormatting.AQUA + "Requested a trade with " + p2.getName()));
                p2.sendMessage(new TextComponentString(TextFormatting.AQUA + p1.getName() + " requests a trade"));
            }
            else
            {
                EntityPlayerMP p3 = data2.playerBesides(p2);

                if (p3 == p1)
                {
                    TRADE_DATA.put(p1, data2);
                    start(data2);
                }
                else if (TRADE_DATA.containsKey(p3) && TRADE_DATA.get(p3).playerBesides(p3) == p2)
                {
                    p1.sendMessage(new TextComponentString(TextFormatting.AQUA + p2.getName() + " is busy right now"));
                }
                else
                {
                    TRADE_DATA.put(p1, new TradeData(p1, p2));
                    p1.sendMessage(new TextComponentString(TextFormatting.AQUA + "Requested a trade with " + p2.getName()));
                    p2.sendMessage(new TextComponentString(TextFormatting.AQUA + p1.getName() + " requests a trade"));
                }
            }
        }
    }

    protected static void start(TradeData data)
    {
        EntityPlayerMP p1 = data.p1, p2 = data.p2;

        Network.WRAPPER.sendTo(new Network.TradePacket(), p1);
        Network.WRAPPER.sendTo(new Network.TradePacket(), p2);

        ContainerTrade.InterfaceTrade iface1 = new ContainerTrade.InterfaceTrade(p1.world), iface2 = new ContainerTrade.InterfaceTrade(p2.world);
        data.p1Interface = iface1;
        data.p2Interface = iface2;

        p1.getNextWindowId();
        p2.getNextWindowId();

        p1.connection.sendPacket(new SPacketOpenWindow(p1.currentWindowId, iface1.getGuiID(), iface1.getDisplayName()));
        p2.connection.sendPacket(new SPacketOpenWindow(p2.currentWindowId, iface2.getGuiID(), iface2.getDisplayName()));

        p1.openContainer = iface1.createContainer(p1.inventory, p1);
        p2.openContainer = iface2.createContainer(p2.inventory, p2);

        p1.openContainer.windowId = p1.currentWindowId;
        p2.openContainer.windowId = p2.currentWindowId;

        p1.openContainer.addListener(p1);
        p2.openContainer.addListener(p2);

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(p1, p1.openContainer));
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(p2, p2.openContainer));
    }


    public static void tryLock(EntityPlayerMP p1, boolean lock)
    {
        TradeData data = TRADE_DATA.get(p1);
        if (data == null)
        {
            p1.sendMessage(new TextComponentString(TextFormatting.RED + "Something went wrong; closing trade GUI"));
            if (p1.openContainer instanceof ContainerTrade) p1.closeScreen();
        }
        else
        {
            EntityPlayerMP p2 = data.playerBesides(p1);

            if (p1 == p2)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + "You attempt to trade with yourself, but fail"));
                if (p1.openContainer instanceof ContainerTrade) p1.closeScreen();
            }
            else if (p1.dimension != 0)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + "You can only trade in town"));
                if (p1.openContainer instanceof ContainerTrade) p1.closeScreen();
            }
            else if (p2 == null)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + "They seem to have vanished"));
                if (p1.openContainer instanceof ContainerTrade) p1.closeScreen();
            }
            else if (p2.world != p1.world || p2.getDistanceSq(p1) > 16)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + p2.getName() + " is too far away to trade"));
                if (p1.openContainer instanceof ContainerTrade) p1.closeScreen();
            }
            else
            {
                if (p1 == data.p1) data.p1Locked = lock;
                else data.p2Locked = lock;
                data.p1Ready = false;
                data.p2Ready = false;
                data.sendUpdates();
            }
        }
    }


    public static void tryReady(EntityPlayerMP p1, boolean complete)
    {
        TradeData data = TRADE_DATA.get(p1);
        if (data == null)
        {
            p1.sendMessage(new TextComponentString(TextFormatting.RED + "Something went wrong; closing trade GUI"));
            if (p1.openContainer instanceof ContainerTrade) p1.closeScreen();
        }
        else
        {
            EntityPlayerMP p2 = data.playerBesides(p1);

            if (p1 == p2)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + "You attempt to trade with yourself, but fail"));
                if (p1.openContainer instanceof ContainerTrade) p1.closeScreen();
            }
            else if (p1.dimension != 0)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + "You can only trade in town"));
                if (p1.openContainer instanceof ContainerTrade) p1.closeScreen();
            }
            else if (p2 == null)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + "They seem to have vanished"));
                if (p1.openContainer instanceof ContainerTrade) p1.closeScreen();
            }
            else if (p2.world != p1.world || p2.getDistanceSq(p1) > 16)
            {
                p1.sendMessage(new TextComponentString(TextFormatting.RED + p2.getName() + " is too far away to trade"));
                if (p1.openContainer instanceof ContainerTrade) p1.closeScreen();
            }
            else if (data.p1Locked && data.p2Locked)
            {
                if (p1 == data.p1) data.p1Ready = complete;
                else data.p2Ready = complete;
                if (data.p1Ready && data.p2Ready) complete(data);
                else data.sendUpdates();
            }
            else
            {
                data.p1Ready = false;
                data.p2Ready = false;
                data.sendUpdates();
            }
        }
    }

    protected static void complete(TradeData data)
    {
        NonNullList<ItemStack> p1Stacks = ((ContainerTrade) data.p1.openContainer).inventory.stackList;
        NonNullList<ItemStack> p2Stacks = ((ContainerTrade) data.p2.openContainer).inventory.stackList;
        ArrayList<ItemStack> swap = new ArrayList<>(p1Stacks);
        for (int i = 0; i < swap.size(); i++)
        {
            p1Stacks.set(i, p2Stacks.get(i));
            p2Stacks.set(i, swap.get(i));
        }

        data.p1Locked = false;
        data.p2Locked = false;
        data.p1Ready = false;
        data.p2Ready = false;
        data.sendUpdates();
    }


    public static class TradeData
    {
        public final EntityPlayerMP p1, p2;
        public boolean p1Locked = false, p2Locked = false, p1Ready = false, p2Ready = false;
        public ContainerTrade.InterfaceTrade p1Interface = null, p2Interface = null;

        public TradeData(EntityPlayerMP p1, EntityPlayerMP p2)
        {
            this.p1 = p1;
            this.p2 = p2;
        }

        public boolean hasPlayer(EntityPlayerMP player)
        {
            return player == p1 || player == p2;
        }

        public EntityPlayerMP playerBesides(EntityPlayerMP player)
        {
            if (player == p1) return p2;
            if (player == p2) return p1;
            throw new IllegalArgumentException("Player must be one of the players involved");
        }

        public void sendUpdates()
        {
            Network.WRAPPER.sendTo(new Network.UpdateTradePacket(p1Locked, p1Ready, p2Locked, p2Ready), p1);
            Network.WRAPPER.sendTo(new Network.UpdateTradePacket(p2Locked, p2Ready, p1Locked, p1Ready), p2);
        }
    }
}
