package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import com.feed_the_beast.mods.money.shop.ShopTab;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author LatvianModder
 */
public class MessageBuy extends MessageToServer
{
	private int tab;
	private int id;
	private int count;

	public MessageBuy()
	{
	}

	public MessageBuy(ShopEntry e, int c)
	{
		tab = e.tab.getIndex();
		id = e.getIndex();
		count = c;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBMoneyNetHandler.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeVarInt(tab);
		data.writeVarInt(id);
		data.writeVarInt(count);
	}

	@Override
	public void readData(DataIn data)
	{
		tab = data.readVarInt();
		id = data.readVarInt();
		count = data.readVarInt();
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		ShopTab t = Shop.SERVER.tabs.get(tab);
		ShopEntry entry = t.entries.get(id);

		if (entry.disabledServer && player.server.isDedicatedServer())
		{
			return;
		}

		long money = FTBMoney.getMoney(player);

		if (money >= entry.buy * count && entry.isUnlocked(ServerQuestFile.INSTANCE.getData(player)))
		{
			ItemStack stack = entry.stack;

			if (stack.getCount() * count <= stack.getMaxStackSize())
			{
				ItemHandlerHelper.giveItemToPlayer(player, ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() * count));
			}
			else
			{
				for (int i = 0; i < count; i++)
				{
					ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
				}
			}

			FTBMoney.setMoney(player, money - entry.buy * count);
		}
	}
}