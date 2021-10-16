package dev.ftb.mods.ftbmoney.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.WidgetLayout;
import dev.ftb.mods.ftbmoney.shop.Shop;
import dev.ftb.mods.ftbmoney.shop.ShopEntry;
import dev.ftb.mods.ftbmoney.shop.ShopTab;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ShopEntryButtonsPanel extends Panel {
	public final ShopScreen shopScreen;

	public ShopEntryButtonsPanel(ShopScreen g) {
		super(g);
		shopScreen = g;
	}

	@Override
	public void add(Widget widget) {
		if (shopScreen.searchBox.getText().isEmpty() || widget.getTitle().getString().toLowerCase().contains(shopScreen.searchBox.getText().toLowerCase())) {
			super.add(widget);
		}
	}

	@Override
	public void addWidgets() {
		List<ShopEntryButton> buttons = new ArrayList<>();
		List<ShopEntryButton> locked = new ArrayList<>();

		if (!shopScreen.searchBox.getText().isEmpty()) {
			for (ShopTab tab : Shop.CLIENT.tabs) {
				for (ShopEntry entry : tab.entries) {
					ShopEntryButton b = new ShopEntryButton(this, entry);
					(b.locked == 0 ? buttons : locked).add(b);
				}
			}
		} else if (shopScreen.selectedTab != null) {
			for (ShopEntry entry : shopScreen.selectedTab.entries) {
				ShopEntryButton b = new ShopEntryButton(this, entry);
				(b.locked == 0 ? buttons : locked).add(b);
			}
		}

		buttons.sort(ShopEntryButton.COMPARATOR);
		addAll(buttons);
		addAll(locked);

		if (shopScreen.selectedTab != null && Shop.CLIENT.file.get().canEdit()) {
			add(new AddEntryButton(this));
		}
	}

	@Override
	public void alignWidgets() {
		setY(23);
		int prevWidth = width;

		if (widgets.isEmpty()) {
			setWidth(100);
		} else {
			setWidth(0);

			for (Widget w : widgets) {
				setWidth(Math.max(width, w.width));
			}
		}

		setWidth(Math.max(width, prevWidth));

		for (Widget w : widgets) {
			w.setX(1);
			w.setWidth(width - 2);
		}

		setHeight(140);

		shopScreen.scrollBar.setPosAndSize(posX + width + 6, posY - 1, 16, height + 2);
		shopScreen.scrollBar.setMaxValue(align(new WidgetLayout.Vertical(1, 1, 1)));

		shopScreen.setWidth(shopScreen.scrollBar.posX + shopScreen.scrollBar.width + 8);
		shopScreen.setHeight(height + 32);

		shopScreen.searchBox.setPosAndSize(8, 6, shopScreen.width - 16, 12);
	}

	@Override
	public void drawBackground(PoseStack poseStack, Theme theme, int x, int y, int w, int h) {
		theme.drawPanelBackground(poseStack, x, y, w, h);
	}
}