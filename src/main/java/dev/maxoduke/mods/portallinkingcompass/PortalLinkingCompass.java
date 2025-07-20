package dev.maxoduke.mods.portallinkingcompass;

import dev.maxoduke.mods.portallinkingcompass.item.PortalLinkingCompassItem;
import dev.maxoduke.mods.portallinkingcompass.item.component.LinkedPortalTracker;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("SpellCheckingInspection")
public class PortalLinkingCompass
{
    public static final String MOD_ID = "portallinkingcompass";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final String LINKED_PORTAL_TRACKER_COMPONENT_NAME = "linked_portal_tracker";
    public static final DataComponentType<LinkedPortalTracker> LINKED_PORTAL_TRACKER_COMPONENT = DataComponentType
        .<LinkedPortalTracker>builder()
        .persistent(LinkedPortalTracker.CODEC)
        .networkSynchronized(LinkedPortalTracker.STREAM_CODEC)
        .build();

    public static final String ITEM_NAME = "portal_linking_compass";
    public static final ResourceLocation ITEM_RESOURCE = ResourceLocation.fromNamespaceAndPath(MOD_ID, ITEM_NAME);
    public static final Item ITEM = new PortalLinkingCompassItem(new Item
        .Properties()
        .setId(ForgeInitializer.ITEMS.key(ITEM_NAME))
        .component(LINKED_PORTAL_TRACKER_COMPONENT, LinkedPortalTracker.empty())
    );

    public static final String COMPASS_LOCKS_SOUND_NAME = "item.portal_linking_compass.lock";
    public static final ResourceLocation COMPASS_LOCKS_SOUND_RESOURCE = ResourceLocation.fromNamespaceAndPath(PortalLinkingCompass.MOD_ID, COMPASS_LOCKS_SOUND_NAME);
    public static final SoundEvent COMPASS_LOCKS_SOUND_EVENT = SoundEvent.createVariableRangeEvent(COMPASS_LOCKS_SOUND_RESOURCE);
}
