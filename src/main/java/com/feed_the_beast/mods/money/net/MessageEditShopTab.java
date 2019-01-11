package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbquests.FTBQuests;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopTab;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author LatvianModder
 */
public class MessageEditShopTab extends MessageToServer
{
	private int tab;
	private NBTTagCompound nbt;

	public MessageEditShopTab()
	{
	}

	public MessageEditShopTab(ShopTab t, boolean delete)
	{
		tab = t.getIndex();
		nbt = delete ? null : t.serializeSettings();
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBMoneyNetHandler.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeVarInt(tab);
		data.writeNBT(nbt);
	}

	@Override
	public void readData(DataIn data)
	{
		tab = data.readVarInt();
		nbt = data.readNBT();
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		if (FTBQuests.canEdit(player))
		{
			ShopTab t = Shop.SERVER.tabs.get(tab);

			if (nbt == null)
			{
				t.shop.tabs.remove(tab);
			}
			else
			{
				t.shop.tabs.get(tab).deserializeSettings(nbt);
			}

			t.shop.markDirty();
		}
	}
}