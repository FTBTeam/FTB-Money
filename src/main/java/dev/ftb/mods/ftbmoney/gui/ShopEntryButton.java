package dev.ftb.mods.ftbmoney.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.IntConfig;
import dev.ftb.mods.ftblibrary.config.ui.EditConfigScreen;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.WidgetType;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbmoney.FTBMoney;
import dev.ftb.mods.ftbmoney.net.BuyMessage;
import dev.ftb.mods.ftbmoney.net.EditShopEntryMessage;
import dev.ftb.mods.ftbmoney.shop.ShopEntry;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ShopEntryButton extends Button {
	public static final Comparator<ShopEntryButton> COMPARATOR = (o1, o2) -> SortType.sort.comparator.compare(o1.entry, o2.entry);

	public final ShopEntry entry;
	public final int locked;

	public ShopEntryButton(Panel panel, ShopEntry e) {
		super(panel, new TextComponent("").append(e.stack.getHoverName()).withStyle(e.stack.getRarity().color), ItemIcon.getItemIcon(e.stack));
		entry = e;
		QuestObject lock = ClientQuestFile.INSTANCE.get(entry.lock);
		locked = (entry.lock == 0 || lock != null && ClientQuestFile.INSTANCE.self.isCompleted(lock)) ? (entry.disabledServer && !Minecraft.getInstance().hasSingleplayerServer()) ? 1 : 0 : 2;
		setWidth(Math.max(panel.getGui().getTheme().getStringWidth(title), panel.getGui().getTheme().getStringWidth(FTBMoney.moneyString(entry.buy))) + 32);
		setHeight(24);
	}

	@Override
	public void onClicked(MouseButton button) {
		playClickSound();
		ShopScreen gui = (ShopScreen) getGui();

		if (button.isLeft()) {
			if (locked == 0 || entry.tab.shop.file.get().canEdit()) {
				IntConfig count = new IntConfig(1, (int) Math.min(1024L, entry.buy <= 0L ? 1024L : FTBMoney.getClientMoney() / entry.buy));
				count.value = 1;
				count.onClicked(button, set -> {
					gui.openGui();

					if (set) {
						new BuyMessage(entry, count.value).sendToServer();
					}
				});
			}
		} else if (button.isRight() && entry.tab.shop.file.get().canEdit()) {
			List<ContextMenuItem> contextMenu = new ArrayList<>();

			contextMenu.add(new ContextMenuItem(new TranslatableComponent("selectServer.edit"), Icons.SETTINGS, () -> {
				ConfigGroup group = new ConfigGroup("ftbmoney").setNameKey("sidebar_button.ftbmoney.shop");
				group.savedCallback = b -> {
					openGui();

					if (b) {
						new EditShopEntryMessage(entry, false).sendToServer();
					}
				};
				ConfigGroup g = group.getGroup("shop").getGroup("entry");
				entry.getConfig(g);
				new EditConfigScreen(group).openGui();
				gui.refreshWidgets();
			}));

			contextMenu.add(new ContextMenuItem(new TranslatableComponent("selectServer.delete"), Icons.REMOVE, () -> gui.openYesNo(new TranslatableComponent("delete_item", entry.stack.getHoverName()), TextComponent.EMPTY, () -> {
				new EditShopEntryMessage(entry, true).sendToServer();
				entry.tab.entries.remove(entry);
				gui.refreshWidgets();
			})));

			gui.openContextMenu(contextMenu);
		}
	}

	@Override
	public WidgetType getWidgetType() {
		if (locked > 0 && !entry.tab.shop.file.get().canEdit()) {
			return WidgetType.DISABLED;
		}

		return super.getWidgetType();
	}

	@Override
	public void addMouseOverText(TooltipList list) {
		if (locked == 2) {
			list.string("Locked!");
			QuestObject object = ClientQuestFile.INSTANCE.get(entry.lock);

			if (object != null) {
				list.add(new TextComponent("Requires: ").append(object.getTitle()));
			}
		}

		if (locked < 2 || entry.tab.shop.file.get().canEdit()) {
			if (entry.disabledServer && !Minecraft.getInstance().hasSingleplayerServer()) {
				list.add(new TranslatableComponent("ftbmoney.shop.entry.disabled_server").withStyle(ChatFormatting.RED));
			}

			List<Component> list1 = new ArrayList<>();
			GuiHelper.addStackTooltip(entry.stack, list1);
			list1.forEach(list::add);
		}
	}

	@Override
	public void draw(PoseStack poseStack, Theme theme, int x, int y, int w, int h) {
		drawBackground(poseStack, theme, x, y, w, h);

		if (locked == 2 && !entry.tab.shop.file.get().canEdit()) {
			Icons.LOCK.draw(poseStack, x + 4, y + 4, 16, 16);
			theme.drawString(poseStack, "???", x + 24, y + 3, theme.getContentColor(getWidgetType()), Theme.SHADOW);
			theme.drawString(poseStack, new TextComponent("").append(FTBMoney.moneyComponent(999_999_999L)).withStyle(ChatFormatting.OBFUSCATED), x + 24, y + 13, Color4I.WHITE, Theme.SHADOW);
			return;
		}

		drawIcon(poseStack, theme, x + 4, y + 4, 16, 16);
		theme.drawString(poseStack, theme.trimStringToWidth(title, w - 24), x + 24, y + 3, theme.getContentColor(getWidgetType()), Theme.SHADOW);
		theme.drawString(poseStack, FTBMoney.moneyComponent(entry.buy), x + 24, y + 13, Color4I.WHITE, Theme.SHADOW);
	}

	@Override
	@Nullable
	public Object getIngredientUnderMouse() {
		return locked < 2 ? entry.stack : null;
	}
}