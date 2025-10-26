package dev.maxoduke.mods.portallinkingcompass.client;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
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
    public float get(ItemStack itemStack, ClientLevel clientLevel, @Nullable ItemOwner itemOwner, int seed)
    {
        return state.calculate(itemStack, clientLevel, seed, itemOwner);
    }

    @Override
    public @NotNull MapCodec<PortalLinkingCompassAngle> type()
    {
        return MAP_CODEC;
    }
}
