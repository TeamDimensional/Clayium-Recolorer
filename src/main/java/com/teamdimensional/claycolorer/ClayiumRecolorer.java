package com.teamdimensional.claycolorer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies = "required-after:clayium", clientSideOnly = true)
public class ClayiumRecolorer {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClayiumRecolorerConfig.applyConfig();
    }

}
