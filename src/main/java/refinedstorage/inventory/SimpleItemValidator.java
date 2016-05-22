package refinedstorage.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SimpleItemValidator implements IItemValidator {
    private Item item;
    private int damage = -1;

    public SimpleItemValidator(Item item) {
        this.item = item;
    }

    public SimpleItemValidator(Item item, int damage) {
        this.item = item;
        this.damage = damage;
    }

    @Override
    public boolean valid(ItemStack stack) {
        if (stack.getItem() == item) {
            if (damage != -1 && stack.getItemDamage() != damage) {
                return false;
            }

            return true;
        }

        return false;
    }
}
