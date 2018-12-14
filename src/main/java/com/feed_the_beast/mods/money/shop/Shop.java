package com.feed_the_beast.mods.money.shop;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class Shop implements INBTSerializable<NBTTagCompound>
{
	public static final Pattern PATTERN = Pattern.compile("[^a-zA-Z0-9]");

	public static Shop SERVER;

	public final List<ShopTab> tabs = new ArrayList<>();
	public boolean shouldSave = false;

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
			tabs.add(tab);
		}
	}

	public void markDirty()
	{
		shouldSave = true;
	}

	@Nullable
	public ShopTab getTab(String name)
	{
		String s = PATTERN.matcher(name).replaceAll("").toLowerCase();

		for (ShopTab tab : tabs)
		{
			if (PATTERN.matcher(tab.title).replaceAll("").toLowerCase().equals(s))
			{
				return tab;
			}
		}

		for (ShopTab tab : tabs)
		{
			if (PATTERN.matcher(tab.title).replaceAll("").toLowerCase().contains(s))
			{
				return tab;
			}
		}

		return null;
	}
}