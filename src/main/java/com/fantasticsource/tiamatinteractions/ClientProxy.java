package com.fantasticsource.tiamatinteractions;

import com.fantasticsource.tiamatinteractions.interaction.trading.TradeGUI;
import net.minecraft.client.Minecraft;

public class ClientProxy
{
    public static void showTradeGUI()
    {
        Minecraft.getMinecraft().displayGuiScreen(new TradeGUI());
    }
}
