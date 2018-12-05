package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopTab;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * @author LatvianModder
 */
public class MessageDeleteShopTab extends MessageToServer
{
	private int id;

	public MessageDeleteShopTab()
	{
	}

	public MessageDeleteShopTab(ShopTab t)
	{
		id = t.getIndex();
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBMoneyNetHandler.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeVarInt(id);
	}

	@Override
	public void readData(DataIn data)
	{
		id = data.readVarInt();
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		if (PermissionAPI.hasPermission(player, FTBMoney.PERM_EDIT_SHOP))
		{
			ShopTab tab = Shop.SERVER.tabs.get(id);
			tab.shop.tabs.remove(tab);
			tab.shop.markDirty();
		}
	}
}