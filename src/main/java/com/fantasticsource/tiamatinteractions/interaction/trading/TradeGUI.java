package com.fantasticsource.tiamatinteractions.interaction.trading;

import com.fantasticsource.mctools.inventory.gui.BetterContainerGUI;
import com.fantasticsource.tiamatinteractions.Network;
import com.fantasticsource.tools.Collision;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class TradeGUI extends BetterContainerGUI
{
    public boolean locked = false, ready = false, otherLocked = false, otherReady = false;

    public TradeGUI()
    {
        super(new ContainerTrade(Minecraft.getMinecraft().player, Minecraft.getMinecraft().world));
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        //Main background
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(ContainerTrade.TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

        int x1 = (width >> 1) - (xSize >> 1);
        int y1 = (height >> 1) - (ySize >> 1);
        int x2 = x1 + xSize;
        int y2 = y1 + ySize;
        bufferbuilder.pos(x1, y2, zLevel).tex(0, (double) ySize / 256).endVertex();
        bufferbuilder.pos(x2, y2, zLevel).tex((double) xSize / 256, (double) ySize / 256).endVertex();
        bufferbuilder.pos(x2, y1, zLevel).tex((double) xSize / 256, 0).endVertex();
        bufferbuilder.pos(x1, y1, zLevel).tex(0, 0).endVertex();

        tessellator.draw();


        //Warning
        drawCenteredString(fontRenderer, "MAKE SURE YOU HAVE SPACE!", guiLeft + 88, guiTop + 67, 0xffff0000);
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        //Buttons
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(ContainerTrade.TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

        int x1 = 7, x2 = x1 + 16;
        int y1 = 27, y2 = y1 + 16;
        double u1 = locked ? 16d / 256 : 0, u2 = u1 + 16d / 256;
        double v1 = Collision.pointRectangle(mouseX - guiLeft, mouseY - guiTop, x1, y1, x2 - 1, y2 - 1) ? 224d / 256 : 240d / 256, v2 = v1 + 16d / 256;

        bufferbuilder.pos(x1, y2, zLevel).tex(u1, v2).endVertex();
        bufferbuilder.pos(x2, y2, zLevel).tex(u2, v2).endVertex();
        bufferbuilder.pos(x2, y1, zLevel).tex(u2, v1).endVertex();
        bufferbuilder.pos(x1, y1, zLevel).tex(u1, v1).endVertex();

        x1 = x2;
        x2 += 16;
        u1 = ready ? 48d / 256 : 32d / 256;
        u2 = u1 + 16d / 256;
        v1 = Collision.pointRectangle(mouseX - guiLeft, mouseY - guiTop, x1, y1, x2 - 1, y2 - 1) ? 224d / 256 : 240d / 256;
        v2 = v1 + 16d / 256;

        bufferbuilder.pos(x1, y2, zLevel).tex(u1, v2).endVertex();
        bufferbuilder.pos(x2, y2, zLevel).tex(u2, v2).endVertex();
        bufferbuilder.pos(x2, y1, zLevel).tex(u2, v1).endVertex();
        bufferbuilder.pos(x1, y1, zLevel).tex(u1, v1).endVertex();

        x1 = 153;
        x2 = x1 + 16;
        y1 = 25;
        y2 = y1 + 16;
        u1 = otherLocked ? 16d / 256 : 0;
        u2 = u1 + 16d / 256;
        v1 = 240d / 256;
        v2 = v1 + 16d / 256;

        bufferbuilder.pos(x1, y2, zLevel).tex(u1, v2).endVertex();
        bufferbuilder.pos(x2, y2, zLevel).tex(u2, v2).endVertex();
        bufferbuilder.pos(x2, y1, zLevel).tex(u2, v1).endVertex();
        bufferbuilder.pos(x1, y1, zLevel).tex(u1, v1).endVertex();

        x2 = x1;
        x1 -= 16;
        u1 = otherReady ? 48d / 256 : 32d / 256;
        u2 = u1 + 16d / 256;

        bufferbuilder.pos(x1, y2, zLevel).tex(u1, v2).endVertex();
        bufferbuilder.pos(x2, y2, zLevel).tex(u2, v2).endVertex();
        bufferbuilder.pos(x2, y1, zLevel).tex(u2, v1).endVertex();
        bufferbuilder.pos(x1, y1, zLevel).tex(u1, v1).endVertex();

        tessellator.draw();


        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();

        x1 = 7;
        x2 = 169;
        y1 = 43;
        y2 = 61;
        if (ready)
        {
            GlStateManager.color(0, 1, 0, 0.2f);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION);


            bufferbuilder.pos(x1, y2, zLevel).endVertex();
            bufferbuilder.pos(x2, y2, zLevel).endVertex();
            bufferbuilder.pos(x2, y1, zLevel).endVertex();
            bufferbuilder.pos(x1, y1, zLevel).endVertex();

            tessellator.draw();
        }
        else if (locked)
        {
            GlStateManager.color(1, 1, 0, 0.2f);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION);


            bufferbuilder.pos(x1, y2, zLevel).endVertex();
            bufferbuilder.pos(x2, y2, zLevel).endVertex();
            bufferbuilder.pos(x2, y1, zLevel).endVertex();
            bufferbuilder.pos(x1, y1, zLevel).endVertex();

            tessellator.draw();
        }

        y1 = 7;
        y2 = 25;
        if (otherReady)
        {
            GlStateManager.color(0, 1, 0, 0.2f);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION);


            bufferbuilder.pos(x1, y2, zLevel).endVertex();
            bufferbuilder.pos(x2, y2, zLevel).endVertex();
            bufferbuilder.pos(x2, y1, zLevel).endVertex();
            bufferbuilder.pos(x1, y1, zLevel).endVertex();

            tessellator.draw();
        }
        else if (otherLocked)
        {
            GlStateManager.color(1, 1, 0, 0.2f);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION);


            bufferbuilder.pos(x1, y2, zLevel).endVertex();
            bufferbuilder.pos(x2, y2, zLevel).endVertex();
            bufferbuilder.pos(x2, y1, zLevel).endVertex();
            bufferbuilder.pos(x1, y1, zLevel).endVertex();

            tessellator.draw();
        }

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int x1 = 7, x2 = x1 + 16;
        int y1 = 27, y2 = y1 + 16;
        if (Collision.pointRectangle(mouseX - guiLeft, mouseY - guiTop, x1, y1, x2 - 1, y2 - 1))
        {
            locked = !locked;
            Network.WRAPPER.sendToServer(new Network.LockTradePacket(locked));
        }
        else
        {
            x1 = x2;
            x2 += 16;
            if (Collision.pointRectangle(mouseX - guiLeft, mouseY - guiTop, x1, y1, x2 - 1, y2 - 1))
            {
                ready = !ready;
                Network.WRAPPER.sendToServer(new Network.ReadyTradePacket(ready));
            }
        }
    }
}
