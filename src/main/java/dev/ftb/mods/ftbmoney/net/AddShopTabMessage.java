package dev.ftb.mods.ftbmoney.net;

import dev.ftb.mods.ftbmoney.shop.Shop;
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
public class AddShopTabMessage extends BaseC2SMessage {
	private final CompoundTag nbt;

	public AddShopTabMessage(ShopTab t) {
		nbt = t.serializeNBT();
	}

	public AddShopTabMessage(FriendlyByteBuf buf) {
		nbt = buf.readAnySizeNbt();
	}

	@Override
	public MessageType getType() {
		return FTBMoneyNetHandler.ADD_SHOP_TAB;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeNbt(nbt);
	}

	@Override
	public void handle(NetworkManager.PacketContext context) {
		if (NetUtils.canEdit(context)) {
			ShopTab tab = new ShopTab(Shop.SERVER);
			tab.deserializeNBT(nbt);
			tab.shop.tabs.add(tab);
			tab.shop.markDirty();
		}
	}
}