package dev.maxoduke.mods.portallinkingcompass;

import com.mojang.serialization.MapCodec;
import dev.maxoduke.mods.portallinkingcompass.client.PortalLinkingCompassAngle;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

@SuppressWarnings({"unchecked", "unused"})
@Mod.EventBusSubscriber(modid = PortalLinkingCompass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeClientInitializer
{
    private static final Field idMapperField = ObfuscationReflectionHelper.findField(RangeSelectItemModelProperties.class, "ID_MAPPER");

    @SubscribeEvent
    public static void onConstructMod(FMLConstructModEvent event)
    {
        try
        {
            var idMapper = (ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends RangeSelectItemModelProperty>>) idMapperField.get(null);
            idMapper.put(Identifier.fromNamespaceAndPath(PortalLinkingCompass.MOD_ID, "angle"), PortalLinkingCompassAngle.MAP_CODEC);
        }
        catch (IllegalAccessException e)
        {
            PortalLinkingCompass.LOGGER.error("Failed to register Portal Linking Compass client code");
            throw new RuntimeException("Failed to register Portal Linking Compass client code");
        }
    }
}
