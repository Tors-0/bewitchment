package moriyashiine.bewitchment.common.block.dragonsblood;

import com.terraformersmc.terraform.wood.block.TerraformTrapdoorBlock;
import moriyashiine.bewitchment.api.interfaces.misc.SigilHolder;
import moriyashiine.bewitchment.common.block.entity.SigilBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DragonsBloodTrapdoorBlock extends TerraformTrapdoorBlock implements BlockEntityProvider {
	public DragonsBloodTrapdoorBlock(Settings settings) {
		super(settings);
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new SigilBlockEntity();
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ActionResult result = SigilHolder.onUse(world, pos, player, hand);
		if (result == ActionResult.FAIL) {
			return ActionResult.FAIL;
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}
}
