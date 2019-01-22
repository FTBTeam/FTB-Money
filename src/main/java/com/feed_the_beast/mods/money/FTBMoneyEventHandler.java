package com.feed_the_beast.mods.money;

import com.feed_the_beast.ftblib.lib.config.ConfigString;
import com.feed_the_beast.ftblib.lib.gui.IOpenableGui;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiEditConfigValue;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.feed_the_beast.ftbquests.quest.QuestObjectBase;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.quest.reward.QuestReward;
import com.feed_the_beast.ftbquests.quest.reward.QuestRewardType;
import com.feed_the_beast.ftbquests.quest.task.QuestTaskType;
import com.feed_the_beast.mods.money.integration.MoneyReward;
import com.feed_the_beast.mods.money.integration.MoneyTask;
import com.feed_the_beast.mods.money.net.MessageUpdateMoney;
import com.feed_the_beast.mods.money.shop.Shop;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.function.Consumer;

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

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onWorldLoaded(WorldEvent.Load event)
	{
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0)
		{
			Shop.SERVER = new Shop(ServerQuestFile.INSTANCE);
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

	@SubscribeEvent
	public static void registerTasks(RegistryEvent.Register<QuestTaskType> event)
	{
		event.getRegistry().register(MoneyTask.TYPE = new QuestTaskType(MoneyTask::new).setRegistryName("money").setIcon(Icon.getIcon(FTBMoney.MOD_ID + ":textures/beastcoin.png")));
	}

	@SubscribeEvent
	public static void registerRewards(RegistryEvent.Register<QuestRewardType> event)
	{
		event.getRegistry().register(MoneyReward.TYPE = new QuestRewardType(MoneyReward::new).setRegistryName("money").setIcon(Icon.getIcon(FTBMoney.MOD_ID + ":textures/beastcoin.png")));

		MoneyReward.TYPE.setGuiProvider(new QuestRewardType.GuiProvider()
		{
			@Override
			@SideOnly(Side.CLIENT)
			public void openCreationGui(IOpenableGui gui, QuestObjectBase parent, Consumer<QuestReward> callback)
			{
				new GuiEditConfigValue("money", new ConfigString(""), (value, set) -> {
					gui.openGui();
					if (set)
					{
						try
						{
							String[] s = value.getString().split("-", 2);
							MoneyReward reward = new MoneyReward(parent);
							reward.value = Long.parseLong(s[0].trim());

							if (s.length == 2)
							{
								long max = Long.parseLong(s[1].trim());

								if (max - reward.value <= Integer.MAX_VALUE)
								{
									reward.randomBonus = (int) (max - reward.value);
								}
							}

							callback.accept(reward);
						}
						catch (Exception ex)
						{
						}
					}
				}).openGui();
			}
		});
	}
}