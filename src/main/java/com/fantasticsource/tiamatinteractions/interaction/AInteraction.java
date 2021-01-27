package com.fantasticsource.tiamatinteractions.interaction;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.HashMap;

public abstract class AInteraction
{
    public static final HashMap<String, AInteraction> INTERACTIONS = new HashMap<>();

    static
    {
        new
    }

    public final String name;

    public AInteraction(String name)
    {
        if (INTERACTIONS.containsKey(name)) System.out.println(TextFormatting.YELLOW + "MULTIPLE INTERACTIONS WITH NAME: " + name);

        this.name = name;
        INTERACTIONS.put(name, this);
    }

    public boolean available(PlayerInteractEvent.EntityInteractSpecific event)
    {
        return false;
    }

    public boolean available(PlayerInteractEvent.RightClickBlock event)
    {
        return false;
    }

    /**
     * @return Whether we're done (true) or should return to the interaction menu (false)
     */
    public boolean execute(PlayerInteractEvent.EntityInteractSpecific event)
    {
        return false;
    }

    /**
     * @return Whether we're done (true) or should return to the interaction menu (false)
     */
    public boolean execute(PlayerInteractEvent.RightClickBlock event)
    {
        return false;
    }
}
