package dev.ftb.mods.ftbmoney.shop;

import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftbmoney.net.SyncShopMessage;
import dev.ftb.mods.ftbquests.quest.QuestFile;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class Shop implements INBTSerializable<CompoundTag> {
	public static final Pattern PATTERN = Pattern.compile("\\W");

	public static Shop SERVER;
	public static Shop CLIENT;

	public final Supplier<QuestFile> file;
	public final List<ShopTab> tabs = new ArrayList<>();
	public boolean shouldSave = false;

	public Shop(Supplier<QuestFile> f) {
		file = f;
	}

	@Override
	public SNBTCompoundTag serializeNBT() {
		SNBTCompoundTag nbt = new SNBTCompoundTag();
		ListTag b = new ListTag();

		for (ShopTab tab : tabs) {
			b.add(tab.serializeNBT());
		}

		nbt.put("tabs", b);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		tabs.clear();
		ListTag t = nbt.getList("tabs", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < t.size(); i++) {
			CompoundTag n = t.getCompound(i);
			ShopTab tab = new ShopTab(this);
			tab.deserializeNBT(n);
			tabs.add(tab);
		}
	}

	public void markDirty() {
		shouldSave = true;

		if (file.get() != null && file.get().isServerSide()) {
			new SyncShopMessage(serializeNBT()).sendToAll(ServerQuestFile.INSTANCE.server);
		}
	}

	@Nullable
	public ShopTab getTab(String name) {
		String s = PATTERN.matcher(name).replaceAll("").toLowerCase();

		for (ShopTab tab : tabs) {
			if (PATTERN.matcher(tab.title).replaceAll("").toLowerCase().equals(s)) {
				return tab;
			}
		}

		for (ShopTab tab : tabs) {
			if (PATTERN.matcher(tab.title).replaceAll("").toLowerCase().contains(s)) {
				return tab;
			}
		}

		return null;
	}
}