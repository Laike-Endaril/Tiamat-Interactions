package com.fantasticsource.tiamatinteractions.interaction;

import com.fantasticsource.tiamatinteractions.Network;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AInteraction
{
    public static final HashMap<String, AInteraction> INTERACTIONS = new HashMap<>();

    public final String name;

    public AInteraction(String name)
    {
        if (INTERACTIONS.containsKey(name)) System.out.println(TextFormatting.YELLOW + "MULTIPLE INTERACTIONS WITH NAME: " + name);

        this.name = name;
        INTERACTIONS.put(name, this);
    }


    public static void tryShowInteractionMenu(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        ArrayList<String> availableInteractions = new ArrayList<>();
        for (AInteraction interaction : INTERACTIONS.values())
        {
            if (interaction.available(player, hitVec, target)) availableInteractions.add(interaction.name);
        }
        if (availableInteractions.size() > 0) Network.WRAPPER.sendTo(new Network.InteractionMenuPacket(target.getName(), availableInteractions, hitVec, target), player);
    }

    public static void tryShowInteractionMenu(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        ArrayList<String> availableInteractions = new ArrayList<>();
        for (AInteraction interaction : INTERACTIONS.values())
        {
            if (interaction.available(player, hitVec, blockPos)) availableInteractions.add(interaction.name);
        }
        if (availableInteractions.size() > 0) Network.WRAPPER.sendTo(new Network.InteractionMenuPacket(player.world.getBlockState(blockPos).getBlock().getUnlocalizedName(), availableInteractions, hitVec, blockPos), player);
    }


    public static boolean tryInteraction(EntityPlayerMP player, String interactionName, Vec3d hitVec, Entity target)
    {
        AInteraction interaction = INTERACTIONS.get(interactionName);
        if (interaction == null || !interaction.available(player, hitVec, target)) return false;
        return interaction.execute(player, hitVec, target);
    }

    public static boolean tryInteraction(EntityPlayerMP player, String interactionName, Vec3d hitVec, BlockPos blockPos)
    {
        AInteraction interaction = INTERACTIONS.get(interactionName);
        if (interaction == null || !interaction.available(player, hitVec, blockPos)) return false;
        return interaction.execute(player, hitVec, blockPos);
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
