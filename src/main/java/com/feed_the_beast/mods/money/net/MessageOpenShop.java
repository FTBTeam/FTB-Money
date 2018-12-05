package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author LatvianModder
 */
public class MessageOpenShop extends MessageToServer
{
	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBMoneyNetHandler.NET;
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		new MessageOpenShopResponse(player).sendTo(player);
	}
}