package com.fantasticsource.tiamatinteractions;

import com.fantasticsource.mctools.MCTools;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

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
        public boolean op;
        public boolean blockListIsWhitelist;
        public ArrayList<String> blockList;

        public SyncConfigPacket()
        {
            //Required
        }

        public SyncConfigPacket(EntityPlayerMP player)
        {
            op = MCTools.isOP(player);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(op);

            buf.writeBoolean(TiamatInteractionsConfig.blockListIsWhitelist);

            buf.writeInt(TiamatInteractionsConfig.blockList.length);
            for (String block : TiamatInteractionsConfig.blockList) ByteBufUtils.writeUTF8String(buf, block);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            op = buf.readBoolean();

            blockListIsWhitelist = buf.readBoolean();

            blockList = new ArrayList<>();
            for (int i = buf.readInt(); i > 0; i--)
            {
                blockList.add(ByteBufUtils.readUTF8String(buf));
            }
        }
    }

    public static class SyncConfigPacketHandler implements IMessageHandler<SyncConfigPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(SyncConfigPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                TiamatInteractionsData.weAreOP = packet.op;
                TiamatInteractionsData.blockListIsWhitelist = packet.blockListIsWhitelist;
                TiamatInteractionsData.blockList = packet.blockList;
            });
            return null;
        }
    }
}
