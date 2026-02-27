package com.teamdimensional.claycolorer;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import io.github.trcdevelopers.clayium.api.ClayiumApi;
import io.github.trcdevelopers.clayium.api.unification.material.CMaterial;
import io.github.trcdevelopers.clayium.api.util.registry.MaterialRegistry;
import net.minecraft.util.ResourceLocation;

public class MaterialRecoloringLogic {
    public static class MaterialData {
        ResourceLocation location;
        int[] colors;

        MaterialData(ResourceLocation location, int[] colors) {
            this.location = location;
            this.colors = colors;
        }
    }

    private static final List<MaterialData> materials = new ArrayList<>();

    public static void apply(List<MaterialData> mats) {
        rollback();
        for (MaterialData m : mats) {
            applyColor(m.location, m.colors);
        }
    }

    @Nullable
    public static MaterialData parse(String s) {
        String[] split = s.split(":");
        if (split.length != 2 && split.length != 3) {
            return null;
        }
        String modId = split.length == 2 ? "clayium" : split[0];
        String matName = split[split.length - 2];
        String colorsString = split[split.length - 1];
        String[] colorsSplit = colorsString.split(",");

        int[] colors = new int[colorsSplit.length];
        try {
            for (int i = 0; i < colorsSplit.length; i++) {
                String col = colorsSplit[i];
                int base = 10;
                if (col.startsWith("0x")) {
                    col = col.substring(2);
                    base = 16;
                }
                colors[i] = Integer.parseInt(col, base);
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return new MaterialData(new ResourceLocation(modId, matName), colors);
    }

    public static void rollback() {
        MaterialRegistry<CMaterial> registry = ClayiumApi.INSTANCE.getMaterialRegistry();
        for (MaterialData m : materials) {
            // Rollback is safe.
            int[] colors = registry.getObject(m.location).getColors();
            for (int i = 0; i < m.colors.length; i++) {
                colors[i] = m.colors[i];
            }
        }

        materials.clear();
    }

    public static boolean applyColor(ResourceLocation loc, int[] newColors) {
        MaterialRegistry<CMaterial> registry = ClayiumApi.INSTANCE.getMaterialRegistry();
        CMaterial regMat = registry.getObject(loc);
        if (regMat == null) {
            ClayiumRecolorer.LOGGER.warn("Tried to change colors for {}, which does not exist", loc);
            return false;
        }
        int[] colors = regMat.getColors();
        if (colors == null) {
            ClayiumRecolorer.LOGGER.warn(
                    "Tried to change colors for {}, which is a placeholder material without colors", loc);
            return false;
        }
        if (colors.length != newColors.length) {
            ClayiumRecolorer.LOGGER.warn(
                    "Tried to change colors for {}, which has {} colors, but instead {} were provided",
                    loc, colors.length, newColors.length);
            return false;
        }
        int[] oldColors = colors.clone();
        for (int i = 0; i < newColors.length; i++) {
            colors[i] = newColors[i];
        }
        materials.add(new MaterialData(loc, oldColors));
        return true;
    }
}
