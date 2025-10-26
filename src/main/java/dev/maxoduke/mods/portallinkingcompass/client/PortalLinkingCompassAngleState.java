package dev.maxoduke.mods.portallinkingcompass.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.maxoduke.mods.portallinkingcompass.PortalLinkingCompass;
import dev.maxoduke.mods.portallinkingcompass.item.component.LinkedPortalTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.NeedleDirectionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
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
    protected float calculate(ItemStack itemStack, ClientLevel clientLevel, int seed, @Nullable ItemOwner itemOwner)
    {
        long gameTime = clientLevel.getGameTime();
        Entity entity;

        if (itemOwner == null)
        {
            entity = itemStack.getEntityRepresentation();
        }
        else
        {
            entity = itemOwner.asLivingEntity();
            if (entity == null)
                entity = itemStack.getEntityRepresentation();
        }

        GlobalPos targetPos = this.target.get(clientLevel, itemStack, entity);
        return !isValidCompassTargetPos(entity, targetPos)
            ? this.getRandomlySpinningRotation(seed, gameTime)
            : this.getRotationTowardsCompassTarget(entity, gameTime, targetPos.pos());
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

    private float getRotationTowardsCompassTarget(Entity entity, long gameTime, BlockPos blockPos)
    {
        float f = (float) getAngleFromEntityToPos(entity, blockPos);
        float g = getWrappedVisualRotationY(entity);

        if (this.wobbler.shouldUpdate(gameTime))
            this.wobbler.update(gameTime, 0.5F - (g - 0.25F));

        float h = f + this.wobbler.rotation();
        return Mth.positiveModulo(h, 1.0F);
    }

    private static boolean isValidCompassTargetPos(Entity entity, @Nullable GlobalPos globalPos)
    {
        return globalPos != null && !(globalPos.pos().distToCenterSqr(entity.position()) < 1.0E-5F);
    }

    private static double getAngleFromEntityToPos(Entity entity, BlockPos blockPos)
    {
        Vec3 vec3 = Vec3.atCenterOf(blockPos);
        return Math.atan2(vec3.z() - entity.getZ(), vec3.x() - entity.getX()) / (float) (Math.PI * 2);
    }

    private static float getWrappedVisualRotationY(Entity entity)
    {
        return Mth.positiveModulo(entity.getVisualRotationYInDegrees() / 360.0F, 1.0F);
    }

    private static int hash(int i)
    {
        return i * 1327217883;
    }

    @Environment(EnvType.CLIENT)
    public enum CompassTarget implements StringRepresentable
    {
        NONE("none")
        {
            @Nullable
            @Override
            public GlobalPos get(ClientLevel clientLevel, ItemStack itemStack, Entity entity)
            {
                return null;
            }
        },

        PORTAL("portal")
        {
            @Nullable
            @Override
            public GlobalPos get(ClientLevel clientLevel, ItemStack itemStack, Entity entity)
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
        abstract GlobalPos get(ClientLevel clientLevel, ItemStack itemStack, Entity entity);
    }
}
