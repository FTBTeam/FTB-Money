package dev.ftb.mods.ftbmoney.gui;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.WidgetLayout;
import dev.ftb.mods.ftbmoney.shop.Shop;
import dev.ftb.mods.ftbmoney.shop.ShopTab;

/**
 * @author LatvianModder
 */
public class TabsPanel extends Panel {
	public TabsPanel(ShopScreen g) {
		super(g);
	}

	@Override
	public void addWidgets() {
		for (ShopTab tab : Shop.CLIENT.tabs) {
			add(new TabButton(this, tab));
		}

		if (Shop.CLIENT.file.get().canEdit()) {
			add(new AddTabButton(this));
		}
	}

	@Override
	public void alignWidgets() {
		align(WidgetLayout.VERTICAL);
	}
}