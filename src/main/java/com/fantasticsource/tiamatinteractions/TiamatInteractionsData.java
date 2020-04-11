package com.fantasticsource.tiamatinteractions;

import scala.actors.threadpool.Arrays;

import java.util.ArrayList;

public class TiamatInteractionsData
{
    public static boolean weAreOP = true;

    public static boolean blockListIsWhitelist = TiamatInteractionsConfig.blockListIsWhitelist;
    public static ArrayList<String> blockList = new ArrayList<>(Arrays.asList(TiamatInteractionsConfig.blockList));
}
