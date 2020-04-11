package com.fantasticsource.tiamatinteractions;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.tiamatinteractions.TiamatInteractions.MODID;

@Config(modid = MODID)
public class TiamatInteractionsConfig
{
    @Config.Name("Block List is Whitelist")
    @Config.LangKey(MODID + ".config.blockListIsWhitelist")
    @Config.Comment({
            "Whether the block interaction list is a whitelist (true) or a blacklist (false)"
    })
    public static boolean blockListIsWhitelist = false;

    @Config.Name("Block List")
    @Config.LangKey(MODID + ".config.blockList")
    @Config.Comment({
            "Which blocks should be whitelisted / blacklisted"
    })
    public static String[] blockList = new String[0];
}
