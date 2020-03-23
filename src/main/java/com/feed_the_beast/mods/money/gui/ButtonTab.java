package com.feed_the_beast.mods.money.gui;

import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.ContextMenuItem;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.WidgetType;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiEditConfig;
import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.mods.money.net.MessageEditShopTab;
import com.feed_the_beast.mods.money.net.MessageMoveShopTab;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopTab;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;

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
		else if (button.isRight() && tab.shop.file.get().canEdit())
		{
			List<ContextMenuItem> contextMenu = new ArrayList<>();

			contextMenu.add(new ContextMenuItem(I18n.format("selectServer.edit"), GuiIcons.SETTINGS, () -> {
				ConfigGroup group = ConfigGroup.newGroup("ftbmoney").setDisplayName(new TextComponentTranslation("sidebar_button.ftbmoney.shop"));
				ConfigGroup g = group.getGroup("shop").getGroup("tab");
				tab.getConfig(g);
				new GuiEditConfig(group, (g1, sender) -> new MessageEditShopTab(tab, false).sendToServer()).openGui();
				gui.refreshWidgets();
			}));

			contextMenu.add(new ContextMenuItem(I18n.format("gui.move"), GuiIcons.UP, () -> {
				new MessageMoveShopTab(tab, true).sendToServer();
				int id = tab.getIndex();

				if (id > 0)
				{
					tab.shop.tabs.remove(id);
					tab.shop.tabs.add(id - 1, tab);
				}

				gui.refreshWidgets();
			}));

			contextMenu.add(new ContextMenuItem(I18n.format("gui.move"), GuiIcons.DOWN, () -> {
				new MessageMoveShopTab(tab, false).sendToServer();
				int id = tab.getIndex();

				if (id < tab.shop.tabs.size() - 1)
				{
					tab.shop.tabs.remove(id);
					tab.shop.tabs.add(id + 1, tab);
				}

				gui.refreshWidgets();
			}));

			contextMenu.add(new ContextMenuItem(I18n.format("selectServer.delete"), GuiIcons.REMOVE, () -> gui.openYesNo(I18n.format("delete_item", tab.title), "", () -> {
				new MessageEditShopTab(tab, true).sendToServer();
				tab.shop.tabs.remove(tab);

				if (gui.selectedTab == tab)
				{
					gui.selectedTab = Shop.CLIENT.tabs.isEmpty() ? null : Shop.CLIENT.tabs.get(0);
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