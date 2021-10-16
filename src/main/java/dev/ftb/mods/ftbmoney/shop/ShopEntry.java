package dev.ftb.mods.ftbmoney.shop;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.util.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ShopEntry implements INBTSerializable<CompoundTag> {
	public final ShopTab tab;
	public String page = "";
	public ItemStack stack = ItemStack.EMPTY;
	public long buy = 0L;
	public long sell = 0L;
	public BlockPos stock = null;
	public ResourceKey<Level> stockDimension = null;
	public UUID createdBy = null;
	public long lock = 0L;
	public boolean disabledServer = false;

	public ShopEntry(ShopTab t) {
		tab = t;
	}

	@Override
	public SNBTCompoundTag serializeNBT() {
		SNBTCompoundTag nbt = new SNBTCompoundTag();
		NBTUtils.write(nbt, "item", stack);

		if (buy > 0L) {
			nbt.putLong("buy", buy);
		}

		if (sell > 0L) {
			nbt.putLong("sell", sell);
		}

		if (stock != null && stockDimension != null) {
			nbt.putIntArray("stock", new int[]{stock.getX(), stock.getY(), stock.getZ()});
			nbt.putString("stock_dimension", stockDimension.location().toString());
		}

		if (createdBy != null) {
			nbt.putString("created_by", createdBy.toString());
		}

		if (lock != 0L) {
			nbt.putLong("lock", lock);
		}

		if (disabledServer) {
			nbt.putBoolean("disabled_server", true);
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		page = nbt.getString("page");
		stack = NBTUtils.read(nbt, "item");
		buy = nbt.getLong("buy");
		sell = nbt.getLong("sell");

		if (nbt.contains("") && nbt.contains("")) {
			int[] ai = nbt.getIntArray("stock");
			stock = new BlockPos(ai[0], ai[1], ai[2]);
			stockDimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("stock_dimension")));
		} else {
			stock = null;
			stockDimension = null;
		}

		createdBy = nbt.contains("created_by") ? UUID.fromString(nbt.getString("created_by")) : null;
		lock = nbt.getLong("lock");
		disabledServer = nbt.getBoolean("disabled_server");
	}

	public boolean isUnlocked(@Nullable TeamData data) {
		if (lock == 0L) {
			return true;
		} else if (data == null) {
			return false;
		}

		QuestObject object = tab.shop.file.get().get(lock);
		return object != null && data.isCompleted(object);
	}

	public void getConfig(ConfigGroup group) {
		group.addItemStack("item", stack, v -> stack = v, ItemStack.EMPTY, false, false);
		group.addLong("buy", buy, v -> buy = v, 1L, 0L, Long.MAX_VALUE);
		//group.addLong("sell", () -> sell, v -> sell = v, 0L, 0L, Long.MAX_VALUE);
		/*
		group.add("lock", new ConfigQuestObject(tab.shop.file.get(), lock, QuestObjectType.ALL_PROGRESSING_OR_NULL) {
			@Override
			public void setObject(int v) {
				lock = v;
			}

			@Override
			public int getObject() {
				return lock;
			}
		}, new ConfigQuestObject(tab.shop.file.get(), 0, QuestObjectType.ALL_PROGRESSING_OR_NULL));
		 */
		group.addBool("disabled_server", disabledServer, v -> disabledServer = v, false);
	}

	public int getIndex() {
		return tab.entries.indexOf(this);
	}
}