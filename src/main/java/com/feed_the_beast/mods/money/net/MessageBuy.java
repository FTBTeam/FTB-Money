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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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

		if (entry.disabledServer && player.getServer().isDedicatedServer())
		{
			return;
		}

		long money = FTBMoney.getMoney(player);

		if (entry.buy >= 1) {
			if (money >= entry.buy * count && entry.isUnlocked(Objects.requireNonNull(ServerQuestFile.INSTANCE.getData(player)).getFile())) {
				ItemStack stack = entry.stack;

				if (stack.getCount() * count <= stack.getMaxStackSize()) {
					ItemHandlerHelper.giveItemToPlayer(player, ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() * count));
				} else {
					for (int i = 0; i < count; i++) {
						ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
					}
				}

				FTBMoney.setMoney(player, money - entry.buy * count);
			}
		}
		else if (entry.sell >= 1) {
			Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
			int slot = 0;
			int current_items = 0;
			for (ItemStack next : player.inventory.mainInventory) {
				if (next != null) {
					if (next.isItemEqual(entry.stack)) {
						current_items += next.getCount();
						items.put(slot, next);
					}
				}
				slot++;
			}

			if (current_items >= entry.stack.getCount() * count && entry.sell > 0) {
				int remaining_items = entry.stack.getCount() * count;
				AtomicLong addmoney = new AtomicLong(0);
				AtomicInteger selled = new AtomicInteger(0);
				// Sell each item
				for (Map.Entry<Integer, ItemStack> entrys : items.entrySet()) {
					ItemStack item = entrys.getValue();

					if (remaining_items >= 0) {
						if (item.getCount() <= remaining_items) {
							// Sell the entire stack
							addmoney.addAndGet(entry.sell);
							remaining_items -= item.getCount();
							selled.addAndGet(item.getCount());
							item.shrink(item.getCount());
						} else {
							// Sell part of the stack
							addmoney.addAndGet(entry.sell);
							selled.addAndGet(remaining_items);
							item.shrink(remaining_items);
							break; // Exit early since we've sold enough items
						}
					}
				}
				FTBMoney.setMoney(player, money + addmoney.get() * selled.get());
			}
			else if (entry.sell == 0 && entry.buy == 0) {
				ItemStack stack = entry.stack;

				if (stack.getCount() * count <= stack.getMaxStackSize()) {
					ItemHandlerHelper.giveItemToPlayer(player, ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() * count));
				} else {
					for (int i = 0; i < count; i++) {
						ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
					}
				}
			}
		}
	}
}