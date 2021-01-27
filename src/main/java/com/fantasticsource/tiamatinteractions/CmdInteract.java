package com.fantasticsource.tiamatinteractions;

import com.fantasticsource.mctools.PlayerData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.fantasticsource.tiamatinteractions.TiamatInteractions.MODID;

public class CmdInteract extends CommandBase
{
    @Override
    public String getName()
    {
        return "interact";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        if (sender.canUseCommand(getRequiredPermissionLevel(), getName())) return MODID + ".cmd." + getName() + ".usage";

        return "commands.generic.permission";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        BlockPos blockPos;
        try
        {
            blockPos = new BlockPos(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        }
        catch (NumberFormatException e)
        {
            notifyCommandListener(sender, this, getUsage(sender));
            return;
        }

        EntityPlayerMP player = null;
        if (args.length > 3) player = (EntityPlayerMP) PlayerData.getEntity(args[3]);
        if (player == null && sender instanceof EntityPlayerMP) player = (EntityPlayerMP) sender;
        if (player == null)
        {
            notifyCommandListener(sender, this, getUsage(sender));
            return;
        }

        Network.WRAPPER.sendTo(new Network.InteractPacket(blockPos), player);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        return args.length == 4 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.emptyList();
    }
}
