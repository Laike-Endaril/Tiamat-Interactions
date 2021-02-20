package com.fantasticsource.tiamatinteractions.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * To add a custom interaction, just extend this class and edit the methods
 */
public abstract class AInteraction
{
    private static final LinkedHashMap<String, AInteraction> INTERACTIONS = new LinkedHashMap<>();


    public final String name;

    public AInteraction(String name)
    {
        this.name = name;
        register(this);
    }


    private static void register(AInteraction interaction)
    {
        if (INTERACTIONS.containsKey(interaction.name)) System.out.println(TextFormatting.YELLOW + "MULTIPLE INTERACTIONS WITH NAME: " + interaction.name);

        INTERACTIONS.put(interaction.name, interaction);
    }


    public abstract boolean available(EntityPlayerMP player, Vec3d hitVec, Entity target);

    public abstract boolean available(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos);

    /**
     * @return Whether we're done (true) or should return to the interaction menu (false)
     */
    public abstract boolean execute(EntityPlayerMP player, Vec3d hitVec, Entity target);

    /**
     * @return Whether we're done (true) or should return to the interaction menu (false)
     */
    public abstract boolean execute(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos);
}
