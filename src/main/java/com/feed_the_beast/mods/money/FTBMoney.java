package com.feed_the_beast.mods.money;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.feed_the_beast.mods.money.command.CommandImportItemsFromChest;
import com.feed_the_beast.mods.money.command.CommandMoney;
import com.feed_the_beast.mods.money.command.CommandSetMoney;
import com.feed_the_beast.mods.money.net.FTBMoneyNetHandler;
import com.feed_the_beast.mods.money.net.MessageUpdateMoney;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
		modid = FTBMoney.MOD_ID,
		name = FTBMoney.MOD_NAME,
		version = FTBMoney.VERSION,
		acceptableRemoteVersions = "*",
		dependencies = FTBLib.THIS_DEP + ";required-after:ftbquests"
)
public class FTBMoney
{
	public static final String MOD_ID = "ftbmoney";
	public static final String MOD_NAME = "FTB Money";
	public static final String VERSION = "0.0.0.ftbmoney";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		FTBMoneyNetHandler.init();
	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandMoney());
		event.registerServerCommand(new CommandSetMoney());
		event.registerServerCommand(new CommandImportItemsFromChest());
	}

	public static long getMoney(EntityPlayer player)
	{
		long money = NBTUtils.getPersistedData(player, false).getLong("ftb_money");

		if (money == 0L)
		{
			money = player.getEntityData().getLong("ftb_money");

			if (money > 0L)
			{
				NBTUtils.getPersistedData(player, true).setLong("ftb_money", money);
				player.getEntityData().removeTag("ftb_money");
			}
		}

		return money;
	}

	public static void setMoney(EntityPlayer player, long money)
	{
		if (money <= 0L)
		{
			NBTUtils.getPersistedData(player, false).removeTag("ftb_money");
		}
		else
		{
			NBTUtils.getPersistedData(player, true).setLong("ftb_money", money);
		}

		if (!player.world.isRemote)
		{
			new MessageUpdateMoney(money).sendTo((EntityPlayerMP) player);
		}
	}

	public static String moneyString(long money)
	{
		return String.format("\u0398 %,d", money);
	}

	public static ITextComponent moneyComponent(long money)
	{
		ITextComponent component = new TextComponentString(moneyString(money));
		component.getStyle().setColor(TextFormatting.GOLD);
		return component;
	}
}