package com.feed_the_beast.mods.money.gui;

import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftblib.lib.gui.SimpleButton;
import com.feed_the_beast.ftblib.lib.gui.TextBox;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.FTBMoneyClientConfig;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

import java.util.List;

/**
 * @author LatvianModder
 */
public class GuiShop extends GuiBase
{
	public ShopTab selectedTab;
	public final Panel panelButtons;
	public final Panel panelTabs;
	public final PanelScrollBar scrollBar;
	public TextBox searchBox;
	private String title;
	public final Button sort;

	public GuiShop()
	{
		selectedTab = Shop.CLIENT.tabs.isEmpty() ? null : Shop.CLIENT.tabs.get(0);
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
				sort.setPosAndSize(GuiShop.this.width - 1, 6, 20, 20);
			}
		};

		searchBox.ghostText = I18n.format("gui.search_box");

		sort = new SimpleButton(this, I18n.format("ftbmoney.shop.tab.sort"), GuiIcons.SORT_AZ, (widget, button) -> {
			FTBMoneyClientConfig.general.sort = EnumSortType.VALUES[(FTBMoneyClientConfig.general.sort.ordinal() + 1) % EnumSortType.VALUES.length];
			panelButtons.refreshWidgets();
			ConfigManager.sync(FTBMoney.MOD_ID, Config.Type.INSTANCE);
		})
		{
			@Override
			public void addMouseOverText(List<String> list)
			{
				super.addMouseOverText(list);
				list.add(TextFormatting.GRAY + I18n.format("ftbmoney.shop.tab.sort." + FTBMoneyClientConfig.general.sort.name));
			}

			@Override
			public void drawBackground(Theme theme, int x, int y, int w, int h)
			{
				theme.drawButton(x, y, w, h, getWidgetType());
			}
		};
	}

	@Override
	public void addWidgets()
	{
		add(panelButtons);
		add(panelTabs);
		add(scrollBar);
		add(searchBox);
		add(sort);
	}

	@Override
	public void alignWidgets()
	{
		panelButtons.alignWidgets();
		panelTabs.alignWidgets();
		sort.setPosAndSize(width - 1, 6, 20, 20);
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