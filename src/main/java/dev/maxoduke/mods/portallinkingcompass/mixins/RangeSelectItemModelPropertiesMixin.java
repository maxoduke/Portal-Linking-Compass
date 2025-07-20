package dev.maxoduke.mods.portallinkingcompass.mixins;

import dev.maxoduke.mods.portallinkingcompass.PortalLinkingCompass;
import dev.maxoduke.mods.portallinkingcompass.client.PortalLinkingCompassAngle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(RangeSelectItemModelProperties.class)
public class RangeSelectItemModelPropertiesMixin
{
    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void bootstrap(CallbackInfo ci)
    {
        RangeSelectItemModelProperties.ID_MAPPER.put(ResourceLocation.fromNamespaceAndPath(PortalLinkingCompass.MOD_ID, "angle"), PortalLinkingCompassAngle.MAP_CODEC);
    }
}
