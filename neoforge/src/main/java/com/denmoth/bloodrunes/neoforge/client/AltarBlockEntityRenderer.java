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
        public final ItemStackRenderState[] itemStates = new ItemStackRenderState[10];
        public final boolean[] hasItem = new boolean[10];
        
        public AltarRenderState() {
            for (int i = 0; i < 10; i++) {
                itemStates[i] = new ItemStackRenderState();
            }
        }
        public long gameTime;
        public float partialTick;
        public boolean ritualActive;
        public int ticksActive;
        public int duration;
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
        for (int i = 0; i < 10; i++) {
            ItemStack st = blockEntity.getItems().get(i);
            state.hasItem[i] = !st.isEmpty();
            if (state.hasItem[i]) {
                this.itemModelResolver.updateForTopItem(state.itemStates[i], st, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
            }
        }
        state.gameTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0;
        state.partialTick = partialTick;
        state.ritualActive = blockEntity.isRitualActive();
        state.ticksActive = blockEntity.getTicksActive();
        state.duration = blockEntity.getDuration();
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
            
            float progress = 0.0f;
            if (state.ritualActive && state.duration > 0) {
                progress = Math.min(1.0f, (state.ticksActive + state.partialTick) / (float) state.duration);
            }
            // Starts at 0.45 radius, collapses to 0 by progress = 1.0
            // Rotation speed increases based on progress
            double radius = 0.45D * (1.0f - progress * progress); // Accelerate collapse near the end
            float spinSpeed = 1.5F + progress * 10.0F;
            float rotationOffset = renderTime * spinSpeed;
            
            int idx = 0;
            for (int i = 1; i < 9; i++) {
                if (state.hasItem[i] && !state.itemStates[i].isEmpty()) {
                    poseStack.pushPose();
                    float baseAngle = rotationOffset + (idx * angleStep);
                    double rad = Math.toRadians(baseAngle);
                    double cx = 0.5D + Math.cos(rad) * radius;
                    double cz = 0.5D + Math.sin(rad) * radius;
                    double cy = 1.35D + Math.sin(renderTime * 0.06 + idx * 0.8) * 0.15D;
                    
                    // As items collapse, they move down to meet the center item
                    cy = cy * (1.0 - progress) + (1.25D) * progress;
                    
                    poseStack.translate(cx, cy, cz);
                    poseStack.mulPose(Axis.YP.rotationDegrees(renderTime * (3.0F + progress * 5.0F)));
                    
                    // Scale down as they collapse
                    float scale = 0.45f * (1.0f - progress * 0.5f);
                    poseStack.scale(scale, scale, scale);
                    
                    state.itemStates[i].submit(poseStack, collector, 15728880, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 0);
                    poseStack.popPose();
                    idx++;
                }
            }
        }
        
        // Slot 9 item (Rune during Ristublot)
        if (state.hasItem[9] && !state.itemStates[9].isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5D, 1.6D + Math.sin(renderTime * 0.1) * 0.1D, 0.5D);
            poseStack.mulPose(Axis.YP.rotationDegrees(-renderTime * 3.0F));
            
            float progress = 0.0f;
            if (state.ritualActive && state.duration > 0) {
                progress = Math.min(1.0f, (state.ticksActive + state.partialTick) / (float) state.duration);
            }
            float scale = 0.5f * (1.0f - progress * 0.3f);
            poseStack.scale(scale, scale, scale);
            
            state.itemStates[9].submit(poseStack, collector, 15728880, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
