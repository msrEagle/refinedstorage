package refinedstorage.tile.solderer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorageUtils;

import java.util.ArrayList;
import java.util.List;

public class SoldererRegistry {
    public static List<ISoldererRecipe> recipes = new ArrayList<ISoldererRecipe>();

    public static void addRecipe(ISoldererRecipe recipe) {
        recipes.add(recipe);
    }

    public static ISoldererRecipe getRecipe(IItemHandler inventory) {
        for (ISoldererRecipe recipe : recipes) {
            boolean ok = true;

            for (int i = 0; i < 3; ++i) {
                if (!RefinedStorageUtils.compareStackNoQuantity(recipe.getRow(i), inventory.getStackInSlot(i))) {
                    ok = false;
                }

                if (inventory.getStackInSlot(i) != null && recipe.getRow(i) != null) {
                    if (inventory.getStackInSlot(i).stackSize < recipe.getRow(i).stackSize) {
                        ok = false;
                    }
                }
            }

            if (ok) {
                return recipe;
            }
        }

        return null;
    }

    public static ISoldererRecipe getRecipe(ItemStack result) {
        for (ISoldererRecipe recipe : recipes) {
            if (RefinedStorageUtils.compareStack(result, recipe.getResult())) {
                return recipe;
            }
        }

        return null;
    }
}
