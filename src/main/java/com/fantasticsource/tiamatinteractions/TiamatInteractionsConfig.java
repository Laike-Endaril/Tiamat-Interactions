package com.fantasticsource.tiamatinteractions;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.tiamatinteractions.TiamatInteractions.MODID;

@Config(modid = MODID)
public class TiamatInteractionsConfig
{
    @Config.Name("Adventure Block List is Whitelist")
    @Config.LangKey(MODID + ".config.adventureBlockListIsWhitelist")
    @Config.Comment({
            "Whether the block interaction list for adventure mode players is a whitelist (true) or a blacklist (false)"
    })
    public static boolean adventureBlockListIsWhitelist = false;

    @Config.Name("Adventure Block List")
    @Config.LangKey(MODID + ".config.adventureBlockList")
    @Config.Comment({
            "Which blocks should be whitelisted / blacklisted for adventure mode players"
    })
    public static String[] adventureBlockList = new String[0];

    @Config.Name("Survival Block List is Whitelist")
    @Config.LangKey(MODID + ".config.survivalBlockListIsWhitelist")
    @Config.Comment({
            "Whether the block interaction list for survival mode players is a whitelist (true) or a blacklist (false)"
    })
    public static boolean survivalBlockListIsWhitelist = false;

    @Config.Name("Survival Block List")
    @Config.LangKey(MODID + ".config.survivalBlockList")
    @Config.Comment({
            "Which blocks should be whitelisted / blacklisted for survival mode players"
    })
    public static String[] survivalBlockList = new String[0];

    @Config.Name("Creative Block List is Whitelist")
    @Config.LangKey(MODID + ".config.creativeBlockListIsWhitelist")
    @Config.Comment({
            "Whether the block interaction list for creative mode players is a whitelist (true) or a blacklist (false)"
    })
    public static boolean creativeBlockListIsWhitelist = false;

    @Config.Name("Creative Block List")
    @Config.LangKey(MODID + ".config.creativeBlockList")
    @Config.Comment({
            "Which blocks should be whitelisted / blacklisted for creative mode players"
    })
    public static String[] creativeBlockList = new String[0];
}
