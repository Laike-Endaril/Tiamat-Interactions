package com.fantasticsource.tiamatinteractions.interaction;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.HashMap;

public abstract class AInteraction
{
    public static boolean doDefault = false;
    public static final HashMap<String, AInteraction> INTERACTIONS = new HashMap<>();

    public final String name;

    public AInteraction(String name)
    {
        if (INTERACTIONS.containsKey(name)) System.out.println(TextFormatting.YELLOW + "MULTIPLE INTERACTIONS WITH NAME: " + name);

        this.name = name;
        INTERACTIONS.put(name, this);
    }

    public abstract boolean available(PlayerInteractEvent.EntityInteractSpecific event);

    public abstract boolean available(PlayerInteractEvent.RightClickBlock event);

    /**
     * @return Whether we're done (true) or should return to the interaction menu (false)
     */
    public abstract boolean execute(PlayerInteractEvent.EntityInteractSpecific event);

    /**
     * @return Whether we're done (true) or should return to the interaction menu (false)
     */
    public abstract boolean execute(PlayerInteractEvent.RightClickBlock event);


    public static class DefaultInteraction extends AInteraction
    {
        public DefaultInteraction()
        {
            super("Interact");
        }

        @Override
        public boolean available(PlayerInteractEvent.EntityInteractSpecific event)
        {
            return true;
        }

        @Override
        public boolean available(PlayerInteractEvent.RightClickBlock event)
        {
            return true;
        }

        @Override
        public boolean execute(PlayerInteractEvent.EntityInteractSpecific event)
        {
            doDefault = true;
            //TODO
            doDefault = false;

            return false;
        }

        @Override
        public boolean execute(PlayerInteractEvent.RightClickBlock event)
        {
            doDefault = true;
            //TODO
            doDefault = false;

            return false;
        }
    }
}
