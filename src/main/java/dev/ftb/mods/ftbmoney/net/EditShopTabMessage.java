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
public class EditShopTabMessage extends BaseC2SMessage {
	private final int tab;
	private final CompoundTag nbt;

	public EditShopTabMessage(ShopTab t, boolean delete) {
		tab = t.getIndex();
		nbt = delete ? null : t.serializeSettings();
	}

	public EditShopTabMessage(FriendlyByteBuf buf) {
		tab = buf.readVarInt();
		nbt = buf.readAnySizeNbt();
	}

	@Override
	public MessageType getType() {
		return FTBMoneyNetHandler.EDIT_SHOP_TAB;
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

			if (nbt == null) {
				t.shop.tabs.remove(tab);
			} else {
				t.shop.tabs.get(tab).deserializeSettings(nbt);
			}

			t.shop.markDirty();
		}
	}
}