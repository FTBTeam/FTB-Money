package com.feed_the_beast.mods.money.shop;

import com.feed_the_beast.ftblib.lib.item.ItemStackSerializer;
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
	public final List<ShopEntry> entries = new ArrayList<>();

	public ShopTab(Shop s)
	{
		shop = s;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("title", title);
		nbt.setTag("icon", ItemStackSerializer.write(icon, false));

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
		title = nbt.getString("title");
		icon = ItemStackSerializer.read(nbt.getTag("icon"));
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
}