package com.feed_the_beast.mods.money.gui;

import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.WidgetLayout;
import com.feed_the_beast.mods.money.shop.ShopTab;

/**
 * @author LatvianModder
 */
public class PanelTabs extends Panel
{
	private final GuiShop guiShop;

	public PanelTabs(GuiShop g)
	{
		super(g);
		guiShop = g;
	}

	@Override
	public void addWidgets()
	{
		for (ShopTab tab : guiShop.shop.tabs)
		{
			add(new ButtonTab(this, tab));
		}

		if (guiShop.shop.file.canEdit())
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