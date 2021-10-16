package dev.ftb.mods.ftbmoney.gui;

import dev.ftb.mods.ftblibrary.config.ItemStackConfig;
import dev.ftb.mods.ftblibrary.config.LongConfig;
import dev.ftb.mods.ftblibrary.config.ui.SelectItemStackScreen;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftbmoney.net.AddShopEntryMessage;
import dev.ftb.mods.ftbmoney.shop.ShopEntry;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * @author LatvianModder
 */
public class AddEntryButton extends SimpleTextButton {
	public AddEntryButton(Panel panel) {
		super(panel, new TranslatableComponent("gui.add"), Icons.ADD);
	}

	@Override
	public void onClicked(MouseButton button) {
		playClickSound();
		ShopScreen gui = (ShopScreen) getGui();
		ItemStackConfig item = new ItemStackConfig(false, false);

		new SelectItemStackScreen(item, set -> {
			if (set && !item.value.isEmpty()) {
				LongConfig buy = new LongConfig(1L, Long.MAX_VALUE);
				buy.value = 1L;

				buy.onClicked(button, set2 -> {
					if (set2 && buy.value >= 1L) {
						ShopEntry entry = new ShopEntry(gui.selectedTab);
						entry.stack = item.value.copy();
						entry.buy = buy.value;
						entry.tab.entries.add(entry);
						gui.refreshWidgets();
						new AddShopEntryMessage(entry).sendToServer();
					}

					gui.openGui();
				});
			} else {
				gui.openGui();
			}
		}).openGui();
	}
}