package com.feed_the_beast.mods.money.integration.jei;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.mods.money.FTBMoney;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class ShopCategory implements IRecipeCategory<ShopWrapper>
{
	public static final String UID = "ftbmoney.shop";

	private final IDrawable background;
	private final IDrawable icon;

	public ShopCategory(IGuiHelper guiHelper)
	{
		background = guiHelper.drawableBuilder(new ResourceLocation(FTBMoney.MOD_ID + ":textures/jei.png"), 0, 0, 128, 18).setTextureSize(128, 64).build();
		icon = guiHelper.drawableBuilder(new ResourceLocation(FTBLib.MOD_ID + ":textures/icons/money_bag.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
	}

	@Override
	public String getUid()
	{
		return UID;
	}

	@Override
	public String getTitle()
	{
		return I18n.format("sidebar_button.ftbmoney.shop");
	}

	@Override
	public String getModName()
	{
		return FTBMoney.MOD_NAME;
	}

	@Override
	public IDrawable getBackground()
	{
		return background;
	}

	@Override
	public IDrawable getIcon()
	{
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayout layout, ShopWrapper entry, IIngredients ingredients)
	{
		IGuiItemStackGroup stacks = layout.getItemStacks();
		stacks.init(0, false, 0, 0);
		stacks.set(ingredients);
	}
}