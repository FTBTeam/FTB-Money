package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
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
		new MessageOpenShopResponse(ServerQuestFile.INSTANCE.getData(FTBLibAPI.getTeam(player.getUniqueID()))).sendTo(player);
	}
}