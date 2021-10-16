package dev.ftb.mods.ftbmoney.net;

import dev.ftb.mods.ftbmoney.FTBMoney;
import dev.ftb.mods.ftbmoney.shop.Shop;
import dev.ftb.mods.ftbmoney.shop.ShopEntry;
import dev.ftb.mods.ftbmoney.shop.ShopTab;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.BaseC2SMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author LatvianModder
 */
public class BuyMessage extends BaseC2SMessage {
	private final int tab;
	private final int id;
	private final int count;

	public BuyMessage(ShopEntry e, int c) {
		tab = e.tab.getIndex();
		id = e.getIndex();
		count = c;
	}

	public BuyMessage(FriendlyByteBuf buf) {
		tab = buf.readVarInt();
		id = buf.readVarInt();
		count = buf.readVarInt();
	}

	@Override
	public MessageType getType() {
		return FTBMoneyNetHandler.BUY;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(tab);
		buf.writeVarInt(id);
		buf.writeVarInt(count);
	}

	@Override
	public void handle(NetworkManager.PacketContext context) {
		ServerPlayer player = (ServerPlayer) context.getPlayer();
		ShopTab t = Shop.SERVER.tabs.get(tab);
		ShopEntry entry = t.entries.get(id);

		if (entry.disabledServer && player.server.isDedicatedServer()) {
			return;
		}

		long money = FTBMoney.getMoney(player);

		if (money >= entry.buy * count && entry.isUnlocked(ServerQuestFile.INSTANCE.getData(player))) {
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
}