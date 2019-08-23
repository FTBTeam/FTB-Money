package com.feed_the_beast.mods.money.integration;

import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftbquests.net.MessageDisplayRewardToast;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.reward.Reward;
import com.feed_the_beast.ftbquests.quest.reward.RewardType;
import com.feed_the_beast.mods.money.FTBMoney;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class MoneyReward extends Reward
{
	public static RewardType TYPE;

	public long value = 1L;
	public int randomBonus = 0;

	public MoneyReward(Quest quest)
	{
		super(quest);
	}

	@Override
	public RewardType getType()
	{
		return TYPE;
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		nbt.setLong("ftb_money", value);

		if (randomBonus > 0)
		{
			nbt.setInteger("random_bonus", randomBonus);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		value = nbt.getLong("ftb_money");
		randomBonus = nbt.getInteger("random_bonus");
	}

	@Override
	public void writeNetData(DataOut data)
	{
		super.writeNetData(data);
		data.writeVarLong(value);
		data.writeVarInt(randomBonus);
	}

	@Override
	public void readNetData(DataIn data)
	{
		super.readNetData(data);
		value = data.readVarLong();
		randomBonus = data.readVarInt();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getConfig(ConfigGroup config)
	{
		super.getConfig(config);
		config.addLong("value", () -> value, v -> value = v, 1L, 1L, Long.MAX_VALUE).setDisplayName(new TextComponentTranslation("ftbquests.reward.ftbmoney.money"));
		config.addInt("random_bonus", () -> randomBonus, v -> randomBonus = v, 0, 0, Integer.MAX_VALUE).setDisplayName(new TextComponentTranslation("ftbquests.reward.random_bonus"));
	}

	@Override
	public void claim(EntityPlayerMP player, boolean notify)
	{
		long added = value + player.world.rand.nextInt(randomBonus + 1);
		FTBMoney.setMoney(player, FTBMoney.getMoney(player) + added);

		if (notify)
		{
			new MessageDisplayRewardToast(id, FTBMoney.moneyComponent(added), Icon.getIcon("ftbmoney:textures/beastcoinmini.png")).sendTo(player);
		}
	}

	@Override
	public String getAltTitle()
	{
		if (randomBonus > 0)
		{
			return TextFormatting.GOLD + FTBMoney.moneyString(value) + " - " + FTBMoney.moneyString(value + randomBonus);
		}

		return TextFormatting.GOLD + FTBMoney.moneyString(value);
	}

	@Override
	public String getButtonText()
	{
		if (randomBonus > 0)
		{
			return randomBonus + "-" + Long.toUnsignedString(value + randomBonus);
		}

		return Long.toUnsignedString(value);
	}
}