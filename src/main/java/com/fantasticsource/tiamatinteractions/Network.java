package com.fantasticsource.tiamatinteractions;

import com.fantasticsource.mctools.PlayerData;
import com.fantasticsource.tiamatinteractions.interaction.trading.TradeGUI;
import com.fantasticsource.tiamatinteractions.interaction.trading.Trading;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;

import static com.fantasticsource.tiamatinteractions.TiamatInteractions.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(SyncConfigPacketHandler.class, SyncConfigPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(RequestTradePacketHandler.class, RequestTradePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(LockTradePacketHandler.class, LockTradePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(CompleteTradePacketHandler.class, ReadyTradePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(TradePacketHandler.class, TradePacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(UpdateTradePacketHandler.class, UpdateTradePacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(InteractPacketHandler.class, InteractPacket.class, discriminator++, Side.CLIENT);
    }


    public static class SyncConfigPacket implements IMessage
    {
        public LinkedHashMap<GameType, CInteractionData> data = new LinkedHashMap<>();

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(CInteractionData.data.size());
            for (CInteractionData data : CInteractionData.data.values()) data.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            CInteractionData data;
            for (int i = buf.readInt(); i > 0; i--)
            {
                data = new CInteractionData().read(buf);
                this.data.put(data.gameType, data);
            }
        }
    }

    public static class SyncConfigPacketHandler implements IMessageHandler<SyncConfigPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(SyncConfigPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> CInteractionData.data = packet.data);
            return null;
        }
    }


    public static class RequestTradePacket implements IMessage
    {
        String playerName;

        public RequestTradePacket()
        {
            //Required
        }

        public RequestTradePacket(String playerName)
        {
            this.playerName = playerName;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, playerName);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            playerName = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class RequestTradePacketHandler implements IMessageHandler<RequestTradePacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestTradePacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> Trading.tryStart(ctx.getServerHandler().player, (EntityPlayerMP) PlayerData.getEntity(packet.playerName)));
            return null;
        }
    }


    public static class LockTradePacket implements IMessage
    {
        public boolean lock;

        public LockTradePacket()
        {
            //Required
        }

        public LockTradePacket(boolean lock)
        {
            this.lock = lock;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(lock);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            lock = buf.readBoolean();
        }
    }

    public static class LockTradePacketHandler implements IMessageHandler<LockTradePacket, IMessage>
    {
        @Override
        public IMessage onMessage(LockTradePacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> Trading.tryLock(ctx.getServerHandler().player, packet.lock));
            return null;
        }
    }


    public static class ReadyTradePacket implements IMessage
    {
        public boolean ready;

        public ReadyTradePacket()
        {
            //Required
        }

        public ReadyTradePacket(boolean ready)
        {
            this.ready = ready;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(ready);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            ready = buf.readBoolean();
        }
    }

    public static class CompleteTradePacketHandler implements IMessageHandler<ReadyTradePacket, IMessage>
    {
        @Override
        public IMessage onMessage(ReadyTradePacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> Trading.tryReady(ctx.getServerHandler().player, packet.ready));
            return null;
        }
    }


    public static class TradePacket implements IMessage
    {
        public TradePacket() //Required; probably for when the packet is received
        {
        }


        @Override
        public void toBytes(ByteBuf buf)
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
        }
    }

    public static class TradePacketHandler implements IMessageHandler<TradePacket, IMessage>
    {
        @Override
        public IMessage onMessage(TradePacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft mc = Minecraft.getMinecraft();
                mc.addScheduledTask(ClientProxy::showTradeGUI);
            }

            return null;
        }
    }


    public static class UpdateTradePacket implements IMessage
    {
        boolean locked, ready, otherLocked, otherReady;

        public UpdateTradePacket() //Required; probably for when the packet is received
        {
        }

        public UpdateTradePacket(boolean locked, boolean ready, boolean otherLocked, boolean otherReady)
        {
            this.locked = locked;
            this.ready = ready;
            this.otherLocked = otherLocked;
            this.otherReady = otherReady;
        }


        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(locked);
            buf.writeBoolean(ready);
            buf.writeBoolean(otherLocked);
            buf.writeBoolean(otherReady);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            locked = buf.readBoolean();
            ready = buf.readBoolean();
            otherLocked = buf.readBoolean();
            otherReady = buf.readBoolean();
        }
    }

    public static class UpdateTradePacketHandler implements IMessageHandler<UpdateTradePacket, IMessage>
    {
        @Override
        public IMessage onMessage(UpdateTradePacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft mc = Minecraft.getMinecraft();
                mc.addScheduledTask(() ->
                {
                    if (mc.currentScreen instanceof TradeGUI)
                    {
                        TradeGUI gui = (TradeGUI) mc.currentScreen;
                        gui.locked = packet.locked;
                        gui.ready = packet.ready;
                        gui.otherLocked = packet.otherLocked;
                        gui.otherReady = packet.otherReady;
                    }
                });
            }

            return null;
        }
    }


    public static class InteractPacket implements IMessage
    {
        BlockPos pos;
        EnumFacing facing;
        Vec3d hitVec;
        EnumHand hand;

        public InteractPacket() //Required; probably for when the packet is received
        {
        }

        public InteractPacket(BlockPos pos)
        {
            this(pos, EnumFacing.UP, new Vec3d(pos), EnumHand.MAIN_HAND);
        }

        public InteractPacket(BlockPos pos, EnumFacing facing, Vec3d hitVec, EnumHand hand)
        {
            this.pos = pos;
            this.facing = facing;
            this.hitVec = hitVec;
            this.hand = hand;
        }


        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());

            buf.writeInt(facing.getIndex());

            buf.writeDouble(hitVec.x);
            buf.writeDouble(hitVec.y);
            buf.writeDouble(hitVec.z);

            buf.writeInt(hand.ordinal());
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
            facing = EnumFacing.getFront(buf.readInt());
            hitVec = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            hand = EnumHand.values()[buf.readInt()];
        }
    }

    public static class InteractPacketHandler implements IMessageHandler<InteractPacket, IMessage>
    {
        @Override
        public IMessage onMessage(InteractPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft mc = Minecraft.getMinecraft();
                mc.addScheduledTask(() ->
                {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, packet.pos, packet.facing, packet.hitVec, packet.hand);
                });
            }

            return null;
        }
    }
}
