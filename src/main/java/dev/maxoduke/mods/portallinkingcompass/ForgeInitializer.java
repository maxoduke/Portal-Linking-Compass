package dev.maxoduke.mods.portallinkingcompass;

import com.mojang.serialization.MapCodec;
import dev.maxoduke.mods.portallinkingcompass.client.PortalLinkingCompassAngle;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;

@Mod(PortalLinkingCompass.MOD_ID)
public class ForgeInitializer
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PortalLinkingCompass.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PortalLinkingCompass.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE.key(), PortalLinkingCompass.MOD_ID);

    static
    {
        ITEMS.register(PortalLinkingCompass.ITEM_NAME, () -> PortalLinkingCompass.ITEM);
        SOUND_EVENTS.register(PortalLinkingCompass.COMPASS_LOCKS_SOUND_NAME, () -> PortalLinkingCompass.COMPASS_LOCKS_SOUND_EVENT);
        DATA_COMPONENT_TYPES.register(PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT_NAME, () -> PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT);
    }

    public ForgeInitializer(FMLJavaModLoadingContext context)
    {
        IEventBus eventBus = context.getModEventBus();

        ITEMS.register(eventBus);
        SOUND_EVENTS.register(eventBus);
        DATA_COMPONENT_TYPES.register(eventBus);

        MinecraftForge.EVENT_BUS.register(this);
        eventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(PortalLinkingCompass.ITEM);
    }

    @Mod.EventBusSubscriber(modid = PortalLinkingCompass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SuppressWarnings("unchecked")
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            try
            {
                Field idMapperField = ObfuscationReflectionHelper.findField(RangeSelectItemModelProperties.class, "ID_MAPPER");
                var idMapper = (ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends RangeSelectItemModelProperty>>) idMapperField.get(null);

                idMapper.put(PortalLinkingCompass.ITEM_RESOURCE, PortalLinkingCompassAngle.MAP_CODEC);
            }
            catch (IllegalAccessException e)
            {
                PortalLinkingCompass.LOGGER.error("Failed to register Portal Linking Compass client code");
                throw new RuntimeException("Failed to register Portal Linking Compass client code");
            }
        }
    }
}
