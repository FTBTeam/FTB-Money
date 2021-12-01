package dev.ftb.mods.ftbmoney;

import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftbmoney.integration.MoneyReward;
import dev.ftb.mods.ftbmoney.integration.MoneyTask;
import dev.ftb.mods.ftbmoney.net.FTBMoneyNetHandler;
import dev.ftb.mods.ftbmoney.net.SyncShopMessage;
import dev.ftb.mods.ftbmoney.net.UpdateMoneyMessage;
import dev.ftb.mods.ftbmoney.shop.Shop;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import dev.ftb.mods.ftbquests.quest.reward.RewardTypes;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.ClientTeamManager;
import dev.ftb.mods.ftbteams.data.KnownClientPlayer;
import dev.ftb.mods.ftbteams.data.PlayerTeam;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Mod(FTBMoney.MOD_ID)
@Mod.EventBusSubscriber(modid = FTBMoney.MOD_ID)
public class FTBMoney {
	public static final String MOD_ID = "ftbmoney";
	public static final Logger LOGGER = LogManager.getLogger("FTB Money");

	public FTBMoney() {
		FTBMoneyNetHandler.init();
		DistExecutor.safeRunForDist(() -> FTBMoneyClient::new, () -> FTBMoneyCommon::new).preInit();
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	}

	private void setup(FMLCommonSetupEvent event) {
		MoneyTask.TYPE = TaskTypes.register(new ResourceLocation(MOD_ID, "money"), MoneyTask::new, () -> Icon.getIcon(MOD_ID + ":textures/icon.png"));
		MoneyReward.TYPE = RewardTypes.register(new ResourceLocation(MOD_ID, "money"), MoneyReward::new, () -> Icon.getIcon(MOD_ID + ":textures/icon.png"));

		MoneyReward.TYPE.setGuiProvider(new RewardType.GuiProvider() {
			@Override
			@OnlyIn(Dist.CLIENT)
			public void openCreationGui(Runnable gui, Quest quest, Consumer<Reward> callback) {
				StringConfig money = new StringConfig(Pattern.compile("^\\d+(?:-\\d+)?$"));

				money.onClicked(MouseButton.LEFT, set -> {
					gui.run();
					if (set) {
						try {
							String[] s = money.value.split("-", 2);
							MoneyReward reward = new MoneyReward(quest);
							reward.value = Long.parseLong(s[0].trim());

							if (s.length == 2) {
								long max = Long.parseLong(s[1].trim());

								if (max - reward.value <= Integer.MAX_VALUE) {
									reward.randomBonus = (int) (max - reward.value);
								}
							}

							callback.accept(reward);
						} catch (Exception ex) {
						}
					}
				});
			}
		});
	}

	public static Path getFile() {
		return FMLPaths.CONFIGDIR.get().resolve("ftbmoneyshop.snbt");
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onWorldLoaded(WorldEvent.Load event) {
		if (event.getWorld() instanceof Level && !event.getWorld().isClientSide() && ((Level) event.getWorld()).dimension() == Level.OVERWORLD) {
			Shop.SERVER = new Shop(() -> ServerQuestFile.INSTANCE);
			CompoundTag nbt = SNBT.read(getFile());

			if (nbt != null) {
				Shop.SERVER.deserializeNBT(nbt);
			}
		}
	}

	@SubscribeEvent
	public static void onWorldSaved(WorldEvent.Save event) {
		if (event.getWorld() instanceof Level && Shop.SERVER != null && Shop.SERVER.shouldSave && !event.getWorld().isClientSide() && ((Level) event.getWorld()).dimension() == Level.OVERWORLD) {
			Shop.SERVER.shouldSave = false;
			SNBT.write(getFile(), Shop.SERVER.serializeNBT());
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getPlayer() instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) event.getPlayer();
			new SyncShopMessage(Shop.SERVER.serializeNBT()).sendTo(player);
			// new UpdateMoneyMessage(player.getUUID(), getMoney(player)).sendToAll(player.server);
		}
	}

	public static long getMoney(ServerPlayer player) {
		PlayerTeam team = FTBTeamsAPI.getManager().getInternalPlayerTeam(player.getUUID());
		return team.getExtraData().getLong("Money");
	}

	public static void setMoney(ServerPlayer player, long money) {
		PlayerTeam team = FTBTeamsAPI.getManager().getInternalPlayerTeam(player.getUUID());

		if (money != team.getExtraData().getLong("Money")) {
			team.getExtraData().putLong("Money", money);
			team.save();
			new UpdateMoneyMessage(player.getUUID(), money).sendToAll(player.server);
		}
	}

	public static void addMoney(ServerPlayer player, long money) {
		PlayerTeam team = FTBTeamsAPI.getManager().getInternalPlayerTeam(player.getUUID());
		long balance = team.getExtraData().getLong("Money");
		long current = balance + money;
		team.getExtraData().putLong("Money", current);
		team.save();
		new UpdateMoneyMessage(player.getUUID(), current).sendToAll(player.server);
	}

	public static long getMoney(KnownClientPlayer player) {
		return player.getExtraData().getLong("Money");
	}

	public static void setMoney(KnownClientPlayer player, long money) {
		player.getExtraData().putLong("Money", money);
	}

	public static void addMoney(KnownClientPlayer player, long money) {
		player.getExtraData().putLong("Money", player.getExtraData().getLong("Money") + money);
	}

	public static long getClientMoney() {
		return ClientTeamManager.INSTANCE.selfKnownPlayer.getExtraData().getLong("Money");
	}

	private void registerCommands(RegisterCommandsEvent event) {
		FTBMoneyCommands.registerCommands(event.getDispatcher());
	}

	public static String moneyString(long money) {
		return String.format("◎ %,d", money);
	}

	public static Component moneyComponent(long money) {
		return new TextComponent(moneyString(money)).withStyle(ChatFormatting.GOLD);
	}
}
