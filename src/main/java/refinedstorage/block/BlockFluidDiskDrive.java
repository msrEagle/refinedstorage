package refinedstorage.block;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageGui;
import refinedstorage.tile.TileFluidDiskDrive;

public class BlockFluidDiskDrive extends BlockNode {
    private static final PropertyInteger STORED = PropertyInteger.create("stored", 0, 7);

    public BlockFluidDiskDrive() {
        super("fluid_disk_drive");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileFluidDiskDrive();
    }

    @Override
    public BlockStateContainer createBlockState() {
        return createBlockStateBuilder()
            .add(STORED)
            .build();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getActualState(state, world, pos)
            .withProperty(STORED, Math.max(0, ((TileFluidDiskDrive) world.getTileEntity(pos)).getStoredForDisplay()));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.FLUID_DISK_DRIVE, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        ((TileFluidDiskDrive) world.getTileEntity(pos)).onBreak();

        super.breakBlock(world, pos, state);
    }
}