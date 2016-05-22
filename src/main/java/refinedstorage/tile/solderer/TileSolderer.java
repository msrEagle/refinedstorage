package refinedstorage.tile.solderer;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerSolderer;
import refinedstorage.inventory.SimpleItemHandler;
import refinedstorage.inventory.SimpleItemValidator;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.TileMachine;

public class TileSolderer extends TileMachine {
    public static final String NBT_WORKING = "Working";
    public static final String NBT_PROGRESS = "Progress";

    private SimpleItemHandler items = new SimpleItemHandler(4, this);
    private SimpleItemHandler upgrades = new SimpleItemHandler(4, this, new SimpleItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_SPEED));

    private ISoldererRecipe recipe;

    private boolean working = false;
    private int progress = 0;
    private int duration;

    @Override
    public int getEnergyUsage() {
        return 3;
    }

    @Override
    public void updateMachine() {
        ISoldererRecipe newRecipe = SoldererRegistry.getRecipe(items);

        boolean wasWorking = working;

        if (newRecipe == null) {
            reset();
        } else if (newRecipe != recipe) {
            boolean isSameItem = items.getStackInSlot(3) != null ? RefinedStorageUtils.compareStackNoQuantity(items.getStackInSlot(3), newRecipe.getResult()) : false;

            if (items.getStackInSlot(3) == null || (isSameItem && ((items.getStackInSlot(3).stackSize + newRecipe.getResult().stackSize) <= items.getStackInSlot(3).getMaxStackSize()))) {
                recipe = newRecipe;
                progress = 0;
                working = true;

                markDirty();
            }
        } else if (working) {
            progress += 1 + RefinedStorageUtils.getUpgradeCount(upgrades, ItemUpgrade.TYPE_SPEED);

            if (progress >= recipe.getDuration()) {
                if (items.getStackInSlot(3) != null) {
                    items.getStackInSlot(3).stackSize += recipe.getResult().stackSize;
                } else {
                    items.setStackInSlot(3, recipe.getResult());
                }

                for (int i = 0; i < 3; ++i) {
                    if (recipe.getRow(i) != null) {
                        items.extractItem(i, recipe.getRow(i).stackSize, false);
                    }
                }

                reset();
            }
        }

        if (wasWorking != working) {
            RefinedStorageUtils.updateBlock(worldObj, pos);
        }
    }

    @Override
    public void onDisconnected(World world) {
        super.onDisconnected(world);

        reset();
    }

    public void reset() {
        progress = 0;
        working = false;
        recipe = null;

        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        RefinedStorageUtils.restoreItems(items, 0, nbt);
        RefinedStorageUtils.restoreItems(upgrades, 1, nbt);

        recipe = SoldererRegistry.getRecipe(items);

        if (nbt.hasKey(NBT_WORKING)) {
            working = nbt.getBoolean(NBT_WORKING);
        }

        if (nbt.hasKey(NBT_PROGRESS)) {
            progress = nbt.getInteger(NBT_PROGRESS);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        RefinedStorageUtils.saveItems(items, 0, nbt);
        RefinedStorageUtils.saveItems(upgrades, 1, nbt);

        nbt.setBoolean(NBT_WORKING, working);
        nbt.setInteger(NBT_PROGRESS, progress);
    }

    @Override
    public void writeToDescriptionPacketNBT(NBTTagCompound tag) {
        super.writeToDescriptionPacketNBT(tag);

        tag.setBoolean(NBT_WORKING, working);
    }

    @Override
    public void readFromDescriptionPacketNBT(NBTTagCompound tag) {
        super.readFromDescriptionPacketNBT(tag);

        working = tag.getBoolean(NBT_WORKING);
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        progress = buf.readInt();
        duration = buf.readInt();
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

        buf.writeInt(progress);
        buf.writeInt(recipe != null ? recipe.getDuration() : 0);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerSolderer.class;
    }

    public boolean isWorking() {
        return working;
    }

    public int getProgressScaled(int i) {
        if (progress > duration) {
            return i;
        }

        return (int) ((float) progress / (float) duration * (float) i);
    }

    public IItemHandler getItems() {
        return items;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDroppedItems() {
        return items;
    }
}
