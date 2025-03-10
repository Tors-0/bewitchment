/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.bewitchment.mixin.contract;

import moriyashiine.bewitchment.common.registry.BWComponents;
import moriyashiine.bewitchment.common.registry.BWContracts;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalMateGoal.class)
public abstract class AnimalMateGoalMixin extends Goal {
	@Shadow
	@Final
	protected AnimalEntity animal;

	@Shadow
	protected AnimalEntity mate;

	@Inject(method = "breed", at = @At("HEAD"))
	private void breed(CallbackInfo callbackInfo) {
		ServerPlayerEntity player = animal.getLovingPlayer();
		if (player != null && BWComponents.CONTRACTS_COMPONENT.get(player).hasContract(BWContracts.LUST)) {
			animal.breed(player.getWorld(), mate);
			animal.breed(player.getWorld(), mate);
		}
	}
}
