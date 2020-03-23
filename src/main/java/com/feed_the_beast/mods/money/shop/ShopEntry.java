package com.feed_the_beast.mods.money.shop;

import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.config.ConfigItemStack;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbquests.quest.QuestData;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.QuestObjectType;
import com.feed_the_beast.ftbquests.util.ConfigQuestObject;
import com.latmod.mods.itemfilters.item.ItemStackSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ShopEntry implements INBTSerializable<NBTTagCompound>
{
	public final ShopTab tab;
	public String page = "";
	public ItemStack stack = ItemStack.EMPTY;
	public long buy = 0L;
	public long sell = 0L;
	public BlockDimPos stock = null;
	public UUID createdBy = null;
	public int lock = 0;
	public boolean disabledServer = false;

	public ShopEntry(ShopTab t)
	{
		tab = t;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("item", ItemStackSerializer.write(stack, false));

		if (buy > 0L)
		{
			nbt.setLong("buy", buy);
		}

		if (sell > 0L)
		{
			nbt.setLong("sell", sell);
		}

		if (stock != null)
		{
			nbt.setIntArray("stock", new int[] {stock.posX, stock.posY, stock.posZ, stock.dim});
		}

		if (createdBy != null)
		{
			nbt.setString("created_by", StringUtils.fromUUID(createdBy));
		}

		if (lock != 0)
		{
			nbt.setInteger("lock", lock);
		}

		if (disabledServer)
		{
			nbt.setBoolean("disabled_server", true);
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		page = nbt.getString("page");
		stack = ItemStackSerializer.read(nbt.getTag("item"));
		buy = nbt.getLong("buy");
		sell = nbt.getLong("sell");
		int[] p = nbt.getIntArray("stock");
		stock = p.length == 4 ? new BlockDimPos(p[0], p[1], p[2], p[3]) : null;
		createdBy = StringUtils.fromString(nbt.getString("created_by"));
		lock = nbt.getInteger("lock");

		if (lock == 0 && nbt.hasKey("lock"))
		{
			String slock = nbt.getString("lock");

			if (slock.startsWith("ftbquests:"))
			{
				try
				{
					lock = Integer.decode(slock.substring(10));
				}
				catch (Exception ex)
				{
				}
			}
		}

		disabledServer = nbt.getBoolean("disabled_server");
	}

	public boolean isUnlocked(@Nullable QuestData data)
	{
		if (lock == 0)
		{
			return true;
		}
		else if (data == null)
		{
			return false;
		}

		QuestObject object = tab.shop.file.get().get(lock);
		return object != null && object.isComplete(data);
	}

	public void getConfig(ConfigGroup group)
	{
		group.add("item", new ConfigItemStack.SimpleStack(() -> stack, v -> stack = v), new ConfigItemStack(ItemStack.EMPTY));
		group.addLong("buy", () -> buy, v -> buy = v, 1L, 0L, Long.MAX_VALUE);
		//group.addLong("sell", () -> sell, v -> sell = v, 0L, 0L, Long.MAX_VALUE);
		group.add("lock", new ConfigQuestObject(tab.shop.file.get(), lock, QuestObjectType.ALL_PROGRESSING_OR_NULL)
		{
			@Override
			public void setObject(int v)
			{
				lock = v;
			}

			@Override
			public int getObject()
			{
				return lock;
			}
		}, new ConfigQuestObject(tab.shop.file.get(), 0, QuestObjectType.ALL_PROGRESSING_OR_NULL));
		group.addBool("disabled_server", () -> disabledServer, v -> disabledServer = v, false);
	}

	public int getIndex()
	{
		return tab.entries.indexOf(this);
	}
}