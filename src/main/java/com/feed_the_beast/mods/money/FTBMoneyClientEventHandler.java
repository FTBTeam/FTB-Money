package com.feed_the_beast.mods.money;

import com.feed_the_beast.ftblib.events.client.CustomClickEvent;
import com.feed_the_beast.mods.money.gui.GuiShop;
import com.feed_the_beast.mods.money.shop.Shop;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
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
				if (Shop.CLIENT != null)
				{
					new GuiShop().openGui();
				}
			}

			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onKeyEvent(InputEvent.KeyInputEvent event)
	{
		if (FTBMoneyClient.KEY_SHOP.isPressed())
		{
			new GuiShop().openGui();
		}
	}
}