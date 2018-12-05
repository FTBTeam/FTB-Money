package com.feed_the_beast.mods.money;

import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.feed_the_beast.mods.money.net.MessageUpdateMoney;
import com.feed_the_beast.mods.money.shop.Shop;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.File;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBMoney.MOD_ID)
public class FTBMoneyEventHandler
{
	public static File getFile(World world)
	{
		if (FTBMoneyConfig.general.use_config_store)
		{
			return new File(Loader.instance().getConfigDir(), "ftbmoneyshop.nbt");
		}

		return new File(world.getSaveHandler().getWorldDirectory(), "data/ftbmoneyshop.nbt");
	}

	@SubscribeEvent
	public static void onWorldLoaded(WorldEvent.Load event)
	{
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0)
		{
			Shop.SERVER = new Shop();
			NBTTagCompound nbt = NBTUtils.readNBT(getFile(event.getWorld()));

			if (nbt != null)
			{
				Shop.SERVER.deserializeNBT(nbt);
			}
		}
	}

	@SubscribeEvent
	public static void onWorldSaved(WorldEvent.Save event)
	{
		if (Shop.SERVER != null && Shop.SERVER.shouldSave && !event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0)
		{
			Shop.SERVER.shouldSave = false;
			NBTUtils.writeNBTSafe(getFile(event.getWorld()), Shop.SERVER.serializeNBT());
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			new MessageUpdateMoney(FTBMoney.getMoney(event.player)).sendTo((EntityPlayerMP) event.player);
		}
	}
}