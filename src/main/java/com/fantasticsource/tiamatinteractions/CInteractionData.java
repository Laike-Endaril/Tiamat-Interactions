package com.fantasticsource.tiamatinteractions;

import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class CInteractionData extends Component
{
    public static LinkedHashMap<GameType, CInteractionData> data = new LinkedHashMap<>();

    static
    {
        data.put(GameType.ADVENTURE, new CInteractionData(GameType.ADVENTURE, TiamatInteractionsConfig.adventureBlockListIsWhitelist, TiamatInteractionsConfig.adventureBlockList));
        data.put(GameType.SURVIVAL, new CInteractionData(GameType.SURVIVAL, TiamatInteractionsConfig.survivalBlockListIsWhitelist, TiamatInteractionsConfig.survivalBlockList));
        data.put(GameType.CREATIVE, new CInteractionData(GameType.CREATIVE, TiamatInteractionsConfig.creativeBlockListIsWhitelist, TiamatInteractionsConfig.creativeBlockList));
    }


    public GameType gameType;
    public boolean blockListIsWhitelist;
    public ArrayList<String> blockList = new ArrayList<>();

    public CInteractionData()
    {
        //Required
    }

    public CInteractionData(GameType gameType, boolean blockListIsWhitelist, String... blockList)
    {
        this.gameType = gameType;
        this.blockListIsWhitelist = blockListIsWhitelist;
        this.blockList.addAll(Arrays.asList(blockList));
    }


    @Override
    public CInteractionData write(ByteBuf buf)
    {
        buf.writeInt(gameType.getID());
        buf.writeBoolean(blockListIsWhitelist);

        buf.writeInt(blockList.size());
        for (String block : blockList) ByteBufUtils.writeUTF8String(buf, block);

        return this;
    }

    @Override
    public CInteractionData read(ByteBuf buf)
    {
        gameType = GameType.getByID(buf.readInt());
        blockListIsWhitelist = buf.readBoolean();

        blockList.clear();
        for (int i = buf.readInt(); i > 0; i--) blockList.add(ByteBufUtils.readUTF8String(buf));

        return this;
    }

    @Override
    public CInteractionData save(OutputStream stream)
    {
        CInt ci = new CInt().set(gameType.getID()).save(stream);
        new CBoolean().set(blockListIsWhitelist).save(stream);

        CStringUTF8 cs = new CStringUTF8();
        ci.set(blockList.size()).save(stream);
        for (String block : blockList) cs.set(block).save(stream);

        return this;
    }

    @Override
    public CInteractionData load(InputStream stream)
    {
        CInt ci = new CInt();
        CStringUTF8 cs = new CStringUTF8();

        gameType = GameType.getByID(ci.load(stream).value);
        blockListIsWhitelist = new CBoolean().load(stream).value;

        blockList.clear();
        for (int i = ci.load(stream).value; i > 0; i--) blockList.add(cs.load(stream).value);

        return this;
    }
}
