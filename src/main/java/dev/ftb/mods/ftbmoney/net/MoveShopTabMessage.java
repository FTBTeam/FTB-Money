package dev.ftb.mods.ftbmoney.net;

import dev.ftb.mods.ftbmoney.shop.Shop;
import dev.ftb.mods.ftbmoney.shop.ShopTab;
import dev.ftb.mods.ftbquests.util.NetUtils;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.BaseC2SMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @author LatvianModder
 */
public class MoveShopTabMessage extends BaseC2SMessage {
	private final int tab;
	private final boolean up;

	public MoveShopTabMessage(ShopTab t, boolean u) {
		tab = t.getIndex();
		up = u;
	}

	public MoveShopTabMessage(FriendlyByteBuf buf) {
		tab = buf.readVarInt();
		up = buf.readBoolean();
	}

	@Override
	public MessageType getType() {
		return FTBMoneyNetHandler.MOVE_SHOP_TAB;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(tab);
		buf.writeBoolean(up);
	}

	@Override
	public void handle(NetworkManager.PacketContext context) {
		if (NetUtils.canEdit(context)) {
			ShopTab t = Shop.SERVER.tabs.get(tab);

			if (up ? (tab > 0) : (tab < t.entries.size() - 1)) {
				Shop.SERVER.tabs.remove(tab);
				Shop.SERVER.tabs.add(up ? tab - 1 : tab + 1, t);
			}

			t.shop.markDirty();
		}
	}
}