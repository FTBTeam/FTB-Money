package com.feed_the_beast.mods.money;

import com.feed_the_beast.mods.money.net.MessageUpdateMoney;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBMoney.MOD_ID)
public class FTBMoneyEventHandler
{
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		new MessageUpdateMoney(FTBMoney.getMoney(event.player)).sendTo((EntityPlayerMP) event.player);
	}
}