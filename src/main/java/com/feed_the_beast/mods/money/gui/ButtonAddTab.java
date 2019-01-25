package com.feed_the_beast.mods.money.gui;

import com.feed_the_beast.ftblib.lib.config.ConfigString;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiEditConfigValue;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiSelectItemStack;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.mods.money.net.MessageAddShopTab;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopTab;
import net.minecraft.client.resources.I18n;

import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class ButtonAddTab extends Button
{
	public ButtonAddTab(Panel panel)
	{
		super(panel, I18n.format("gui.add"), GuiIcons.ADD);
		setSize(20, 20);
	}

	@Override
	public void onClicked(MouseButton button)
	{
		GuiHelper.playClickSound();
		GuiShop gui = (GuiShop) getGui();
		new GuiEditConfigValue("title", new ConfigString("", Pattern.compile(".+")), (value, set) -> {
			if (set)
			{
				new GuiSelectItemStack(gui, stack -> {
					ShopTab tab = new ShopTab(Shop.CLIENT);
					tab.title = value.getString().trim();
					tab.icon = stack.copy();
					tab.shop.tabs.add(tab);
					gui.refreshWidgets();
					new MessageAddShopTab(tab).sendToServer();
				}).openGui();
			}
			else
			{
				gui.openGui();
			}
		}).openGui();
	}
}