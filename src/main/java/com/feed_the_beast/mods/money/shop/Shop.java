package com.feed_the_beast.mods.money.shop;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class Shop implements INBTSerializable<NBTTagCompound>
{
	public static Shop SERVER;

	public final List<ShopTab> tabs = new ArrayList<>();
	public boolean shouldSave = false;
	public int nextNetID = 0;

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList b = new NBTTagList();

		for (ShopTab tab : tabs)
		{
			b.appendTag(tab.serializeNBT());
		}

		nbt.setTag("tabs", b);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		tabs.clear();
		NBTTagList t = nbt.getTagList("tabs", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < t.tagCount(); i++)
		{
			NBTTagCompound n = t.getCompoundTagAt(i);
			ShopTab tab = new ShopTab(this);
			tab.deserializeNBT(n);
			tab.netID = ++nextNetID;
			tabs.add(tab);
		}
	}

	@Nullable
	public ShopTab getTab(int netID)
	{
		for (ShopTab tab : tabs)
		{
			if (tab.netID == netID)
			{
				return tab;
			}
		}

		return null;
	}

	public void markDirty()
	{
		shouldSave = true;
	}
}