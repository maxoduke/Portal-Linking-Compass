package dev.maxoduke.mods.portallinkingcompass.client;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class PortalLinkingCompassAngle implements RangeSelectItemModelProperty
{
    public static final MapCodec<PortalLinkingCompassAngle> MAP_CODEC = PortalLinkingCompassAngleState.MAP_CODEC.xmap(
        PortalLinkingCompassAngle::new,
        angle -> angle.state
    );

    private final PortalLinkingCompassAngleState state;

    public PortalLinkingCompassAngle(PortalLinkingCompassAngleState state)
    {
        this.state = state;
    }

    @Override
    public float get(@NotNull ItemStack itemStack, ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int seed)
    {
        return state.calculate(itemStack, clientLevel, seed, livingEntity);
    }

    @Override
    public @NotNull MapCodec<PortalLinkingCompassAngle> type()
    {
        return MAP_CODEC;
    }
}
