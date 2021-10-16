package dev.ftb.mods.ftbmoney.gui;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ui.EditConfigScreen;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.WidgetType;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftbmoney.net.EditShopTabMessage;
import dev.ftb.mods.ftbmoney.net.MoveShopTabMessage;
import dev.ftb.mods.ftbmoney.shop.Shop;
import dev.ftb.mods.ftbmoney.shop.ShopTab;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class TabButton extends Button {
	public final ShopTab tab;

	public TabButton(Panel panel, ShopTab t) {
		super(panel, new TextComponent(t.title), ItemIcon.getItemIcon(t.icon));
		setSize(20, 20);
		tab = t;
	}

	@Override
	public void onClicked(MouseButton button) {
		playClickSound();
		ShopScreen gui = (ShopScreen) getGui();

		if (button.isLeft()) {
			gui.selectedTab = tab;
			gui.refreshWidgets();
		} else if (button.isRight() && tab.shop.file.get().canEdit()) {
			List<ContextMenuItem> contextMenu = new ArrayList<>();

			contextMenu.add(new ContextMenuItem(new TranslatableComponent("selectServer.edit"), Icons.SETTINGS, () -> {
				ConfigGroup group = new ConfigGroup("ftbmoney").setNameKey("sidebar_button.ftbmoney.shop");
				ConfigGroup g = group.getGroup("shop").getGroup("tab");
				tab.getConfig(g);
				g.savedCallback = b -> {
					openGui();

					if (b) {
						new EditShopTabMessage(tab, false).sendToServer();
					}
				};

				new EditConfigScreen(group).openGui();
				gui.refreshWidgets();
			}));

			contextMenu.add(new ContextMenuItem(new TranslatableComponent("gui.move"), Icons.UP, () -> {
				new MoveShopTabMessage(tab, true).sendToServer();
				int id = tab.getIndex();

				if (id > 0) {
					tab.shop.tabs.remove(id);
					tab.shop.tabs.add(id - 1, tab);
				}

				gui.refreshWidgets();
			}));

			contextMenu.add(new ContextMenuItem(new TranslatableComponent("gui.move"), Icons.DOWN, () -> {
				new MoveShopTabMessage(tab, false).sendToServer();
				int id = tab.getIndex();

				if (id < tab.shop.tabs.size() - 1) {
					tab.shop.tabs.remove(id);
					tab.shop.tabs.add(id + 1, tab);
				}

				gui.refreshWidgets();
			}));

			contextMenu.add(new ContextMenuItem(new TranslatableComponent("selectServer.delete"), Icons.REMOVE, () -> gui.openYesNo(new TranslatableComponent("delete_item", tab.title), TextComponent.EMPTY, () -> {
				new EditShopTabMessage(tab, true).sendToServer();
				tab.shop.tabs.remove(tab);

				if (gui.selectedTab == tab) {
					gui.selectedTab = Shop.CLIENT.tabs.isEmpty() ? null : Shop.CLIENT.tabs.get(0);
				}

				gui.refreshWidgets();
			})));
			gui.openContextMenu(contextMenu);
		}
	}

	@Override
	public WidgetType getWidgetType() {
		return ((ShopScreen) getGui()).selectedTab == tab ? WidgetType.MOUSE_OVER : super.getWidgetType();
	}
}