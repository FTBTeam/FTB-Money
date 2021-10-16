package dev.ftb.mods.ftbmoney;

import com.mojang.blaze3d.platform.InputConstants;
import dev.ftb.mods.ftblibrary.ui.CustomClickEvent;
import dev.ftb.mods.ftbmoney.gui.ShopScreen;
import dev.ftb.mods.ftbmoney.shop.Shop;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

/**
 * @author LatvianModder
 */
public class FTBMoneyClient extends FTBMoneyCommon {
	public static KeyMapping KEY_SHOP;
	public static final ResourceLocation OPEN_GUI = new ResourceLocation(FTBMoney.MOD_ID, "open_gui");

	@Override
	public void preInit() {
		ClientRegistry.registerKeyBinding(KEY_SHOP = new KeyMapping("key.ftbmoney.shop", KeyConflictContext.UNIVERSAL, KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.categories.ftbquests"));
		CustomClickEvent.EVENT.register(this::customClick);
		MinecraftForge.EVENT_BUS.addListener(this::keyInput);
	}

	public InteractionResult customClick(CustomClickEvent event) {
		if (Shop.CLIENT != null && event.getId().equals(OPEN_GUI)) {
			new ShopScreen().openGui();
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	public void keyInput(InputEvent.KeyInputEvent event) {
		if (FTBMoneyClient.KEY_SHOP.isDown()) {
			new ShopScreen().openGui();
		}
	}
}