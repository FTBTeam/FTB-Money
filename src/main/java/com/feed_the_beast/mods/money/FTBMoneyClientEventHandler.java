package com.feed_the_beast.mods.money;

import com.feed_the_beast.ftblib.events.client.CustomClickEvent;
import com.feed_the_beast.mods.money.net.MessageOpenShop;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBMoney.MOD_ID, value = Side.CLIENT)
public class FTBMoneyClientEventHandler
{
	@SubscribeEvent
	public static void onCustomClick(CustomClickEvent event)
	{
		if (event.getID().getNamespace().equals(FTBMoney.MOD_ID))
		{
			if (event.getID().getPath().equals("open_gui"))
			{
				new MessageOpenShop().sendToServer();
			}

			event.setCanceled(true);
		}
	}
}