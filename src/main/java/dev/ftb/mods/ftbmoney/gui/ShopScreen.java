package dev.ftb.mods.ftbmoney.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.SimpleButton;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbmoney.FTBMoney;
import dev.ftb.mods.ftbmoney.shop.Shop;
import dev.ftb.mods.ftbmoney.shop.ShopTab;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * @author LatvianModder
 */
public class ShopScreen extends BaseScreen {
	public ShopTab selectedTab;
	public final Panel panelButtons;
	public final Panel panelTabs;
	public final PanelScrollBar scrollBar;
	public TextBox searchBox;
	private final Component title;
	public final Button sort;

	public ShopScreen() {
		selectedTab = Shop.CLIENT.tabs.isEmpty() ? null : Shop.CLIENT.tabs.get(0);
		title = new TranslatableComponent("sidebar_button.ftbmoney.shop").withStyle(ChatFormatting.UNDERLINE);

		panelButtons = new ShopEntryButtonsPanel(this);
		panelButtons.setPosAndSize(9, 9, 0, 146);

		panelTabs = new TabsPanel(this);
		panelTabs.setPosAndSize(-19, 6, 20, 200);

		scrollBar = new PanelScrollBar(this, panelButtons);
		scrollBar.setCanAlwaysScroll(true);
		scrollBar.setScrollStep(24);

		searchBox = new TextBox(this) {
			@Override
			public void onTextChanged() {
				panelButtons.refreshWidgets();
				sort.setPosAndSize(ShopScreen.this.width - 1, 6, 20, 20);
			}
		};

		searchBox.ghostText = I18n.get("gui.search_box");

		sort = new SimpleButton(this, new TranslatableComponent("ftbmoney.shop.tab.sort"), Icons.SORT_AZ, (widget, button) -> {
			SortType.sort = SortType.VALUES[(SortType.sort.ordinal() + 1) % SortType.VALUES.length];
			panelButtons.refreshWidgets();
			// FIXME: ConfigManager.sync(FTBMoney.MOD_ID, Config.Type.INSTANCE);
		}) {
			@Override
			public void addMouseOverText(TooltipList list) {
				super.addMouseOverText(list);
				list.add(new TranslatableComponent("ftbmoney.shop.tab.sort." + SortType.sort.name).withStyle(ChatFormatting.GRAY));
			}

			@Override
			public void drawBackground(PoseStack poseStack, Theme theme, int x, int y, int w, int h) {
				theme.drawButton(poseStack, x, y, w, h, getWidgetType());
			}
		};
	}

	@Override
	public void addWidgets() {
		add(panelButtons);
		add(panelTabs);
		add(scrollBar);
		add(searchBox);
		add(sort);
	}

	@Override
	public void alignWidgets() {
		panelButtons.alignWidgets();
		panelTabs.alignWidgets();
		sort.setPosAndSize(width - 1, 6, 20, 20);
	}

	@Override
	public void drawBackground(PoseStack poseStack, Theme theme, int x, int y, int w, int h) {
		super.drawBackground(poseStack, theme, x, y, w, h);
		theme.drawString(poseStack, title, x + (width - theme.getStringWidth(title)) / 2, y - 23, Theme.SHADOW);

		Component balance = FTBMoney.moneyComponent(FTBMoney.getClientMoney());
		theme.drawString(poseStack, balance, x + (width - theme.getStringWidth(balance)) - 2, y - 10, Theme.SHADOW);

		if (selectedTab != null) {
			theme.drawString(poseStack, selectedTab.title, x + 2, y - 10, Theme.SHADOW);
		}
	}
}