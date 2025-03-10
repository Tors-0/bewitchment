/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.bewitchment.common.statuseffect;

import moriyashiine.bewitchment.common.registry.BWStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;

public class SynchronizedStatusEffect extends StatusEffect {
	public SynchronizedStatusEffect(StatusEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		if (!entity.world.isClient && entity.age % 20 == 0) {
			entity.world.getEntitiesByClass(LivingEntity.class, entity.getBoundingBox().expand(3 * (amplifier + 1)), foundEntity -> foundEntity != entity).forEach(livingEntity -> entity.getStatusEffects().forEach(effect -> {
				if (effect.getEffectType() != this) {
					livingEntity.addStatusEffect(new StatusEffectInstance(effect.getEffectType(), effect.getDuration() / 2, effect.getAmplifier()));
				}
			}));
		}
	}

	@Override
	public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
		super.onApplied(entity, attributes, amplifier);
		entity.removeStatusEffect(BWStatusEffects.THEFT);
	}
}
