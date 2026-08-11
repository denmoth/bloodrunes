package com.denmoth.bloodrunes.neoforge.client;

import com.denmoth.bloodrunes.neoforge.block.AltarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class AltarBlockEntityRenderer implements BlockEntityRenderer<AltarBlockEntity, AltarBlockEntityRenderer.AltarRenderState> {
    private final ItemModelResolver itemModelResolver;

    public static class AltarRenderState extends BlockEntityRenderState {
        public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
        public boolean hasItem = false;
        public long gameTime;
        public float partialTick;
    }

    public AltarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public AltarRenderState createRenderState() {
        return new AltarRenderState();
    }

    @Override
    public void extractRenderState(AltarBlockEntity blockEntity, AltarRenderState state, float partialTick, net.minecraft.world.phys.Vec3 cameraPos, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);
        ItemStack itemStack = blockEntity.getRuneStack();
        state.hasItem = !itemStack.isEmpty();
        if (state.hasItem) {
            this.itemModelResolver.updateForTopItem(state.itemRenderState, itemStack, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
        }
        state.gameTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0;
        state.partialTick = partialTick;
    }

    @Override
    public void submit(AltarRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.hasItem && !state.itemRenderState.isEmpty()) {
            poseStack.pushPose();

            float renderTime = state.gameTime + state.partialTick;
            
            poseStack.translate(0.5D, 1.25D + Math.sin(renderTime * 0.1) * 0.1D, 0.5D);
            poseStack.mulPose(Axis.YP.rotationDegrees(renderTime * 4.0F));
            poseStack.scale(1.33f, 1.33f, 1.33f);

            state.itemRenderState.submit(poseStack, collector, 15728880, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 0);

            poseStack.popPose();
        }
    }
}
