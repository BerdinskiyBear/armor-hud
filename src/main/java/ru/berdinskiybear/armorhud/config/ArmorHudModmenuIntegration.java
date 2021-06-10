package ru.berdinskiybear.armorhud.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

public class ArmorHudModmenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // if (FabricLoader.getInstance().isModLoaded(ArmorHudMod.CLOTH_CONFIG_ID))
        //     return ArmorHudConfigScreenBuilder::create;
        // else
        //TODO enable config screen when ClothConfig 5 works
        return (Screen screen) -> null;
    }
}
