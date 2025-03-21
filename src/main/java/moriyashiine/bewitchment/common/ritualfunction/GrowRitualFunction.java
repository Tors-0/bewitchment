/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.bewitchment.common.ritualfunction;

import moriyashiine.bewitchment.api.registry.RitualFunction;
import moriyashiine.bewitchment.common.misc.BWUtil;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.GrassBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class GrowRitualFunction extends RitualFunction {
	public GrowRitualFunction(ParticleType<?> startParticle, Predicate<LivingEntity> sacrifice) {
		super(startParticle, sacrifice);
	}

	@Override
	public void tick(World world, BlockPos glyphPos, BlockPos effectivePos, boolean catFamiliar) {
		int radius = catFamiliar ? 9 : 3;
		if (!world.isClient) {
			if (world.getTime() % 20 == 0) {
				for (PassiveEntity passiveEntity : world.getEntitiesByClass(PassiveEntity.class, new Box(effectivePos).expand(radius, 0, radius), PassiveEntity::isBaby)) {
					if (world.random.nextFloat() < 1 / 4f) {
						passiveEntity.growUp(world.random.nextInt(), true);
					}
				}
				for (BlockPos foundPos : BWUtil.getBlockPoses(effectivePos, radius, currentPos -> !(world.getBlockState(currentPos).getBlock() instanceof GrassBlock) && world.getBlockState(currentPos).getBlock() instanceof Fertilizable fertilizable && fertilizable.isFertilizable(world, currentPos, world.getBlockState(currentPos), false) && fertilizable.canGrow(world, world.random, currentPos, world.getBlockState(currentPos)))) {
					((Fertilizable) world.getBlockState(foundPos).getBlock()).grow((ServerWorld) world, world.random, foundPos, world.getBlockState(foundPos));
				}
			}
		} else {
			world.addParticle(ParticleTypes.HAPPY_VILLAGER, effectivePos.getX() + MathHelper.nextDouble(world.random, -radius, radius), effectivePos.getY() + 0.5, effectivePos.getZ() + MathHelper.nextDouble(world.random, -radius, radius), 0, 0, 0);
		}
	}
}
