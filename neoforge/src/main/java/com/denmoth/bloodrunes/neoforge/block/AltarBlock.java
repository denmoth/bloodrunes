package com.denmoth.bloodrunes.neoforge.block;

import com.denmoth.bloodrunes.neoforge.setup.ModBlockEntities;
import com.denmoth.bloodrunes.neoforge.setup.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AltarBlock extends BaseEntityBlock {
    public static final com.mojang.serialization.MapCodec<AltarBlock> CODEC = simpleCodec(AltarBlock::new);

    public AltarBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AltarBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof AltarBlockEntity altar) {
            boolean hasAnyItem = false;
            for (int i = 0; i < 9; i++) {
                if (!altar.getItems().get(i).isEmpty()) { hasAnyItem = true; break; }
            }
            if (hasAnyItem) {
                if (!level.isClientSide()) {
                    ItemStack extracted = ItemStack.EMPTY;
                    if (altar.isRitualActive()) {
                        // Punish the player for interrupting the ritual
                        if (player instanceof net.minecraft.server.level.ServerPlayer sp && level instanceof net.minecraft.server.level.ServerLevel sl) {
                            sl.getServer().getCommands().performPrefixedCommand(sp.createCommandSourceStack().withPosition(new net.minecraft.world.phys.Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)), "summon lightning_bolt");
                        }
                    }
                    
                    for (int i = 8; i >= 1; i--) {
                        if (!altar.getItems().get(i).isEmpty()) {
                            extracted = altar.getItems().get(i).copy();
                            altar.getItems().set(i, ItemStack.EMPTY);
                            altar.setChanged();
                            if (level instanceof net.minecraft.server.level.ServerLevel sl) sl.sendBlockUpdated(pos, state, state, 3);
                            break;
                        }
                    }
                    if (extracted.isEmpty() && !altar.getItems().get(0).isEmpty()) {
                        extracted = altar.getItems().get(0).copy();
                        altar.getItems().set(0, ItemStack.EMPTY);
                        altar.setChanged();
                        if (level instanceof net.minecraft.server.level.ServerLevel sl) sl.sendBlockUpdated(pos, state, state, 3);
                    }

                    if (!extracted.isEmpty()) {
                        if (altar.isRitualActive()) {
                            // Only cancel after extracting so we don't double punish, but we need to cancel it
                            // Actually it's better to cancel by manually calling a method, but since it's private,
                            // we'll let the tick loop catch the empty center and cancel itself, OR we just set a flag.
                            // The tick loop will cancel it automatically because the items don't match anymore.
                        }
                        if (!player.addItem(extracted)) {
                            player.drop(extracted, false);
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof AltarBlockEntity altar) {
            if (stack.isEmpty()) {
                boolean hasAnyItem = false;
                for (int i = 0; i < 9; i++) {
                    if (!altar.getItems().get(i).isEmpty()) { hasAnyItem = true; break; }
                }
                if (hasAnyItem) {
                    if (!level.isClientSide()) {
                        ItemStack extracted = ItemStack.EMPTY;
                        if (altar.isRitualActive()) {
                            // Punish the player for interrupting the ritual
                            if (player instanceof net.minecraft.server.level.ServerPlayer sp && level instanceof net.minecraft.server.level.ServerLevel sl) {
                                sl.getServer().getCommands().performPrefixedCommand(sp.createCommandSourceStack().withPosition(new net.minecraft.world.phys.Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)), "summon lightning_bolt");
                            }
                        }
                        
                        for (int i = 8; i >= 1; i--) {
                            if (!altar.getItems().get(i).isEmpty()) {
                                extracted = altar.getItems().get(i).copy();
                                altar.getItems().set(i, ItemStack.EMPTY);
                                altar.setChanged();
                                if (level instanceof net.minecraft.server.level.ServerLevel sl) sl.sendBlockUpdated(pos, state, state, 3);
                                break;
                            }
                        }
                        if (extracted.isEmpty() && !altar.getItems().get(0).isEmpty()) {
                            extracted = altar.getItems().get(0).copy();
                            altar.getItems().set(0, ItemStack.EMPTY);
                            altar.setChanged();
                            if (level instanceof net.minecraft.server.level.ServerLevel sl) sl.sendBlockUpdated(pos, state, state, 3);
                        }

                        if (!extracted.isEmpty()) {
                            if (!player.addItem(extracted)) {
                                player.drop(extracted, false);
                            }
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            }
            if (!level.isClientSide()) {
                if (altar.addItem(stack, player)) {
                    stack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            } else {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return createTickerHelper(blockEntityType, ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), AltarBlockEntity::clientTick);
        }
        return createTickerHelper(blockEntityType, ModBlockEntities.ALTAR_BLOCK_ENTITY.get(), AltarBlockEntity::serverTick);
    }
}
