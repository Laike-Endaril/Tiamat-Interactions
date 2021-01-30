package com.fantasticsource.tiamatinteractions.interaction.trading;

import com.fantasticsource.tiamatinteractions.api.AInteraction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class InteractionTrade extends AInteraction
{
    public InteractionTrade()
    {
        super("Trade");
    }

    @Override
    public boolean available(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        return target instanceof EntityPlayerMP;
    }

    @Override
    public boolean available(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        return false;
    }

    @Override
    public boolean execute(EntityPlayerMP player, Vec3d hitVec, Entity target)
    {
        Trading.tryStart(player, (EntityPlayerMP) target);
        return true;
    }

    @Override
    public boolean execute(EntityPlayerMP player, Vec3d hitVec, BlockPos blockPos)
    {
        return false;
    }
}
