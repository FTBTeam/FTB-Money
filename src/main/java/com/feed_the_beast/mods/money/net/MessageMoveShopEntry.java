package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import com.feed_the_beast.mods.money.shop.ShopTab;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * @author LatvianModder
 */
public class MessageMoveShopEntry extends MessageToServer
{
	private int tab;
	private int id;
	private boolean up;

	public MessageMoveShopEntry()
	{
	}

	public MessageMoveShopEntry(ShopEntry e, boolean u)
	{
		tab = e.tab.getIndex();
		id = e.getIndex();
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
		data.writeVarInt(id);
		data.writeBoolean(up);
	}

	@Override
	public void readData(DataIn data)
	{
		tab = data.readVarInt();
		id = data.readVarInt();
		up = data.readBoolean();
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		if (PermissionAPI.hasPermission(player, FTBMoney.PERM_EDIT_SHOP))
		{
			ShopTab t = Shop.SERVER.tabs.get(tab);
			ShopEntry e = t.entries.get(id);

			if (up ? (id > 0) : (id < t.entries.size() - 1))
			{
				t.entries.remove(id);
				t.entries.add(up ? id - 1 : id + 1, e);
			}

			t.shop.markDirty();
		}
	}
}