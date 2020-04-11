package com.fantasticsource.tiamatinteractions;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod(modid = TiamatInteractions.MODID, name = TiamatInteractions.NAME, version = TiamatInteractions.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.034b,)")
public class TiamatInteractions
{
    public static final String MODID = "tiamatinteractions";
    public static final String NAME = "Tiamat Interactions";
    public static final String VERSION = "1.12.2.000";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(TiamatInteractions.class);
        Network.init();
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        Network.WRAPPER.sendTo(new Network.SyncConfigPacket(player), player);
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getEntityPlayer() instanceof EntityPlayerMP)
        {
            if (MCTools.isOP((EntityPlayerMP) event.getEntityPlayer())) return;
        }
        else if (TiamatInteractionsData.weAreOP) return;

        IBlockState blockState = event.getWorld().getBlockState(event.getPos());
        if (TiamatInteractionsData.blockListIsWhitelist != TiamatInteractionsData.blockList.contains(blockState.getBlock().getRegistryName().toString()))
        {
            event.setUseBlock(Event.Result.DENY);
        }
    }
}
