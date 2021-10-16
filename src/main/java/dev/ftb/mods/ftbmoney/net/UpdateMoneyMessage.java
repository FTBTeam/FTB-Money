package dev.ftb.mods.ftbmoney.net;

import dev.ftb.mods.ftbmoney.FTBMoney;
import dev.ftb.mods.ftbteams.data.ClientTeamManager;
import dev.ftb.mods.ftbteams.data.KnownClientPlayer;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.BaseS2CMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class UpdateMoneyMessage extends BaseS2CMessage {
	private final UUID playerId;
	private final long money;

	public UpdateMoneyMessage(UUID id, long m) {
		playerId = id;
		money = m;
	}

	public UpdateMoneyMessage(FriendlyByteBuf buf) {
		playerId = buf.readUUID();
		money = buf.readVarLong();
	}

	@Override
	public MessageType getType() {
		return FTBMoneyNetHandler.UPDATE_MONEY;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUUID(playerId);
		buf.writeVarLong(money);
	}

	@Override
	public void handle(NetworkManager.PacketContext packetContext) {
		KnownClientPlayer team = ClientTeamManager.INSTANCE.getKnownPlayer(playerId);

		if (team != null) {
			FTBMoney.setMoney(team, money);
		}
	}
}