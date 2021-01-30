package com.fantasticsource.tiamatinteractions.interaction;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.textured.GUIImage;
import com.fantasticsource.tiamatinteractions.Network;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

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
    protected String title;

    public InteractionGUI(String title, ArrayList<String> options, Vec3d hitVec, BlockPos blockPos)
    {
        super(0.5);

        internalScaling = textScale * 0.5;
        this.title = title;

        showUnstacked();

        root.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);

        root.add(makeLabel(title));
        for (String option : options)
        {
            root.add(makeButton(option).addClickActions(() ->
            {
                Network.WRAPPER.sendToServer(new Network.RequestInteractionPacket(option, hitVec, blockPos));
                close();
            }));
        }
        recalc();
    }

    public InteractionGUI(String title, ArrayList<String> options, Vec3d hitVec, int entityID)
    {
        super(0.5);

        internalScaling = textScale * 0.5;
        this.title = title;

        showUnstacked();

        root.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);

        root.add(makeLabel(title));
        for (String option : options)
        {
            root.add(makeButton(option).addClickActions(() ->
            {
                Network.WRAPPER.sendToServer(new Network.RequestInteractionPacket(option, hitVec, entityID));
                close();
            }));
        }
        recalc();
    }

    protected GUIImage makeLabel(String text)
    {
        GUIImage label = new GUIImage(this, 256 * internalScaling, 32 * internalScaling, TEX_LABEL);
        label.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        label.add(new GUIText(this, text, Color.WHITE));

        return label;
    }

    protected GUIButton makeButton(String text)
    {
        GUIImage active = new GUIImage(this, 256 * internalScaling, 32 * internalScaling, TEX_BUTTON_ACTIVE);
        active.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        active.add(new GUIText(this, text, Color.WHITE));

        GUIImage hover = new GUIImage(this, 256 * internalScaling, 32 * internalScaling, TEX_BUTTON_HOVER);
        hover.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        hover.add(new GUIText(this, text, hoverButtonColor));

        GUIImage idle = new GUIImage(this, 256 * internalScaling, 32 * internalScaling, TEX_BUTTON_IDLE);
        idle.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);
        idle.add(new GUIText(this, text, idleButtonColor));

        return new GUIButton(this, idle, hover, active);
    }

    @Override
    public String title()
    {
        return title;
    }
}
