package com.feed_the_beast.mods.money.gui;

import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.config.ConfigInt;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.ContextMenuItem;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.WidgetType;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiEditConfig;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiEditConfigValue;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.net.MessageBuy;
import com.feed_the_beast.mods.money.net.MessageEditShopEntry;
import com.feed_the_beast.mods.money.net.MessageMoveShopEntry;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ButtonShopEntry extends Button
{
	public final ShopEntry entry;
	public final boolean locked;

	public ButtonShopEntry(Panel panel, ShopEntry e)
	{
		super(panel, e.stack.getRarity().color + e.stack.getDisplayName(), ItemIcon.getItemIcon(e.stack));
		entry = e;
		locked = ((GuiShop) panel.getGui()).locked.contains(entry);
		setWidth(Math.max(panel.getGui().getTheme().getStringWidth(title), panel.getGui().getTheme().getStringWidth(FTBMoney.moneyString(entry.buy))) + 32);
		setHeight(24);
	}

	@Override
	public void onClicked(MouseButton button)
	{
		GuiHelper.playClickSound();
		GuiShop gui = (GuiShop) getGui();

		if (button.isLeft())
		{
			if (!locked || gui.canEdit)
			{
				if (isShiftKeyDown())
				{
					new GuiEditConfigValue("count", new ConfigInt(entry.stack.getMaxStackSize(), 1, 1024), (value, set) -> {
						gui.openGui();

						if (set)
						{
							new MessageBuy(entry, value.getInt()).sendToServer();
						}
					}).openGui();
				}
				else
				{
					new MessageBuy(entry, 1).sendToServer();
				}
			}
		}
		else if (button.isRight() && gui.canEdit)
		{
			List<ContextMenuItem> contextMenu = new ArrayList<>();

			contextMenu.add(new ContextMenuItem(I18n.format("selectServer.edit"), GuiIcons.SETTINGS, () -> {
				ConfigGroup group = ConfigGroup.newGroup("ftbmoney").setDisplayName(new TextComponentTranslation("sidebar_button.ftbmoney.shop"));
				ConfigGroup g = group.getGroup("shop").getGroup("entry");
				entry.getConfig(g);
				new GuiEditConfig(group, (g1, sender) -> new MessageEditShopEntry(entry, false).sendToServer()).openGui();
				gui.refreshWidgets();
			}));

			contextMenu.add(new ContextMenuItem(I18n.format("gui.move"), GuiIcons.UP, () -> {
				new MessageMoveShopEntry(entry, true).sendToServer();
				int id = entry.getIndex();

				if (id > 0)
				{
					entry.tab.entries.remove(id);
					entry.tab.entries.add(id - 1, entry);
				}

				gui.refreshWidgets();
			}));

			contextMenu.add(new ContextMenuItem(I18n.format("gui.move"), GuiIcons.DOWN, () -> {
				new MessageMoveShopEntry(entry, false).sendToServer();
				int id = entry.getIndex();

				if (id < entry.tab.entries.size() - 1)
				{
					entry.tab.entries.remove(id);
					entry.tab.entries.add(id + 1, entry);
				}

				gui.refreshWidgets();
			}));

			contextMenu.add(new ContextMenuItem(I18n.format("selectServer.delete"), GuiIcons.REMOVE, () -> gui.openYesNo(I18n.format("delete_item", entry.stack.getDisplayName()), "", () -> {
				new MessageEditShopEntry(entry, true).sendToServer();
				entry.tab.entries.remove(entry);
				gui.refreshWidgets();
			})));

			gui.openContextMenu(contextMenu);
		}
	}

	@Override
	public WidgetType getWidgetType()
	{
		if (locked && !((GuiShop) getGui()).canEdit)
		{
			return WidgetType.DISABLED;
		}

		return super.getWidgetType();
	}

	@Override
	public void addMouseOverText(List<String> list)
	{
		boolean canEdit = ((GuiShop) getGui()).canEdit;

		if (locked && canEdit)
		{
			list.add("Locked!");
		}

		if (!locked || canEdit)
		{
			GuiHelper.addStackTooltip(entry.stack, list);
		}
	}

	@Override
	public void draw(Theme theme, int x, int y, int w, int h)
	{
		drawBackground(theme, x, y, w, h);

		if (locked && !((GuiShop) getGui()).canEdit)
		{
			GuiIcons.LOCK.draw(x + 4, y + 4, 16, 16);
			theme.drawString("???", x + 24, y + 3, theme.getContentColor(getWidgetType()), Theme.SHADOW);
			theme.drawString(TextFormatting.GOLD + "\u0398 " + TextFormatting.OBFUSCATED + "000,000,000", x + 24, y + 13, Color4I.WHITE, Theme.SHADOW);
			return;
		}

		String t = title;

		int mw = w - 24;

		if (theme.getStringWidth(t) > mw)
		{
			t = theme.trimStringToWidth(t, mw);
		}

		drawIcon(theme, x + 4, y + 4, 16, 16);
		theme.drawString(t, x + 24, y + 3, theme.getContentColor(getWidgetType()), Theme.SHADOW);
		theme.drawString(TextFormatting.GOLD + FTBMoney.moneyString(entry.buy), x + 24, y + 13, Color4I.WHITE, Theme.SHADOW);
	}
}