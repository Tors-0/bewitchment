/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.bewitchment.common.ritualfunction;

import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.api.registry.RitualFunction;
import moriyashiine.bewitchment.common.item.TaglockItem;
import moriyashiine.bewitchment.common.registry.BWComponents;
import moriyashiine.bewitchment.common.world.BWUniversalWorldState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;

import java.util.function.Predicate;

public class UnbindFamiliarRitualFunction extends RitualFunction {
	public UnbindFamiliarRitualFunction(ParticleType<?> startParticle, Predicate<LivingEntity> sacrifice) {
		super(startParticle, sacrifice);
	}

	@Override
	public String getInvalidMessage() {
		return "ritual.precondition.found_entity";
	}

	@Override
	public boolean isValid(ServerWorld world, BlockPos pos, Inventory inventory) {
		ItemStack taglock = null;
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (inventory.getStack(i).getItem() instanceof TaglockItem && TaglockItem.hasTaglock(stack)) {
				taglock = stack;
				break;
			}
		}
		return taglock != null && BewitchmentAPI.getTaglockOwner(world, taglock) != null;
	}

	@Override
	public void start(ServerWorld world, BlockPos glyphPos, BlockPos effectivePos, Inventory inventory, boolean catFamiliar) {
		boolean succeeded = false;
		ItemStack taglock = null;
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (inventory.getStack(i).getItem() instanceof TaglockItem) {
				taglock = stack;
				break;
			}
		}
		if (taglock != null) {
			LivingEntity livingEntity = BewitchmentAPI.getTaglockOwner(world, taglock);
			if (livingEntity != null) {
				PlayerEntity closestPlayer = world.getClosestPlayer(effectivePos.getX() + 0.5, effectivePos.getY() + 0.5, effectivePos.getZ() + 0.5, 8, false);
				if (closestPlayer != null && BewitchmentAPI.getFamiliar(closestPlayer) != null) {
					NbtCompound entityCompound = new NbtCompound();
					livingEntity.saveSelfNbt(entityCompound);
					if (entityCompound.contains("Owner") && closestPlayer.getUuid().equals(entityCompound.getUuid("Owner"))) {
						BWComponents.FAMILIAR_COMPONENT.get(livingEntity).setFamiliar(false);
						BWUniversalWorldState universalWorldState = BWUniversalWorldState.get(world);
						for (int i = universalWorldState.familiars.size() - 1; i >= 0; i--) {
							if (livingEntity.getUuid().equals(universalWorldState.familiars.get(i).getRight().getUuid("UUID"))) {
								universalWorldState.familiars.remove(i);
								universalWorldState.markDirty();
								succeeded = true;
							}
						}
					}
				}
			}
		}
		if (!succeeded) {
			ItemScatterer.spawn(world, glyphPos, inventory);
		}
		super.start(world, glyphPos, effectivePos, inventory, catFamiliar);
	}
}
