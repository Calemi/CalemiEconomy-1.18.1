package com.tm.calemieconomy.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.render.RenderedFloatingItemStack;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTradingPost implements BlockEntityRenderer<BlockEntityTradingPost> {

    private final RenderedFloatingItemStack renderedItemStack = new RenderedFloatingItemStack();

    public RenderTradingPost(BlockEntityRendererProvider.Context pContext) {}

    @Override
    public void render(BlockEntityTradingPost post, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        renderedItemStack.setStack(post.getStackForSale());

        if (!post.getStackForSale().isEmpty()) {

            renderedItemStack.updateSpinningAndFloating();

            poseStack.pushPose();
            poseStack.translate(0.5D, 0.5D, 0.5D);

            if (post.getStackForSale().getItem() instanceof BlockItem) {
                poseStack.translate(0.0D, -0.1D, 0.0D);
            }

            renderedItemStack.applyRotations(poseStack);
            renderedItemStack.render(poseStack, buffer, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }
}
