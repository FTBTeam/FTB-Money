package dev.ftb.mods.ftbmoney.net;

import dev.ftb.mods.ftbmoney.shop.Shop;
import dev.ftb.mods.ftbmoney.shop.ShopEntry;
import dev.ftb.mods.ftbmoney.shop.ShopTab;
import dev.ftb.mods.ftbquests.util.NetUtils;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.BaseC2SMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @author LatvianModder
 */
public class AddShopEntryMessage extends BaseC2SMessage {
	private final int tab;
	private final CompoundTag nbt;

	public AddShopEntryMessage(ShopEntry e) {
		tab = e.tab.getIndex();
		nbt = e.serializeNBT();
	}

	public AddShopEntryMessage(FriendlyByteBuf buf) {
		tab = buf.readVarInt();
		nbt = buf.readAnySizeNbt();
	}

	@Override
	public MessageType getType() {
		return FTBMoneyNetHandler.ADD_SHOP_ENTRY;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(tab);
		buf.writeNbt(nbt);
	}

	@Override
	public void handle(NetworkManager.PacketContext context) {
		if (NetUtils.canEdit(context)) {
			ShopTab t = Shop.SERVER.tabs.get(tab);
			ShopEntry entry = new ShopEntry(t);
			entry.deserializeNBT(nbt);
			t.entries.add(entry);
			t.shop.markDirty();
		}
	}
}