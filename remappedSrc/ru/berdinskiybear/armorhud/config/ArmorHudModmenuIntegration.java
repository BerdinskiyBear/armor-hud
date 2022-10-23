package ru.berdinskiybear.armorhud.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import ru.berdinskiybear.armorhud.ArmorHudMod;

public class ArmorHudModmenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded(ArmorHudMod.CLOTH_CONFIG_ID))
            return ArmorHudConfigScreenBuilder::create;
        else
            return (Screen screen) -> null;
    }
}
