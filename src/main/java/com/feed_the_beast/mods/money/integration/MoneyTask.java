package com.feed_the_beast.mods.money.integration;

import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.config.ConfigLong;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestData;
import com.feed_the_beast.ftbquests.quest.task.ISingleLongValueTask;
import com.feed_the_beast.ftbquests.quest.task.Task;
import com.feed_the_beast.ftbquests.quest.task.TaskData;
import com.feed_the_beast.ftbquests.quest.task.TaskType;
import com.feed_the_beast.mods.money.FTBMoney;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class MoneyTask extends Task implements ISingleLongValueTask
{
	public static TaskType TYPE;

	public long value = 1L;

	public MoneyTask(Quest quest)
	{
		super(quest);
	}

	@Override
	public TaskType getType()
	{
		return TYPE;
	}

	@Override
	public long getMaxProgress()
	{
		return value;
	}

	@Override
	public String getMaxProgressString()
	{
		return FTBMoney.moneyString(value);
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);
		nbt.setLong("value", value);
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		value = nbt.getLong("value");
	}

	@Override
	public void writeNetData(DataOut data)
	{
		super.writeNetData(data);
		data.writeVarLong(value);
	}

	@Override
	public void readNetData(DataIn data)
	{
		super.readNetData(data);
		value = data.readVarLong();
	}

	@Override
	public ConfigLong getDefaultValue()
	{
		return new ConfigLong(value, 1L, Long.MAX_VALUE);
	}

	@Override
	public void setValue(long v)
	{
		value = v;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getConfig(ConfigGroup config)
	{
		super.getConfig(config);
		config.addLong("value", () -> value, v -> value = v, 1L, 1L, Long.MAX_VALUE).setDisplayName(new TextComponentTranslation("ftbquests.task.ftbquests.ftb_money"));
	}

	@Override
	public String getAltTitle()
	{
		return TextFormatting.GOLD + FTBMoney.moneyString(value);
	}

	@Override
	public boolean consumesResources()
	{
		return true;
	}

	@Override
	public TaskData createData(QuestData data)
	{
		return new Data(this, data);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addMouseOverText(List<String> list, @Nullable TaskData data)
	{
		list.add(TextFormatting.GRAY + I18n.format("ftbmoney.balance") + ": " + TextFormatting.GOLD + FTBMoney.moneyString(FTBMoney.getMoney(Minecraft.getMinecraft().player)));
	}

	public static class Data extends TaskData<MoneyTask>
	{
		private Data(MoneyTask task, QuestData data)
		{
			super(task, data);
		}

		@Override
		public String getProgressString()
		{
			return FTBMoney.moneyString(progress);
		}

		@Override
		public void submitTask(EntityPlayerMP player, ItemStack item)
		{
			long money = FTBMoney.getMoney(player);
			long add = Math.min(money, task.value - progress);

			if (add > 0)
			{
				FTBMoney.setMoney(player, money - add);
				addProgress(add);
			}
		}
	}
}