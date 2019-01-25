package com.feed_the_beast.mods.money.integration.jei;

import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import com.feed_the_beast.mods.money.shop.ShopTab;

import java.util.ArrayList;

/**
 * @author LatvianModder
 */
public enum ShopRegistry
{
	INSTANCE;

	public final ArrayList<ShopWrapper> list = new ArrayList<>();

	@SuppressWarnings("deprecation")
	public void refresh()
	{
		if (FTBMoneyJEIIntegration.RUNTIME != null && !list.isEmpty())
		{
			for (ShopWrapper wrapper : list)
			{
				FTBMoneyJEIIntegration.RUNTIME.getRecipeRegistry().removeRecipe(wrapper, ShopCategory.UID);
			}
		}

		list.clear();

		if (Shop.CLIENT != null)
		{
			for (ShopTab tab : Shop.CLIENT.tabs)
			{
				for (ShopEntry entry : tab.entries)
				{
					if (entry.lock == 0)
					{
						ShopWrapper wrapper = new ShopWrapper(entry);
						list.add(wrapper);
						FTBMoneyJEIIntegration.RUNTIME.getRecipeRegistry().addRecipe(wrapper, ShopCategory.UID);
					}
				}
			}
		}
	}
}