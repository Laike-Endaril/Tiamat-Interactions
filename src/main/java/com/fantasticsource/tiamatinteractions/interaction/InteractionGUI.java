package com.fantasticsource.tiamatinteractions.interaction;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.textured.GUIStretchedImage;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.tiamatinteractions.Network;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;

import java.util.LinkedHashMap;
import java.util.Map;

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

    public InteractionGUI(String title, LinkedHashMap<String, String> options, Vec3d hitVec, BlockPos blockPos)
    {
        super(0.5);

        internalScaling = textScale * 0.5;
        this.title = title;

        showUnstacked();

        root.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);

        root.add(makeLabel(title));
        for (Map.Entry<String, String> option : options.entrySet())
        {
            root.add(makeButton(I18n.translateToLocal(option.getKey())).addClickActions(() ->
            {
                Network.WRAPPER.sendToServer(new Network.RequestInteractionPacket(option.getValue(), hitVec, blockPos));
                close();
            }));
        }
        recalc();
    }

    public InteractionGUI(String title, LinkedHashMap<String, String> options, Vec3d hitVec, int entityID)
    {
        super(0.5);

        internalScaling = textScale * 0.5;
        this.title = title;

        showUnstacked();

        root.setSubElementAutoplaceMethod(GUIElement.AP_CENTER);

        root.add(makeLabel(title));
        for (Map.Entry<String, String> option : options.entrySet())
        {
            root.add(makeButton(I18n.translateToLocal(option.getKey())).addClickActions(() ->
            {
                Network.WRAPPER.sendToServer(new Network.RequestInteractionPacket(option.getValue(), hitVec, entityID));
                close();
            }));
        }
        recalc();
    }

    protected GUIElement makeLabel(String text)
    {
        GUIElement label = new GUIAutocroppedView(this, 0.4, new GUIStretchedImage(this, TEX_LABEL));
        label.add(new GUIText(this, text, Color.WHITE));
        return label;
    }

    protected GUIElement makeButton(String text)
    {
        GUIElement active = new GUIAutocroppedView(this, 0.4, new GUIStretchedImage(this, TEX_BUTTON_ACTIVE));
        active.add(new GUIText(this, text, Color.WHITE));

        GUIElement hover = new GUIAutocroppedView(this, 0.4, new GUIStretchedImage(this, TEX_BUTTON_HOVER));
        hover.add(new GUIText(this, text, hoverButtonColor));

        GUIElement idle = new GUIAutocroppedView(this, 0.4, new GUIStretchedImage(this, TEX_BUTTON_IDLE));
        idle.add(new GUIText(this, text, idleButtonColor));

        return new GUIButton(this, idle, hover, active);
    }

    @Override
    public String title()
    {
        return title;
    }
}
