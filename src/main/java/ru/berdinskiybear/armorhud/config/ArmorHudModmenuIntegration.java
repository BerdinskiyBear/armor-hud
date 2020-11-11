package ru.berdinskiybear.armorhud.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ArmorHudModmenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2"))
            return ArmorHudConfigScreenBuilder::create;
        else
            return null;
    }
}
