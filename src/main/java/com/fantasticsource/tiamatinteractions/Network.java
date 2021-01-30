package com.fantasticsource.tiamatinteractions;

import com.fantasticsource.tiamatinteractions.interaction.InteractionGUI;
import com.fantasticsource.tiamatinteractions.interaction.Interactions;
import com.fantasticsource.tiamatinteractions.interaction.trading.TradeGUI;
import com.fantasticsource.tiamatinteractions.interaction.trading.Trading;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.GameType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.tiamatinteractions.TiamatInteractions.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(SyncConfigPacketHandler.class, SyncConfigPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(LockTradePacketHandler.class, LockTradePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(ReadyTradePacketHandler.class, ReadyTradePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(TradePacketHandler.class, TradePacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(UpdateTradePacketHandler.class, UpdateTradePacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(BlockInteractionPacketHandler.class, BlockInteractionPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(InteractionMenuPacketHandler.class, InteractionMenuPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(RequestInteractionPacketHandler.class, RequestInteractionPacket.class, discriminator++, Side.SERVER);
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

    public static class ReadyTradePacketHandler implements IMessageHandler<ReadyTradePacket, IMessage>
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
                Minecraft.getMinecraft().addScheduledTask(ClientProxy::showTradeGUI);
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


    public static class BlockInteractionPacket implements IMessage
    {
        BlockPos pos;
        EnumFacing facing;
        Vec3d hitVec;
        EnumHand hand;

        public BlockInteractionPacket() //Required; probably for when the packet is received
        {
        }

        public BlockInteractionPacket(BlockPos pos)
        {
            this(pos, EnumFacing.UP, new Vec3d(pos), EnumHand.MAIN_HAND);
        }

        public BlockInteractionPacket(PlayerInteractEvent.RightClickBlock event)
        {
            this(event.getPos(), event.getFace(), event.getHitVec(), event.getHand());
        }

        public BlockInteractionPacket(BlockPos pos, EnumFacing facing, Vec3d hitVec, EnumHand hand)
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

    public static class BlockInteractionPacketHandler implements IMessageHandler<BlockInteractionPacket, IMessage>
    {
        @Override
        public IMessage onMessage(BlockInteractionPacket packet, MessageContext ctx)
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


    public static class InteractionMenuPacket implements IMessage
    {
        String title;
        ArrayList<String> options;
        Vec3d hitVec;
        BlockPos blockPos = null;
        int entityID;

        public InteractionMenuPacket() //Required; probably for when the packet is received
        {
        }

        public InteractionMenuPacket(String title, ArrayList<String> options, Vec3d hitVec, Entity entity)
        {
            this.title = title;
            this.options = options;
            this.hitVec = hitVec;
            entityID = entity.getEntityId();
        }

        public InteractionMenuPacket(String title, ArrayList<String> options, Vec3d hitVec, BlockPos blockPos)
        {
            this.title = title;
            this.options = options;
            this.hitVec = hitVec;
            this.blockPos = blockPos;
        }


        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, title);
            buf.writeInt(options.size());
            for (String option : options) ByteBufUtils.writeUTF8String(buf, option);

            buf.writeDouble(hitVec.x);
            buf.writeDouble(hitVec.y);
            buf.writeDouble(hitVec.z);

            buf.writeBoolean(blockPos != null);
            if (blockPos != null)
            {
                buf.writeInt(blockPos.getX());
                buf.writeInt(blockPos.getY());
                buf.writeInt(blockPos.getZ());
            }
            else buf.writeInt(entityID);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            title = ByteBufUtils.readUTF8String(buf);
            options = new ArrayList<>();
            for (int i = buf.readInt(); i > 0; i--) options.add(ByteBufUtils.readUTF8String(buf));

            hitVec = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());

            if (buf.readBoolean()) blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
            else entityID = buf.readInt();
        }
    }

    public static class InteractionMenuPacketHandler implements IMessageHandler<InteractionMenuPacket, IMessage>
    {
        @Override
        public IMessage onMessage(InteractionMenuPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    if (packet.blockPos != null) new InteractionGUI(I18n.translateToLocal(packet.title), packet.options, packet.hitVec, packet.blockPos);
                    else new InteractionGUI(packet.title, packet.options, packet.hitVec, packet.entityID);
                });
            }

            return null;
        }
    }


    public static class RequestInteractionPacket implements IMessage
    {
        public String interaction;
        public Vec3d hitVec;
        public BlockPos blockPos = null;
        public int entityID;

        public RequestInteractionPacket()
        {
            //Required
        }

        public RequestInteractionPacket(String interaction, Vec3d hitVec, BlockPos blockPos)
        {
            this.interaction = interaction;
            this.hitVec = hitVec;
            this.blockPos = blockPos;
        }

        public RequestInteractionPacket(String interaction, Vec3d hitVec, int entityID)
        {
            this.interaction = interaction;
            this.hitVec = hitVec;
            this.entityID = entityID;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, interaction);

            buf.writeDouble(hitVec.x);
            buf.writeDouble(hitVec.y);
            buf.writeDouble(hitVec.z);

            buf.writeBoolean(blockPos != null);
            if (blockPos != null)
            {
                buf.writeInt(blockPos.getX());
                buf.writeInt(blockPos.getY());
                buf.writeInt(blockPos.getZ());
            }
            else buf.writeInt(entityID);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            interaction = ByteBufUtils.readUTF8String(buf);

            hitVec = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());

            if (buf.readBoolean()) blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
            else entityID = buf.readInt();
        }
    }

    public static class RequestInteractionPacketHandler implements IMessageHandler<RequestInteractionPacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestInteractionPacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                if (packet.blockPos != null)
                {
                    if (!Interactions.tryInteraction(player, packet.interaction, packet.hitVec, packet.blockPos))
                    {
                        Interactions.tryShowInteractionMenu(player, packet.hitVec, packet.blockPos);
                    }
                }
                else
                {
                    Entity entity = player.world.getEntityByID(packet.entityID);
                    if (!Interactions.tryInteraction(player, packet.interaction, packet.hitVec, entity))
                    {
                        Interactions.tryShowInteractionMenu(player, packet.hitVec, entity);
                    }
                }
            });
            return null;
        }
    }
}
