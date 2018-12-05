package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
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
	private int id;
	private int count;

	public MessageBuy()
	{
	}

	public MessageBuy(int i, int c)
	{
		id = i;
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
		data.writeVarInt(id);
		data.writeVarInt(count);
	}

	@Override
	public void readData(DataIn data)
	{
		id = data.readVarInt();
		count = data.readVarInt();
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		for (ShopTab tab : Shop.SERVER.tabs)
		{
			ShopEntry entry = tab.getEntry(id);

			if (entry != null)
			{
				long money = FTBMoney.getMoney(player);

				if (money >= entry.buy * count)
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

				return;
			}
		}
	}
}