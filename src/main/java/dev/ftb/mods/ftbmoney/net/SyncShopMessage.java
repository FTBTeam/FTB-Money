package dev.ftb.mods.ftbmoney.net;

import dev.ftb.mods.ftbmoney.shop.Shop;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.BaseS2CMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @author LatvianModder
 */
public class SyncShopMessage extends BaseS2CMessage {
	private final CompoundTag tag;

	public SyncShopMessage(CompoundTag t) {
		tag = t;
	}

	public SyncShopMessage(FriendlyByteBuf buf) {
		tag = buf.readAnySizeNbt();
	}

	@Override
	public MessageType getType() {
		return FTBMoneyNetHandler.SYNC_SHOP;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeNbt(tag);
	}

	@Override
	public void handle(NetworkManager.PacketContext packetContext) {
		Shop.CLIENT = new Shop(() -> ClientQuestFile.INSTANCE);
		Shop.CLIENT.deserializeNBT(tag);
		// FIXME: FTBMoneyJEIHelper.refresh();
	}
}