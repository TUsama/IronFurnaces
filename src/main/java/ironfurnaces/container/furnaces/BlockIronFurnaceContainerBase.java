package ironfurnaces.container.furnaces;

import ironfurnaces.container.slots.*;
import ironfurnaces.adaptor.energy.FEnergyStorage;
import ironfurnaces.items.ItemHeater;
import ironfurnaces.items.augments.ItemAugmentBlasting;
import ironfurnaces.items.augments.ItemAugmentSmoking;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.tileentity.furnaces.UnifiedTileEntity;
import ironfurnaces.util.container.FactoryDataSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
//? 1.20.1 {
import net.minecraftforge.common.capabilities.ForgeCapabilities;
//? } else {

//?}



public class BlockIronFurnaceContainerBase extends AbstractContainerMenu {

    protected BlockIronFurnaceTileBase te;
    protected Player playerEntity;
    protected IItemHandler playerInventory;
    protected final Level world;

    public BlockIronFurnaceContainerBase(MenuType<?> containerType, int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(containerType, windowId);
        
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.world = playerInventory.player.level();
        this.te = ((UnifiedTileEntity) world.getBlockEntity(pos));

        //FURNACE
        this.addSlot(new SlotIronFurnaceInput(te, 0, 56, 17));
        this.addSlot(new SlotIronFurnaceFuel(te, 1, 56, 53));
        this.addSlot(new SlotIronFurnace(playerEntity, te, 2, 116, 35));
        this.addSlot(new SlotIronFurnaceAugmentRed(te, 3, 26, 35));
        this.addSlot(new SlotIronFurnaceAugmentGreen(te, 4, 80, 35));
        this.addSlot(new SlotIronFurnaceAugmentBlue(te, 5, 134, 35));

        //GENERATOR
        this.addSlot(new SlotIronFurnaceInputGenerator(te, 6, 56, 40));

        //FACTORY
        this.addSlot(new SlotIronFurnaceInputFactory(0, te, 7, 28, 6));
        this.addSlot(new SlotIronFurnaceInputFactory(1, te, 8, 49, 6));
        this.addSlot(new SlotIronFurnaceInputFactory(2, te, 9, 70, 6));
        this.addSlot(new SlotIronFurnaceInputFactory(3, te, 10, 91, 6));
        this.addSlot(new SlotIronFurnaceInputFactory(4, te, 11, 112, 6));
        this.addSlot(new SlotIronFurnaceInputFactory(5, te, 12, 133, 6));
        this.addSlot(new SlotIronFurnaceOutputFactory(0, playerEntity, te, 13, 28, 55));
        this.addSlot(new SlotIronFurnaceOutputFactory(1, playerEntity, te, 14, 49, 55));
        this.addSlot(new SlotIronFurnaceOutputFactory(2, playerEntity, te, 15, 70, 55));
        this.addSlot(new SlotIronFurnaceOutputFactory(3, playerEntity, te, 16, 91, 55));
        this.addSlot(new SlotIronFurnaceOutputFactory(4, playerEntity, te,17, 112, 55));
        this.addSlot(new SlotIronFurnaceOutputFactory(5, playerEntity, te, 18, 133, 55));
        layoutPlayerInventorySlots(8, 84);
        checkContainerSize(this.te, 19);
        addDataSlots();
    }

    public void addDataSlots()
    {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getAugmentGUI() ? 1 : 0;
            }

            @Override
            public void set(int value) {
                te.furnaceSettings.set(10, value);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getIsFurnace() ? 1 : 0;
            }

            @Override
            public void set(int value) {
                if (value == 1)
                {
                    te.currentAugment[2] = 0;
                }
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getIsGenerator() ? 1 : 0;
            }

            @Override
            public void set(int value) {
                if (value == 1)
                {
                    te.currentAugment[2] = 2;
                }
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getIsFactory() ? 1 : 0;
            }

            @Override
            public void set(int value) {
                if (value == 1)
                {
                    te.currentAugment[2] = 1;
                }
            }
        });
        addEnergyData();
        addFurnaceData();
        addGeneratorData();
        addFactoryData();

    }

    public void addFurnaceData()
    {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return te.furnaceBurnTime & 0xffff;
            }

            @Override
            public void set(int value) {
                int add = te.furnaceBurnTime & 0xffff0000;
                te.furnaceBurnTime = add + (value & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (te.furnaceBurnTime >> 16) & 0xffff;
            }

            @Override
            public void set(int value) {
                int add = te.furnaceBurnTime & 0x0000ffff;
                te.furnaceBurnTime = add | (value << 16);
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return te.recipesUsed & 0xffff;
            }

            @Override
            public void set(int value) {
                int add = te.recipesUsed & 0xffff0000;
                te.recipesUsed = add + (value & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (te.recipesUsed >> 16) & 0xffff;
            }

            @Override
            public void set(int value) {
                int add = te.recipesUsed & 0x0000ffff;
                te.recipesUsed = add | (value << 16);
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return te.cookTime & 0xffff;
            }

            @Override
            public void set(int value) {
                int add = te.cookTime & 0xffff0000;
                te.cookTime = add + (value & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (te.cookTime >> 16) & 0xffff;
            }

            @Override
            public void set(int value) {
                int add = te.cookTime & 0x0000ffff;
                te.cookTime = add | (value << 16);
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return te.totalCookTime & 0xffff;
            }

            @Override
            public void set(int value) {
                int add = te.totalCookTime & 0xffff0000;
                te.totalCookTime = add + (value & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (te.totalCookTime >> 16) & 0xffff;
            }

            @Override
            public void set(int value) {
                int add = te.totalCookTime & 0x0000ffff;
                te.totalCookTime = add | (value << 16);
            }
        });
    }

    public void addGeneratorData()
    {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (int)te.generatorBurn & 0xffff;
            }

            @Override
            public void set(int value) {
                int add = (int)te.generatorBurn & 0xffff0000;
                te.generatorBurn = add + (value & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return ((int)te.generatorBurn >> 16) & 0xffff;
            }

            @Override
            public void set(int value) {
                int add = (int)te.generatorBurn & 0x0000ffff;
                te.generatorBurn = add | (value << 16);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return te.generatorRecentRecipeRF & 0xffff;
            }

            @Override
            public void set(int value) {
                int add = te.generatorRecentRecipeRF & 0xffff0000;
                te.generatorRecentRecipeRF = add + (value & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (te.generatorRecentRecipeRF >> 16) & 0xffff;
            }

            @Override
            public void set(int value) {
                int add = te.generatorRecentRecipeRF & 0x0000ffff;
                te.generatorRecentRecipeRF = add | (value << 16);
            }
        });
    }

    public void addFactoryData()
    {
        for (int i = 0; i < te.factoryCookTime.length; i++)
        {
            addDataSlot(new FactoryDataSlot(i) {
                @Override
                public int get() {
                    return te.factoryCookTime[index] & 0xffff;
                }

                @Override
                public void set(int value) {
                    int add = te.factoryCookTime[index] & 0xffff0000;
                    te.factoryCookTime[index] = add + (value & 0xffff);
                }
            });
            addDataSlot(new FactoryDataSlot(i) {
                @Override
                public int get() {
                    return (te.factoryCookTime[index] >> 16) & 0xffff;
                }

                @Override
                public void set(int value) {
                    int add = te.factoryCookTime[index] & 0x0000ffff;
                    te.factoryCookTime[index] = add | (value << 16);
                }
            });
        }

        for (int i = 0; i < te.factoryTotalCookTime.length; i++)
        {
            addDataSlot(new FactoryDataSlot(i) {
                @Override
                public int get() {
                    return te.factoryTotalCookTime[index] & 0xffff;
                }

                @Override
                public void set(int value) {
                    int add = te.factoryTotalCookTime[index] & 0xffff0000;
                    te.factoryTotalCookTime[index] = add + (value & 0xffff);
                }
            });
            addDataSlot(new FactoryDataSlot(i) {
                @Override
                public int get() {
                    return (te.factoryTotalCookTime[index] >> 16) & 0xffff;
                }

                @Override
                public void set(int value) {
                    int add = te.factoryTotalCookTime[index] & 0x0000ffff;
                    te.factoryTotalCookTime[index] = add | (value << 16);
                }
            });
        }
    }

    public int getEnergy() {
        return te.energyStorage.getEnergyStored();
    }

    public int getMaxEnergy() {
        return te.energyStorage.getMaxEnergyStored();
    }

    private void addEnergyData() {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getMaxEnergy();
            }

            @Override
            public void set(int value) {
                te.energyStorage.setCapacity(value);
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy();
            }

            @Override
            public void set(int value) {
                te.energyStorage.receiveEnergy(value - te.energyStorage.getEnergyStored(), false);
            }
        });
    }

    public boolean stillValid(Player player) {
        return this.te.stillValid(player);
    }

    public int getTier()
    {
        return te.getTier();

    }


    public boolean isAutoSplit()
    {
        return te.isAutoSplit();
    }



    public int getRedstoneMode() {
        return this.te.getRedstoneSetting();
    }


    public int getComSub() {
        return this.te.getRedstoneComSub();
    }


    public boolean getAutoInput() {
        return this.te.getAutoInput() == 1;
    }


    public boolean getAugmentGUI() {
        return this.te.getAugmentGUI() == 1;
    }


    public boolean getIsFactory() {
        return this.te.isFactory();
    }


    public boolean getIsFurnace() {
        return this.te.isFurnace();
    }
    
    public boolean getIsGenerator() {
        return this.te.isGenerator();
    }
    
    public boolean getAutoOutput() {
        return this.te.getAutoOutput() == 1;
    }
    
    public Component getTooltip(int index) {
        switch (te.furnaceSettings.get(index))
        {
            case 1:
                return Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_input");
            case 2:
                return Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_output");
            case 3:
                return Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_input_output");
            case 4:
                return Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_fuel");
            default:
                return Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_none");
        }
    }


    public int getSettingTop()
    {
        return this.te.getSettingTop();
    }


    public int getSettingBottom()
    {
        return this.te.getSettingBottom();
    }


    public int getSettingFront()
    {
        return this.te.getSettingFront();
    }


    public int getSettingBack()
    {
        return this.te.getSettingBack();
    }


    public int getSettingLeft()
    {
        return this.te.getSettingLeft();
    }


    public int getSettingRight()
    {
        return this.te.getSettingRight();
    }


    public int getIndexFront()
    {
        return this.te.getIndexFront();
    }


    public int getIndexBack()
    {
        return this.te.getIndexBack();
    }


    public int getIndexLeft()
    {
        return this.te.getIndexLeft();
    }


    public int getIndexRight()
    {
        return this.te.getIndexRight();
    }


    public BlockPos getPos() {
        return this.te.getBlockPos();
    }


    public boolean isBurning() {
        return this.te.isBurning();
    }

    public boolean isRainbowFurnace() {
        return this.te.isRainbowFurnace();
    }


    public int getCookScaled(int pixels) {
        int i = this.te.cookTime;
        int j = this.te.totalCookTime;
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }


    public int getFactoryCookScaled(int index, int pixels) {
        int i = this.te.factoryCookTime[index];
        int j = this.te.factoryTotalCookTime[index];
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }


    public int getFactoryCooktimeSize()
    {
        return this.te.factoryCookTime.length;
    }


    public int getBurnLeftScaled(int pixels) {
        int i = this.te.recipesUsed;
        if (i == 0) {
            i = 200;
        }

        return this.te.furnaceBurnTime * pixels / i;
    }


    public int getGeneratorBurnScaled(int pixels) {
        int i = this.te.generatorRecentRecipeRF;
        if (i == 0) {
            i = 200;
        }
        return (int)this.te.generatorBurn * pixels / i;
    }


    public boolean isGeneratorBurning()
    {
        return te.generatorBurn > 0;
    }


    public int getEnergyScaled(int pixels)
    {
        int i = this.te.getEnergy();
        int j = this.te.getCapacity();
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        boolean moved = false;

        if (te.isGenerator()) {
            moved = handleGenerator(index, stack);
        } else if (te.isFactory()) {
            moved = handleFactory(index, stack, slot, copy);
        } else if (te.isFurnace()) {
            moved = handleFurnace(index, stack, slot, copy);
        }

        if (!moved) return ItemStack.EMPTY;

        finalizeSlot(player, slot, stack, copy);
        return copy;
    }
    private boolean handleGenerator(int index, ItemStack stack) {

        if (isGeneratorInternalSlot(index)) {
            return moveTo(stack, 19, 55);
        }

        if (tryMoveGeneratorInput(stack)) {
            return true;
        }

        if (handleAugment(stack)) {
            return true;
        }

        return moveBetweenPlayerSections(index, stack);
    }

    private boolean tryMoveGeneratorInput(ItemStack stack) {

        ItemStack augment = te.getItem(3);

        // Smoking
        if (augment.getItem() instanceof ItemAugmentSmoking) {
            if (te.getSmokingBurn(stack) <= 0) return false;
            if (stack.hasCraftingRemainingItem() && stack.getCount() > 1){
                ItemStack singleItem = stack.copyWithCount(1);
                if (moveTo(singleItem, 6, 7)){
                    stack.shrink(1);
                    return true;
                }
                return false;
            }
            return moveTo(stack, 6, 7);
        }

        // Blasting
        if (augment.getItem() instanceof ItemAugmentBlasting) {
            return te.hasGeneratorBlastingRecipe(stack)
                    && moveTo(stack, 6, 7);
        }

        // Default fuel
        return BlockIronFurnaceTileBase.isItemFuel(stack, RecipeType.SMELTING)
                && !(stack.getItem() instanceof ItemHeater)
                && moveTo(stack, 6, 7);
    }

    private boolean isGeneratorInternalSlot(int index) {
        return index == 3 || index == 4 || index == 5 || index == 6;
    }

    private boolean handleFactory(int index, ItemStack stack, Slot slot, ItemStack original) {

        if (isFactoryOutputSlot(index)) {
            if (!moveToPlayer(stack, true)) {
                return false;
            }

            slot.onQuickCraft(stack, original);
            return true;
        }

        if (isPlayerInventory(index)) {

            if (tryMoveFactoryInput(stack)) return true;

            if (handleAugment(stack)) return true;

            return moveBetweenPlayerSections(index, stack);
        }

        return moveToPlayer(stack, false);
    }

    private boolean isFactoryOutputSlot(int index) {
        return index > 12 && index <= 18;
    }

    private boolean isPlayerInventory(int index) {
        return index >= 19;
    }

    private boolean tryMoveFactoryInput(ItemStack stack) {

        if (!te.hasRecipe(stack)) {
            return false;
        }

        int tier = getTier();

        if (tier == 2) {
            return moveTo(stack, 7, 13);
        }
        if (tier == 1) {
            return moveTo(stack, 8, 12);
        }

        return moveTo(stack, 9, 11);
    }

    private boolean handleFurnace(int index, ItemStack stack, Slot slot, ItemStack original) {

        if (index == 2) {
            if (!moveToPlayer(stack, true)) {
                return false;
            }

            slot.onQuickCraft(stack, original);
            return true;
        }

        if (isPlayerInventory(index)) {

            if (tryMoveFurnaceInput(stack)) return true;

            if (tryMoveFurnaceFuel(stack)) return true;

            if (handleAugment(stack)) return true;

            return moveBetweenPlayerSections(index, stack);
        }

        return moveToPlayer(stack, false);
    }


    private boolean tryMoveFurnaceInput(ItemStack stack) {
        if (te.hasRecipe(stack)) {
            return moveTo(stack, 0, 1);
        }
        return false;
    }

    private boolean tryMoveFurnaceFuel(ItemStack stack) {
        if (BlockIronFurnaceTileBase.isItemFuel(stack, RecipeType.SMELTING)) {
            return moveTo(stack, 1, 2);
        }
        return false;
    }




    private boolean moveTo(ItemStack stack, int start, int end) {
        return this.moveItemStackTo(stack, start, end, false);
    }

    private boolean moveToPlayer(ItemStack stack, boolean reverse) {
        return this.moveItemStackTo(stack, 19, 55, reverse);
    }

    private boolean handleAugment(ItemStack stack) {
        for (int i = 0; i < 3; i++) {
            if (BlockIronFurnaceTileBase.isItemAugment(stack, i)) {
                return moveTo(stack, 3 + i, 4 + i);
            }
        }
        return false;
    }


    private boolean moveBetweenPlayerSections(int index, ItemStack stack) {
        if (index >= 19 && index <= 45)
            return moveTo(stack, 46, 55);
        if (index >= 45 && index < 55)
            return moveTo(stack, 19, 46);
        return false;
    }

    private ItemStack finalizeSlot(Player player, Slot slot, ItemStack stack, ItemStack copy) {
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        return copy;
    }
/*
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (te.isGenerator())
            {
                if (index != 6 && index != 3 && index != 4 && index != 5)
                {
                    if (te.getItem(3).getItem() instanceof ItemAugmentSmoking)
                    {
                        if (te.getSmokingBurn(itemstack1) > 0) {
                            itemstack1.hasCraftingRemainingItem()
                            if (!this.moveItemStackTo(itemstack1, 6, 7, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                    else if (te.getItem(3).getItem() instanceof ItemAugmentBlasting)
                    {
                        if (te.hasGeneratorBlastingRecipe(itemstack1)) {
                            if (!this.moveItemStackTo(itemstack1, 6, 7, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                    else
                    {
                        if (BlockIronFurnaceTileBase.isItemFuel(itemstack1, RecipeType.SMELTING) && !(itemstack1.getItem() instanceof ItemHeater)) {
                            if (!this.moveItemStackTo(itemstack1, 6, 7, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                    if (BlockIronFurnaceTileBase.isItemAugment(itemstack1, 0)) {
                        if (!this.moveItemStackTo(itemstack1, 3, 4, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (BlockIronFurnaceTileBase.isItemAugment(itemstack1, 1)) {
                        if (!this.moveItemStackTo(itemstack1, 4, 5, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (BlockIronFurnaceTileBase.isItemAugment(itemstack1, 2)) {
                        if (!this.moveItemStackTo(itemstack1, 5, 6, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 19 && index <= 45) {
                        if (!this.moveItemStackTo(itemstack1, 46, 55, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 45 && index < 55 && !this.moveItemStackTo(itemstack1, 19, 46, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 19, 55, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (te.isFactory()) {
                if (index > 12 && index <= 18) {
                    if (!this.moveItemStackTo(itemstack1, 19, 55, true)) {
                        return ItemStack.EMPTY;
                    }

                    slot.onQuickCraft(itemstack1, itemstack);
                } else if (index >= 19) {
                    if (this.te.hasRecipe(itemstack1)) {
                        if (getTier() == 2)
                        {
                            if (!this.moveItemStackTo(itemstack1, 7, 13, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                        else if (getTier() == 1)
                        {
                            if (!this.moveItemStackTo(itemstack1, 8, 12, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                        else
                        {
                            if (!this.moveItemStackTo(itemstack1, 9, 11, false)) {
                                return ItemStack.EMPTY;
                            }
                        }

                    } else if (BlockIronFurnaceTileBase.isItemAugment(itemstack1, 0)) {
                        if (!this.moveItemStackTo(itemstack1, 3, 4, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (BlockIronFurnaceTileBase.isItemAugment(itemstack1, 1)) {
                        if (!this.moveItemStackTo(itemstack1, 4, 5, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (BlockIronFurnaceTileBase.isItemAugment(itemstack1, 2)) {
                        if (!this.moveItemStackTo(itemstack1, 5, 6, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 19 && index <= 45) {
                        if (!this.moveItemStackTo(itemstack1, 46, 55, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 45 && index < 55 && !this.moveItemStackTo(itemstack1, 19, 46, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 19, 55, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (te.isFurnace())
            {

                if (index == 2) {
                    if (!this.moveItemStackTo(itemstack1, 19, 55, true)) {
                        return ItemStack.EMPTY;
                    }

                    slot.onQuickCraft(itemstack1, itemstack);
                } else if (index != 1 && index != 0 && index != 3 && index != 4 && index != 5) {
                    if (this.te.hasRecipe(itemstack1)) {
                        if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (BlockIronFurnaceTileBase.isItemFuel(itemstack1, RecipeType.SMELTING)) {
                        if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (BlockIronFurnaceTileBase.isItemAugment(itemstack1, 0)) {
                        if (!this.moveItemStackTo(itemstack1, 3, 4, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (BlockIronFurnaceTileBase.isItemAugment(itemstack1, 1)) {
                        if (!this.moveItemStackTo(itemstack1, 4, 5, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (BlockIronFurnaceTileBase.isItemAugment(itemstack1, 2)) {
                        if (!this.moveItemStackTo(itemstack1, 5, 6, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 19 && index <= 45) {
                        if (!this.moveItemStackTo(itemstack1, 46, 55, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 45 && index < 55 && !this.moveItemStackTo(itemstack1, 19, 46, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 19, 55, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);

        }


        return itemstack;
    }
*/

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

}
