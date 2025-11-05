package dev.maxoduke.mods.portallinkingcompass.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.maxoduke.mods.portallinkingcompass.PortalLinkingCompass;
import dev.maxoduke.mods.portallinkingcompass.item.component.LinkedPortalTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.NeedleDirectionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class PortalLinkingCompassAngleState extends NeedleDirectionHelper
{
    public static final MapCodec<PortalLinkingCompassAngleState> MAP_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
                Codec.BOOL.optionalFieldOf("wobble", true).forGetter(o -> o.wobble()),
                PortalLinkingCompassAngleState.CompassTarget.CODEC.fieldOf("target").forGetter(angleState -> angleState.target)
            )
            .apply(instance, PortalLinkingCompassAngleState::new)
    );
    private final NeedleDirectionHelper.Wobbler wobbler;
    private final NeedleDirectionHelper.Wobbler noTargetWobbler;
    private final CompassTarget target;
    private final RandomSource random = RandomSource.create();

    protected PortalLinkingCompassAngleState(boolean wobble, CompassTarget target)
    {
        super(wobble);

        this.wobbler = super.newWobbler(0.8F);
        this.noTargetWobbler = super.newWobbler(0.8F);
        this.target = target;
    }

    @Override
    protected float calculate(@NotNull ItemStack itemStack, ClientLevel clientLevel, int seed, @Nullable ItemOwner itemOwner)
    {
        long gameTime = clientLevel.getGameTime();
        GlobalPos targetPos = this.target.get(clientLevel, itemStack, itemOwner);

        return !isValidCompassTargetPos(itemOwner, targetPos)
            ? this.getRandomlySpinningRotation(seed, gameTime)
            : this.getRotationTowardsCompassTarget(itemOwner, gameTime, targetPos.pos());
    }

    private float getRandomlySpinningRotation(int i, long l)
    {
        if (this.noTargetWobbler.shouldUpdate(l))
        {
            this.noTargetWobbler.update(l, this.random.nextFloat());
        }

        float f = this.noTargetWobbler.rotation() + (float) hash(i) / 2.1474836E9F;
        return Mth.positiveModulo(f, 1.0F);
    }

    private float getRotationTowardsCompassTarget(ItemOwner itemOwner, long gameTime, BlockPos blockPos)
    {
        float f = (float)getAngleFromEntityToPos(itemOwner, blockPos);
        float g = getWrappedVisualRotationY(itemOwner);
        LivingEntity livingEntity = itemOwner.asLivingEntity();
        float h;
        if (livingEntity instanceof Player player) {
            //noinspection resource
            if (player.isLocalPlayer() && player.level().tickRateManager().runsNormally()) {
                if (this.wobbler.shouldUpdate(gameTime)) {
                    this.wobbler.update(gameTime, 0.5F - (g - 0.25F));
                }

                h = f + this.wobbler.rotation();
                return Mth.positiveModulo(h, 1.0F);
            }
        }

        h = 0.5F - (g - 0.25F - f);
        return Mth.positiveModulo(h, 1.0F);
    }

    private static boolean isValidCompassTargetPos(@Nullable ItemOwner itemOwner, @Nullable GlobalPos globalPos)
    {
        return globalPos != null && itemOwner != null && !(globalPos.pos().distToCenterSqr(itemOwner.position()) < 1.0E-5F);
    }

    private static double getAngleFromEntityToPos(ItemOwner itemOwner, BlockPos blockPos)
    {
        Vec3 vec3 = Vec3.atCenterOf(blockPos);
        Vec3 vec32 = itemOwner.position();
        return Math.atan2(vec3.z() - vec32.z(), vec3.x() - vec32.x()) / (double) ((float) Math.PI * 2F);
    }

    private static float getWrappedVisualRotationY(ItemOwner itemOwner)
    {
        return Mth.positiveModulo(itemOwner.getVisualRotationYInDegrees() / 360.0F, 1.0F);
    }

    private static int hash(int i)
    {
        return i * 1327217883;
    }

    @OnlyIn(Dist.CLIENT)
    public enum CompassTarget implements StringRepresentable
    {
        NONE("none")
            {
                @Nullable
                @Override
                public GlobalPos get(ClientLevel clientLevel, ItemStack itemStack, @Nullable ItemOwner itemOwner)
                {
                    return null;
                }
            },

        PORTAL("portal")
            {
                @Nullable
                @Override
                public GlobalPos get(ClientLevel clientLevel, ItemStack itemStack, @Nullable ItemOwner itemOwner)
                {
                    LinkedPortalTracker linkedPortalTracker = itemStack.get(PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT);
                    return linkedPortalTracker != null ? linkedPortalTracker.getTargetPos(clientLevel).orElse(null) : null;
                }
            };

        public static final Codec<CompassTarget> CODEC = StringRepresentable.fromValues(() -> new CompassTarget[]{ NONE, PORTAL });
        private final String name;

        CompassTarget(final String string2)
        {
            this.name = string2;
        }

        @Override
        public @NotNull String getSerializedName()
        {
            return this.name;
        }

        @Nullable
        abstract GlobalPos get(ClientLevel clientLevel, ItemStack itemStack, @Nullable ItemOwner itemOwner);
    }
}
