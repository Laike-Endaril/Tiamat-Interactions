package com.fantasticsource.tiamatinteractions.interaction;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public abstract class AInteraction
{
    public static final HashMap<String, AInteraction> INTERACTIONS = new HashMap<>();

    public final String name;

    public AInteraction(String name)
    {
        this.name = name;
    }

    public boolean available(Entity source, Entity target)
    {
        return false;
    }

    public boolean available(Entity source, BlockPos target)
    {
        return false;
    }

    /**
     * @return Whether we're done (true) or should return to the interaction menu (false)
     */
    public boolean execute(Entity source, Entity target)
    {
        return false;
    }

    /**
     * @return Whether we're done (true) or should return to the interaction menu (false)
     */
    public boolean execute(Entity source, BlockPos target)
    {
        return false;
    }
}
