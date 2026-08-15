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
        public final ItemStackRenderState[] itemStates = new ItemStackRenderState[9];
        public final boolean[] hasItem = new boolean[9];
        
        public AltarRenderState() {
            for (int i = 0; i < 9; i++) {
                itemStates[i] = new ItemStackRenderState();
            }
        }
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
        for (int i = 0; i < 9; i++) {
            ItemStack st = blockEntity.getItems().get(i);
            state.hasItem[i] = !st.isEmpty();
            if (state.hasItem[i]) {
                this.itemModelResolver.updateForTopItem(state.itemStates[i], st, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
            }
        }
        state.gameTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0;
        state.partialTick = partialTick;
    }

    @Override
    public void submit(AltarRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        float renderTime = state.gameTime + state.partialTick;
        
        // Center item
        if (state.hasItem[0] && !state.itemStates[0].isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5D, 1.25D + Math.sin(renderTime * 0.08) * 0.15D, 0.5D);
            poseStack.mulPose(Axis.YP.rotationDegrees(renderTime * 2.0F));
            poseStack.scale(0.75f, 0.75f, 0.75f);
            state.itemStates[0].submit(poseStack, collector, 15728880, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
        
        // Circle items
        int count = 0;
        for (int i = 1; i < 9; i++) {
            if (state.hasItem[i]) count++;
        }
        if (count > 0) {
            float angleStep = 360.0f / count;
            int idx = 0;
            for (int i = 1; i < 9; i++) {
                if (state.hasItem[i] && !state.itemStates[i].isEmpty()) {
                    poseStack.pushPose();
                    float baseAngle = renderTime * 1.5F + (idx * angleStep);
                    double rad = Math.toRadians(baseAngle);
                    double cx = 0.5D + Math.cos(rad) * 0.45D;
                    double cz = 0.5D + Math.sin(rad) * 0.45D;
                    double cy = 1.35D + Math.sin(renderTime * 0.06 + idx * 0.8) * 0.15D;
                    
                    poseStack.translate(cx, cy, cz);
                    poseStack.mulPose(Axis.YP.rotationDegrees(renderTime * 3.0F));
                    poseStack.scale(0.45f, 0.45f, 0.45f);
                    state.itemStates[i].submit(poseStack, collector, 15728880, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 0);
                    poseStack.popPose();
                    idx++;
                }
            }
        }
    }
}
