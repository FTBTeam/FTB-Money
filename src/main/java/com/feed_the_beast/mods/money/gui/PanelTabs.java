package com.feed_the_beast.mods.money.gui;

import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.WidgetLayout;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopTab;

/**
 * @author LatvianModder
 */
public class PanelTabs extends Panel
{
	public PanelTabs(GuiShop g)
	{
		super(g);
	}

	@Override
	public void addWidgets()
	{
		for (ShopTab tab : Shop.CLIENT.tabs)
		{
			add(new ButtonTab(this, tab));
		}

		if (Shop.CLIENT.file.get().canEdit())
		{
			add(new ButtonAddTab(this));
		}
	}

	@Override
	public void alignWidgets()
	{
		align(WidgetLayout.VERTICAL);
	}
}