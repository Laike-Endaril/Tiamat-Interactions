package com.fantasticsource.tiamatinteractions.interaction;

import com.fantasticsource.tiamatinteractions.Network;
import com.fantasticsource.tiamatinteractions.api.AInteraction;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Interactions
{
    private static final HashMap<String, AInteraction> INTERACTIONS = (HashMap<String, AInteraction>) ReflectionTool.get(AInteraction.class, "INTERACTIONS", null);

    public static void tryShowInteractionMenu(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        LinkedHashMap<String, String> availableInteractions = new LinkedHashMap<>();
        for (AInteraction interaction : INTERACTIONS.values())
        {
            String displayName = interaction.titleIfAvailable(player, hitVec, target);
            if (displayName != null) availableInteractions.put(displayName, interaction.name);
        }
        if (availableInteractions.size() > 0) Network.WRAPPER.sendTo(new Network.InteractionMenuPacket(target.getName(), availableInteractions, hitVec, target), player);
    }

    public static void tryShowInteractionMenu(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        LinkedHashMap<String, String> availableInteractions = new LinkedHashMap<>();
        for (AInteraction interaction : INTERACTIONS.values())
        {
            String displayName = interaction.titleIfAvailable(player, hitVec, blockPos);
            if (displayName != null) availableInteractions.put(displayName, interaction.name);
        }
        if (availableInteractions.size() > 0) Network.WRAPPER.sendTo(new Network.InteractionMenuPacket(player.world.getBlockState(blockPos).getBlock().getUnlocalizedName(), availableInteractions, hitVec, blockPos), player);
    }


    public static boolean tryInteraction(EntityPlayerMP player, String interactionName, Vec3d hitVec, Entity target)
    {
        AInteraction interaction = INTERACTIONS.get(interactionName);
        if (interaction == null || interaction.titleIfAvailable(player, hitVec, target) == null) return false;
        return interaction.execute(player, hitVec, target);
    }

    public static boolean tryInteraction(EntityPlayerMP player, String interactionName, Vec3d hitVec, BlockPos blockPos)
    {
        AInteraction interaction = INTERACTIONS.get(interactionName);
        if (interaction == null || interaction.titleIfAvailable(player, hitVec, blockPos) == null) return false;
        return interaction.execute(player, hitVec, blockPos);
    }
}
