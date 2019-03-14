package com.feed_the_beast.mods.money.shop;

import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.config.ConfigItemStack;
import com.latmod.mods.itemfilters.item.ItemStackSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ShopTab implements INBTSerializable<NBTTagCompound>
{
	public final Shop shop;
	public String title = "";
	public ItemStack icon = ItemStack.EMPTY;
	public int lock = 0;
	public final List<ShopEntry> entries = new ArrayList<>();

	public ShopTab(Shop s)
	{
		shop = s;
	}

	public NBTTagCompound serializeSettings()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("title", title);
		nbt.setTag("icon", ItemStackSerializer.write(icon, false));

		if (lock != 0)
		{
			nbt.setInteger("lock", lock);
		}

		return nbt;
	}

	public void deserializeSettings(NBTTagCompound nbt)
	{
		title = nbt.getString("title");
		icon = ItemStackSerializer.read(nbt.getTag("icon"));
		lock = nbt.getInteger("lock");
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = serializeSettings();
		NBTTagList e = new NBTTagList();

		for (ShopEntry entry : entries)
		{
			e.appendTag(entry.serializeNBT());
		}

		nbt.setTag("entries", e);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		deserializeSettings(nbt);
		entries.clear();
		NBTTagList e = nbt.getTagList("entries", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < e.tagCount(); i++)
		{
			ShopEntry entry = new ShopEntry(this);
			entry.deserializeNBT(e.getCompoundTagAt(i));

			if (!entry.stack.isEmpty())
			{
				entries.add(entry);
			}
		}
	}

	public int getIndex()
	{
		return shop.tabs.indexOf(this);
	}

	public void getConfig(ConfigGroup config)
	{
		config.addString("title", () -> title, v -> title = v, "");
		config.add("icon", new ConfigItemStack.SimpleStack(() -> icon, v -> icon = v), new ConfigItemStack(ItemStack.EMPTY));
	}
}