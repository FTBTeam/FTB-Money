package dev.ftb.mods.ftbmoney.gui;

import dev.ftb.mods.ftblibrary.config.ItemStackConfig;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftblibrary.config.ui.SelectItemStackScreen;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftbmoney.net.AddShopTabMessage;
import dev.ftb.mods.ftbmoney.shop.Shop;
import dev.ftb.mods.ftbmoney.shop.ShopTab;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class AddTabButton extends Button {
	public AddTabButton(Panel panel) {
		super(panel, new TranslatableComponent("gui.add"), Icons.ADD);
		setSize(20, 20);
	}

	@Override
	public void onClicked(MouseButton button) {
		playClickSound();
		ShopScreen gui = (ShopScreen) getGui();
		StringConfig title = new StringConfig(Pattern.compile(".+"));

		title.onClicked(button, set -> {
			if (set && !title.value.isEmpty()) {
				ItemStackConfig icon = new ItemStackConfig(false, true);

				new SelectItemStackScreen(icon, set2 -> {
					if (set2 && !icon.value.isEmpty()) {
						ShopTab tab = new ShopTab(Shop.CLIENT);
						tab.title = title.value.trim();
						tab.icon = icon.value.copy();
						tab.shop.tabs.add(tab);
						gui.refreshWidgets();
						new AddShopTabMessage(tab).sendToServer();
					}

					gui.openGui();
				}).openGui();
			} else {
				gui.openGui();
			}
		});
	}
}