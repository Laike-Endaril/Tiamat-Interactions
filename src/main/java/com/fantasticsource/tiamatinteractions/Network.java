package com.fantasticsource.tiamatinteractions;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.GameType;
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
}
