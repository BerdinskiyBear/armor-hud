package ru.berdinskiybear.armorhud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import ru.berdinskiybear.armorhud.config.ArmorHudConfigReloader;

public final class ArmorHudModEntrypoint implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ArmorHudMod.readCurrentConfig();
        if (FabricLoader.getInstance().isModLoaded(ArmorHudMod.FABRIC_RESOURCE_LOADER_ID)) {
            ArmorHudConfigReloader.register();
        }
    }
}
