package dev.maxoduke.mods.portallinkingcompass;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

public class FabricInitializer implements ModInitializer, ClientModInitializer
{
    @Override
    public void onInitialize()
    {
        PortalLinkingCompass.register();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient()
    {
        PortalLinkingCompass.registerClient();
    }
}
