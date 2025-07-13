package dev.maxoduke.mods.portallinkingcompass.item;

import dev.maxoduke.mods.portallinkingcompass.PortalLinkingCompass;
import dev.maxoduke.mods.portallinkingcompass.item.component.LinkedPortalTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PortalLinkingCompassItem extends Item
{
    public PortalLinkingCompassItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel serverLevel, Entity entity, @Nullable EquipmentSlot equipmentSlot)
    {
        LinkedPortalTracker tracker = itemStack.get(PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT);
        if (tracker == null)
            return;

        LinkedPortalTracker newTracker = tracker.tick(serverLevel);
        if (tracker != newTracker)
            itemStack.set(PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT, newTracker);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    @NotNull
    public InteractionResult useOn(UseOnContext useOnContext)
    {
        BlockPos usedOnBlockPos = useOnContext.getClickedPos();
        Level level = useOnContext.getLevel();
        ItemStack heldItem = useOnContext.getItemInHand();
        Player player = useOnContext.getPlayer();

        if (!level.getBlockState(usedOnBlockPos).is(Blocks.NETHER_PORTAL))
            return super.useOn(useOnContext);

        LinkedPortalTracker tracker = new LinkedPortalTracker(usedOnBlockPos, level);
        if (!player.isCreative() && heldItem.getCount() == 1)
            heldItem.set(PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT, tracker);
        else
        {
            ItemStack newCompass = new ItemStack(PortalLinkingCompass.ITEM, 1);
            newCompass.set(PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT, tracker);

            if (!player.isCreative())
                heldItem.shrink(1);

            if (!player.getInventory().add(newCompass))
                player.drop(newCompass, false);
        }

        level.playSound(null, usedOnBlockPos, PortalLinkingCompass.COMPASS_LOCKS_SOUND_EVENT, SoundSource.PLAYERS, 1.0f, 1.0f);
        return InteractionResult.SUCCESS;
    }
}
