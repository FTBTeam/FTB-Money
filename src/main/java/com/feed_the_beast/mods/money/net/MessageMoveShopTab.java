package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbquests.FTBQuests;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopTab;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author LatvianModder
 */
public class MessageMoveShopTab extends MessageToServer
{
	private int tab;
	private boolean up;

	public MessageMoveShopTab()
	{
	}

	public MessageMoveShopTab(ShopTab t, boolean u)
	{
		tab = t.getIndex();
		up = u;
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
		data.writeBoolean(up);
	}

	@Override
	public void readData(DataIn data)
	{
		tab = data.readVarInt();
		up = data.readBoolean();
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		if (FTBQuests.canEdit(player))
		{
			ShopTab t = Shop.SERVER.tabs.get(tab);

			if (up ? (tab > 0) : (tab < t.entries.size() - 1))
			{
				Shop.SERVER.tabs.remove(tab);
				Shop.SERVER.tabs.add(up ? tab - 1 : tab + 1, t);
			}

			t.shop.markDirty();
		}
	}
}