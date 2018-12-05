package com.feed_the_beast.mods.money.gui;

import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.ContextMenuItem;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.WidgetType;
import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.mods.money.net.MessageDeleteShopTab;
import com.feed_the_beast.mods.money.shop.ShopTab;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ButtonTab extends Button
{
	public final ShopTab tab;

	public ButtonTab(Panel panel, ShopTab t)
	{
		super(panel, t.title, ItemIcon.getItemIcon(t.icon));
		setSize(20, 20);
		tab = t;
	}

	@Override
	public void onClicked(MouseButton button)
	{
		GuiHelper.playClickSound();
		GuiShop gui = (GuiShop) getGui();

		if (button.isLeft())
		{
			gui.selectedTab = tab;
			gui.refreshWidgets();
		}
		else if (button.isRight() && ((GuiShop) getGui()).canEdit)
		{
			List<ContextMenuItem> contextMenu = new ArrayList<>();
			contextMenu.add(new ContextMenuItem(I18n.format("selectServer.delete"), GuiIcons.REMOVE, () -> gui.openYesNo(I18n.format("delete_item", tab.title), "", () -> {
				new MessageDeleteShopTab(tab).sendToServer();
				tab.shop.tabs.remove(tab);

				if (gui.selectedTab == tab)
				{
					gui.selectedTab = gui.shop.tabs.isEmpty() ? null : gui.shop.tabs.get(0);
				}

				gui.refreshWidgets();
			})));
			gui.openContextMenu(contextMenu);
		}
	}

	@Override
	public WidgetType getWidgetType()
	{
		return ((GuiShop) getGui()).selectedTab == tab ? WidgetType.MOUSE_OVER : super.getWidgetType();
	}
}