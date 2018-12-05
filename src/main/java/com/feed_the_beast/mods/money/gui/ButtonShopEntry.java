package com.feed_the_beast.mods.money.gui;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.ContextMenuItem;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.net.MessageBuy;
import com.feed_the_beast.mods.money.net.MessageDeleteShopEntry;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ButtonShopEntry extends Button
{
	public final ShopEntry entry;

	public ButtonShopEntry(Panel panel, ShopEntry e)
	{
		super(panel, e.stack.getDisplayName(), ItemIcon.getItemIcon(e.stack));
		entry = e;
		setWidth(panel.getGui().getTheme().getStringWidth(title) + 28);
		setHeight(24);
	}

	@Override
	public void onClicked(MouseButton button)
	{
		GuiHelper.playClickSound();

		if (button.isLeft())
		{
			new MessageBuy(entry.netID, isShiftKeyDown() ? entry.stack.getMaxStackSize() : 1).sendToServer();
		}
		else if (button.isRight() && ClientUtils.isClientOP())
		{
			List<ContextMenuItem> contextMenu = new ArrayList<>();
			contextMenu.add(new ContextMenuItem(I18n.format("selectServer.delete"), GuiIcons.REMOVE, () -> {
				entry.tab.entries.remove(entry);
				getGui().refreshWidgets();
				new MessageDeleteShopEntry(entry.netID).sendToServer();
			}));
			getGui().openContextMenu(contextMenu);
		}
	}

	@Override
	public void addMouseOverText(List<String> list)
	{
		if (getGui().getTheme().getStringWidth(getTitle()) + 28 > width)
		{
			list.add(getTitle());
		}
	}

	@Override
	public void draw(Theme theme, int x, int y, int w, int h)
	{
		drawBackground(theme, x, y, w, h);
		String t = title;

		int mw = w - 30;

		if (theme.getStringWidth(t) > mw)
		{
			t = theme.trimStringToWidth(t, mw);
		}

		drawIcon(theme, x + 4, y + 4, 16, 16);
		theme.drawString(t, x + 24, y + 3, theme.getContentColor(getWidgetType()), Theme.SHADOW);
		theme.drawString(TextFormatting.GOLD + FTBMoney.moneyString(entry.buy), x + 24, y + 13, Color4I.WHITE, Theme.SHADOW);
	}
}