package dev.maxoduke.mods.portallinkingcompass;

import net.fabricmc.api.ModInitializer;

public class FabricInitializer implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        PortalLinkingCompass.register();
    }
}
