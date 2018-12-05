package com.feed_the_beast.mods.money.gui;

import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftblib.lib.gui.TextBox;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

/**
 * @author LatvianModder
 */
public class GuiShop extends GuiBase
{
	public final Shop shop;
	public final boolean canEdit;
	public ShopTab selectedTab;
	public final Panel panelButtons;
	public final Panel panelTabs;
	public final PanelScrollBar scrollBar;
	public TextBox searchBox;
	private String title;

	public GuiShop(Shop s, boolean e)
	{
		shop = s;
		canEdit = e;
		selectedTab = shop.tabs.isEmpty() ? null : shop.tabs.get(0);
		title = TextFormatting.UNDERLINE + I18n.format("sidebar_button.ftbmoney.shop");

		panelButtons = new PanelShopEntryButtons(this);
		panelButtons.setPosAndSize(9, 9, 0, 146);

		panelTabs = new PanelTabs(this);
		panelTabs.setPosAndSize(-19, 6, 20, 200);

		scrollBar = new PanelScrollBar(this, panelButtons);
		scrollBar.setCanAlwaysScroll(true);
		scrollBar.setScrollStep(24);

		searchBox = new TextBox(this)
		{
			@Override
			public void onTextChanged()
			{
				panelButtons.refreshWidgets();
			}
		};

		searchBox.ghostText = I18n.format("gui.search_box");
	}

	@Override
	public void addWidgets()
	{
		add(panelButtons);
		add(panelTabs);
		add(scrollBar);
		add(searchBox);
	}

	@Override
	public void alignWidgets()
	{
		panelButtons.alignWidgets();
		panelTabs.alignWidgets();
	}

	@Override
	public void drawBackground(Theme theme, int x, int y, int w, int h)
	{
		super.drawBackground(theme, x, y, w, h);
		theme.drawString(title, x + (width - theme.getStringWidth(title)) / 2, y - 23, Theme.SHADOW);
		String balance = TextFormatting.GOLD + FTBMoney.moneyString(FTBMoney.getMoney(Minecraft.getMinecraft().player));
		theme.drawString(balance, x + (width - theme.getStringWidth(balance)) - 2, y - 10, Theme.SHADOW);

		if (selectedTab != null)
		{
			theme.drawString(selectedTab.title, x + 2, y - 10, Theme.SHADOW);
		}
	}
}