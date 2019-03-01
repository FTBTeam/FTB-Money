package com.feed_the_beast.mods.money;

import com.feed_the_beast.ftblib.FTBLib;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

/**
 * @author LatvianModder
 */
public class FTBMoneyClient extends FTBMoneyCommon
{
	public static KeyBinding KEY_SHOP;

	@Override
	public void preInit()
	{
		ClientRegistry.registerKeyBinding(KEY_SHOP = new KeyBinding("key.ftbmoney.shop", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NONE, FTBLib.KEY_CATEGORY));
	}
}