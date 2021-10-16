package dev.ftb.mods.ftbmoney.integration;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbmoney.FTBMoney;
import dev.ftb.mods.ftbquests.net.DisplayRewardToastMessage;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author LatvianModder
 */
public class MoneyReward extends Reward {
	public static RewardType TYPE;

	public long value = 1L;
	public int randomBonus = 0;

	public MoneyReward(Quest quest) {
		super(quest);
	}

	@Override
	public RewardType getType() {
		return TYPE;
	}

	@Override
	public void writeData(CompoundTag nbt) {
		nbt.putLong("ftb_money", value);

		if (randomBonus > 0) {
			nbt.putInt("random_bonus", randomBonus);
		}
	}

	@Override
	public void readData(CompoundTag nbt) {
		value = nbt.getLong("ftb_money");
		randomBonus = nbt.getInt("random_bonus");
	}

	@Override
	public void writeNetData(FriendlyByteBuf buf) {
		super.writeNetData(buf);
		buf.writeVarLong(value);
		buf.writeVarInt(randomBonus);
	}

	@Override
	public void readNetData(FriendlyByteBuf buf) {
		super.readNetData(buf);
		value = buf.readVarLong();
		randomBonus = buf.readVarInt();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getConfig(ConfigGroup config) {
		super.getConfig(config);
		config.addLong("value", value, v -> value = v, 1L, 1L, Long.MAX_VALUE).setNameKey("ftbquests.reward.ftbmoney.money");
		config.addInt("random_bonus", randomBonus, v -> randomBonus = v, 0, 0, Integer.MAX_VALUE).setNameKey("ftbquests.reward.random_bonus");
	}

	@Override
	public void claim(ServerPlayer player, boolean notify) {
		long money = FTBMoney.getMoney(player);
		long added = value + player.level.random.nextInt(randomBonus + 1);
		FTBMoney.setMoney(player, money + added);

		if (notify) {
			new DisplayRewardToastMessage(id, FTBMoney.moneyComponent(added), Icon.getIcon("ftbmoney:textures/icon.png")).sendTo(player);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Component getAltTitle() {
		if (randomBonus > 0) {
			return new TextComponent(FTBMoney.moneyString(value) + " - " + FTBMoney.moneyString(value + randomBonus)).withStyle(ChatFormatting.GOLD);
		}

		return FTBMoney.moneyComponent(value);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public String getButtonText() {
		if (randomBonus > 0) {
			return randomBonus + "-" + Long.toUnsignedString(value + randomBonus);
		}

		return Long.toUnsignedString(value);
	}
}