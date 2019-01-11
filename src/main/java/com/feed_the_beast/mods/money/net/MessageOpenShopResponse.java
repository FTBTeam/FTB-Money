package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbquests.client.ClientQuestFile;
import com.feed_the_beast.ftbquests.quest.ITeamData;
import com.feed_the_beast.mods.money.gui.GuiShop;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import com.feed_the_beast.mods.money.shop.ShopTab;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class MessageOpenShopResponse extends MessageToClient
{
	private NBTTagCompound nbt;
	private int[] locked;

	public MessageOpenShopResponse()
	{
	}

	public MessageOpenShopResponse(@Nullable ITeamData team)
	{
		nbt = Shop.SERVER.serializeNBT();

		IntArrayList list = new IntArrayList();
		int i = 0;

		for (ShopTab tab : Shop.SERVER.tabs)
		{
			for (ShopEntry entry : tab.entries)
			{
				if (!entry.isUnlocked(team))
				{
					list.add(i);
				}

				i++;
			}
		}

		locked = list.toIntArray();
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBMoneyNetHandler.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeNBT(nbt);
		data.writeVarInt(locked.length);

		for (int i : locked)
		{
			data.writeVarInt(i);
		}
	}

	@Override
	public void readData(DataIn data)
	{
		nbt = data.readNBT();
		locked = new int[data.readVarInt()];

		for (int i = 0; i < locked.length; i++)
		{
			locked[i] = data.readVarInt();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		Shop shop = new Shop(ClientQuestFile.INSTANCE);
		shop.deserializeNBT(nbt);
		new GuiShop(shop, new IntOpenHashSet(locked)).openGui();
	}
}