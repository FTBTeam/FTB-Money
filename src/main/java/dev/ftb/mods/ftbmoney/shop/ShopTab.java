package dev.ftb.mods.ftbmoney.shop;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftbquests.util.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ShopTab implements INBTSerializable<CompoundTag> {
	public final Shop shop;
	public String title = "";
	public ItemStack icon = ItemStack.EMPTY;
	public int lock = 0;
	public final List<ShopEntry> entries = new ArrayList<>();

	public ShopTab(Shop s) {
		shop = s;
	}

	public SNBTCompoundTag serializeSettings() {
		SNBTCompoundTag nbt = new SNBTCompoundTag();
		nbt.putString("title", title);
		NBTUtils.write(nbt, "icon", icon);

		if (lock != 0) {
			nbt.putInt("lock", lock);
		}

		return nbt;
	}

	public void deserializeSettings(CompoundTag nbt) {
		title = nbt.getString("title");
		icon = NBTUtils.read(nbt, "icon");
		lock = nbt.getInt("lock");
	}

	@Override
	public SNBTCompoundTag serializeNBT() {
		SNBTCompoundTag nbt = serializeSettings();
		ListTag e = new ListTag();

		for (ShopEntry entry : entries) {
			e.add(entry.serializeNBT());
		}

		nbt.put("entries", e);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		deserializeSettings(nbt);
		entries.clear();
		ListTag e = nbt.getList("entries", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < e.size(); i++) {
			ShopEntry entry = new ShopEntry(this);
			entry.deserializeNBT(e.getCompound(i));

			if (!entry.stack.isEmpty()) {
				entries.add(entry);
			}
		}
	}

	public int getIndex() {
		return shop.tabs.indexOf(this);
	}

	public void getConfig(ConfigGroup config) {
		config.addString("title", title, v -> title = v, "");
		config.addItemStack("icon", icon, v -> icon = v, ItemStack.EMPTY, false, true);
	}
}