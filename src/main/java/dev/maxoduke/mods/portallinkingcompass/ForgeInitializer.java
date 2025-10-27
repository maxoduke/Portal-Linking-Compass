package dev.maxoduke.mods.portallinkingcompass;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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
        BusGroup modBusGroup = context.getModBusGroup();

        ITEMS.register(modBusGroup);
        SOUND_EVENTS.register(modBusGroup);
        DATA_COMPONENT_TYPES.register(modBusGroup);

        BuildCreativeModeTabContentsEvent.BUS.addListener(ForgeInitializer::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(PortalLinkingCompass.ITEM);
    }
}
