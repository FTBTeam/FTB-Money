package com.feed_the_beast.mods.money.integration.jei;

import net.minecraftforge.fml.common.Loader;

/**
 * @author LatvianModder
 */
public class FTBMoneyJEIHelper
{
	public static void refresh()
	{
		if (Loader.isModLoaded("jei"))
		{
			refreshShop();
		}
	}

	private static void refreshShop()
	{
		ShopRegistry.INSTANCE.refresh();
	}
}