package com.fantasticsource.tiamatinteractions;

import com.fantasticsource.tiamatinteractions.interaction.Interactions;
import com.fantasticsource.tiamatinteractions.interaction.trading.InteractionTrade;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = TiamatInteractions.MODID, name = TiamatInteractions.NAME, version = TiamatInteractions.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.044zf,)")
public class TiamatInteractions
{
    public static final String MODID = "tiamatinteractions";
    public static final String NAME = "Tiamat Interactions";
    public static final String VERSION = "1.12.2.000c";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(TiamatInteractions.class);
        Network.init();

        new InteractionTrade();
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
        Network.WRAPPER.sendTo(new Network.SyncConfigPacket(), player);
    }

    @Mod.EventHandler
    public static void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CmdInteract());
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        World world = event.getWorld();
        GameType gameType = world.isRemote ? Minecraft.getMinecraft().playerController.getCurrentGameType() : ((EntityPlayerMP) event.getEntityPlayer()).interactionManager.getGameType();

        CInteractionData data = CInteractionData.data.get(gameType);
        if (data == null) return;

        IBlockState blockState = event.getWorld().getBlockState(event.getPos());
        if (data.blockListIsWhitelist != data.blockList.contains(blockState.getBlock().getRegistryName().toString()))
        {
            event.setUseBlock(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void entityInteract(PlayerInteractEvent.EntityInteractSpecific event)
    {
        if (event.getSide() != Side.SERVER || event.getHand() != EnumHand.MAIN_HAND) return;

        Interactions.tryShowInteractionMenu((EntityPlayerMP) event.getEntityPlayer(), event.getLocalPos(), event.getTarget());
    }

    @SubscribeEvent
    public static void blockInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getSide() != Side.SERVER || event.getHand() != EnumHand.MAIN_HAND) return;

        Interactions.tryShowInteractionMenu((EntityPlayerMP) event.getEntityPlayer(), event.getHitVec(), event.getPos());
    }
}
