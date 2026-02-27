package com.teamdimensional.claycolorer;

import java.util.ArrayList;
import java.util.List;

import com.teamdimensional.claycolorer.MaterialRecoloringLogic.MaterialData;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Tags.MOD_ID)
public class ClayiumRecolorerConfig {
    @Config.Comment("List of materials with their new colors. Example format: `clayium:actinium:0xFF0000,0x00FF00,0x0000FF`. `clayium:` may be omitted.")
    public static String[] materialColors = new String[] {};

    public static void applyConfig() {
        List<MaterialData> mats = new ArrayList<>();
        for (String s : materialColors) {
            MaterialData md = MaterialRecoloringLogic.parse(s);
            if (md == null) {
                ClayiumRecolorer.LOGGER.warn("Could not parse config string: " + s);
                continue;
            }
            mats.add(md);
        }

        MaterialRecoloringLogic.apply(mats);
    }

    @Mod.EventBusSubscriber(modid = Tags.MOD_ID)
    public static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Tags.MOD_ID)) {
                ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
                applyConfig();
            }
        }
    }
}
