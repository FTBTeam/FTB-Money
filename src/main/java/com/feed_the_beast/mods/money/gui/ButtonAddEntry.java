package com.feed_the_beast.mods.money.gui;

import com.feed_the_beast.ftblib.lib.config.ConfigLong;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.SimpleTextButton;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiEditConfigValue;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiSelectItemStack;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.mods.money.net.MessageAddShopEntry;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import net.minecraft.client.resources.I18n;

/**
 * @author LatvianModder
 */
public class ButtonAddEntry extends SimpleTextButton
{
	public ButtonAddEntry(Panel panel)
	{
		super(panel, I18n.format("gui.add"), GuiIcons.ADD);
	}

	@Override
	public void onClicked(MouseButton button)
	{
		GuiHelper.playClickSound();
		GuiShop gui = (GuiShop) getGui();
		new GuiSelectItemStack(gui, stack -> {
			new GuiEditConfigValue("price", new ConfigLong(1L, 1L, Long.MAX_VALUE), (value, set) -> {
				gui.openGui();

				if (set)
				{
					ShopEntry entry = new ShopEntry(gui.selectedTab);
					entry.stack = stack.copy();
					entry.buy = value.getLong();
					entry.tab.entries.add(entry);
					gui.refreshWidgets();
					gui.openGui();
					new MessageAddShopEntry(entry).sendToServer();
				}
			}).openGui();
		}).openGui();
	}
}