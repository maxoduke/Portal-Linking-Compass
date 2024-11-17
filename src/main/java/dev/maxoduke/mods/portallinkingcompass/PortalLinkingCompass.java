package dev.maxoduke.mods.portallinkingcompass;

import dev.maxoduke.mods.portallinkingcompass.item.PortalLinkingCompassItem;
import dev.maxoduke.mods.portallinkingcompass.item.component.LinkedPortalTracker;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

@SuppressWarnings("SpellCheckingInspection")
public class PortalLinkingCompass
{
    public static final String MOD_ID = "portallinkingcompass";

    public static final String LINKED_PORTAL_TRACKER_COMPONENT_NAME = "linked_portal_tracker_component";
    public static final ResourceLocation LINKED_PORTAL_TRACKER_COMPONENT_RESOURCE = ResourceLocation.fromNamespaceAndPath(
        PortalLinkingCompass.MOD_ID,
        PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT_NAME
    );
    public static final DataComponentType<LinkedPortalTracker> LINKED_PORTAL_TRACKER_COMPONENT = DataComponentType
        .<LinkedPortalTracker>builder()
        .persistent(LinkedPortalTracker.CODEC)
        .networkSynchronized(LinkedPortalTracker.STREAM_CODEC)
        .build();

    public static final String ITEM_NAME = "portal_linking_compass";
    public static final ResourceLocation ITEM_RESOURCE = ResourceLocation.fromNamespaceAndPath(
        PortalLinkingCompass.MOD_ID,
        PortalLinkingCompass.ITEM_NAME
    );

    public static final Item ITEM = Items.registerItem(
        ResourceKey.create(Registries.ITEM, PortalLinkingCompass.ITEM_RESOURCE),
        PortalLinkingCompassItem::new,
        new Item.Properties().component(LINKED_PORTAL_TRACKER_COMPONENT, new LinkedPortalTracker())
    );

    public static final String COMPASS_LOCKS_SOUND_NAME = "item.portal_linking_compass.lock";
    public static final ResourceLocation COMPASS_LOCKS_SOUND_RESOURCE = ResourceLocation.fromNamespaceAndPath(
        PortalLinkingCompass.MOD_ID,
        COMPASS_LOCKS_SOUND_NAME
    );
    public static final SoundEvent COMPASS_LOCKS_SOUND_EVENT = SoundEvent.createVariableRangeEvent(COMPASS_LOCKS_SOUND_RESOURCE);

    public static void register()
    {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register((content) -> content.accept(PortalLinkingCompass.ITEM));

        Registry.register(
            BuiltInRegistries.SOUND_EVENT,
            PortalLinkingCompass.COMPASS_LOCKS_SOUND_RESOURCE,
            PortalLinkingCompass.COMPASS_LOCKS_SOUND_EVENT
        );

        Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT_RESOURCE,
            PortalLinkingCompass.LINKED_PORTAL_TRACKER_COMPONENT
        );
    }

    public static void registerClient()
    {
        ItemProperties.register(
            PortalLinkingCompass.ITEM,
            ResourceLocation.withDefaultNamespace("angle"),
            new CompassItemPropertyFunction(PortalLinkingCompassItem::pointToTarget)
        );
    }
}
