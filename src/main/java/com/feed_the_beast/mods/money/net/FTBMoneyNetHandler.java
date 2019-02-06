package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.mods.money.FTBMoney;

/**
 * @author LatvianModder
 */
public class FTBMoneyNetHandler
{
	public static final NetworkWrapper NET = NetworkWrapper.newWrapper(FTBMoney.MOD_ID);

	public static void init()
	{
		NET.register(new MessageUpdateMoney());
		NET.register(new MessageSyncShop());
		NET.register(new MessageBuy());
		NET.register(new MessageAddShopTab());
		NET.register(new MessageEditShopTab());
		NET.register(new MessageMoveShopTab());
		NET.register(new MessageAddShopEntry());
		NET.register(new MessageEditShopEntry());
	}
}