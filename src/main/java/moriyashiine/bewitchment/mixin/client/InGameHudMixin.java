/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.bewitchment.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.api.component.BloodComponent;
import moriyashiine.bewitchment.api.component.MagicComponent;
import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.registry.BWComponents;
import moriyashiine.bewitchment.common.registry.BWTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
	@Unique
	private static final Identifier BEWITCHMENT_GUI_ICONS_TEXTURE = new Identifier(Bewitchment.MODID, "textures/gui/icons.png");
	@Unique
	private static final Identifier EMPTY_TEXTURE = new Identifier(Bewitchment.MODID, "textures/gui/empty.png");

	@Shadow
	protected abstract PlayerEntity getCameraPlayer();

	@Shadow
	private int scaledHeight;

	@Shadow
	private int scaledWidth;

	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 2, target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;"))
	private void renderPre(MatrixStack matrices, CallbackInfo callbackInfo) {
		PlayerEntity player = getCameraPlayer();
		BWComponents.MAGIC_COMPONENT.maybeGet(player).ifPresent(magicComponent -> {
			if (magicComponent.getMagicTimer() > 0) {
				RenderSystem.setShaderTexture(0, BEWITCHMENT_GUI_ICONS_TEXTURE);
				RenderSystem.setShaderColor(1, 1, 1, magicComponent.getMagicTimer() / 10f);
				drawTexture(matrices, 13, (scaledHeight - 74) / 2, 25, 0, 7, 74);
				drawTexture(matrices, 13, (scaledHeight - 74) / 2, 32, 0, 7, (int) (74 - (magicComponent.getMagic() * 74f / MagicComponent.MAX_MAGIC)));
				drawTexture(matrices, 4, (scaledHeight - 102) / 2, 0, 0, 25, 102);
				RenderSystem.setShaderColor(1, 1, 1, 1);
				RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
			}
		});
		if (BewitchmentAPI.isVampire(player, true)) {
			RenderSystem.setShaderTexture(0, BEWITCHMENT_GUI_ICONS_TEXTURE);
			drawBlood(matrices, player, scaledWidth / 2 + 82, scaledHeight - 39, 10);
			if (player.isInSneakingPose() && client.targetedEntity instanceof LivingEntity livingEntity && client.targetedEntity.getType().isIn(BWTags.HAS_BLOOD)) {
				drawBlood(matrices, livingEntity, scaledWidth / 2 + 13, scaledHeight / 2 + 9, 5);
			}
			RenderSystem.setShaderTexture(0, EMPTY_TEXTURE);
		}
	}

	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 3, target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;"))
	private void renderPost(MatrixStack matrices, CallbackInfo callbackInfo) {
		PlayerEntity player = getCameraPlayer();
		if (BewitchmentAPI.isVampire(player, true)) {
			RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
		}
	}

	private void drawBlood(MatrixStack matrices, LivingEntity entity, int x, int y, int droplets) {
		BWComponents.BLOOD_COMPONENT.maybeGet(entity).ifPresent(bloodComponent -> {
			int v = entity.hasStatusEffect(StatusEffects.HUNGER) ? 9 : 0;
			float blood = ((float) bloodComponent.getBlood() / BloodComponent.MAX_BLOOD * droplets);
			int full = (int) blood;
			for (int i = 0; i < full; i++) {
				drawTexture(matrices, x - i * 8, y, 39, v, 9, 9);
			}
			if (full < droplets) {
				float remaining = blood - full;
				drawTexture(matrices, x - full * 8, y, remaining > 5 / 6f ? 48 : remaining > 4 / 6f ? 57 : remaining > 3 / 6f ? 66 : remaining > 2 / 6f ? 75 : remaining > 1 / 6f ? 84 : remaining > 0 ? 93 : 102, v, 9, 9);
			}
			for (int i = (full + 1); i < droplets; i++) {
				drawTexture(matrices, x - i * 8, y, 102, v, 9, 9);
			}
		});
	}
}
