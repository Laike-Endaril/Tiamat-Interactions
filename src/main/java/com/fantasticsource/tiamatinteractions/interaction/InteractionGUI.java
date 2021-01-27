package com.fantasticsource.tiamatinteractions.interaction;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.textured.GUIImage;
import com.fantasticsource.tiamatinteractions.Network;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import static com.fantasticsource.tiamatinteractions.TiamatInteractions.MODID;

public class InteractionGUI extends GUIScreen
{
    public static final ResourceLocation
            TEX_LABEL = new ResourceLocation(MODID, "image/label.png"),
            TEX_BUTTON_IDLE = new ResourceLocation(MODID, "image/button_idle.png"),
            TEX_BUTTON_HOVER = new ResourceLocation(MODID, "image/button_hover.png"),
            TEX_BUTTON_ACTIVE = new ResourceLocation(MODID, "image/button_active.png");

    protected static final Color
            hoverButtonColor = new Color("BBBBBB", true),
            idleButtonColor = new Color("777777", true);

    protected double internalScaling;

    public Entity other;

    public InteractionGUI(Entity other)
    {
        super(0.5);

        if (other instanceof EntityPlayer)
        {
            this.other = other;
            internalScaling = textScale * 0.5;

            showUnstacked();

            root.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);

            root.add(makeLabel(other.getName()));
            root.add(makeButton("Trade").addClickActions(() ->
            {
                Network.WRAPPER.sendToServer(new Network.RequestTradePacket(other.getName()));
                close();
            }));
            recalc();
        }
    }

    protected GUIImage makeLabel(String text)
    {
        GUIImage label = new GUIImage(this, 128 * internalScaling, 32 * internalScaling, TEX_LABEL);
        label.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        label.add(new GUIText(this, text, Color.WHITE));

        return label;
    }

    protected GUIButton makeButton(String text)
    {
        GUIImage active = new GUIImage(this, 128 * internalScaling, 32 * internalScaling, TEX_BUTTON_ACTIVE);
        active.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        active.add(new GUIText(this, text, Color.WHITE));

        GUIImage hover = new GUIImage(this, 128 * internalScaling, 32 * internalScaling, TEX_BUTTON_HOVER);
        hover.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        hover.add(new GUIText(this, text, hoverButtonColor));

        GUIImage idle = new GUIImage(this, 128 * internalScaling, 32 * internalScaling, TEX_BUTTON_IDLE);
        idle.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        idle.add(new GUIText(this, text, idleButtonColor));

        return new GUIButton(this, idle, hover, active);
    }

    @Override
    public String title()
    {
        return other.getName();
    }
}
