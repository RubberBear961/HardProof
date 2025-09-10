package net.bieluuu.hardproof;

import net.bieluuu.hardproof.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class HardProof implements ModInitializer {
    public static final String MOD_ID = "hardproof";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static MinecraftServer currentServer;
    private static String currentWorldName;
    private static Path currentWorldPath;

    @Override
    public void onInitialize() {
        ModItems.registerModItems();

        // Inicjalizacja anticheata
        HardProofAnticheat.initialize();

        // Event gdy serwer (świat) startuje
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            currentServer = server;
            currentWorldName = getCurrentWorldName(server);
            currentWorldPath = getCurrentWorldPath(server);
            LOGGER.info("✅ Player entered world: {}", currentWorldName);
            LOGGER.info("📁 Full path: {}", currentWorldPath);
        });

        // Event gdy serwer (świat) się zatrzymuje
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            LOGGER.info("🚪 Player left world: {}", currentWorldName);
            currentServer = null;
            currentWorldName = null;
            currentWorldPath = null;
        });
    }

    public static String getCurrentWorldName(MinecraftServer server) {
        return server.getSaveProperties().getLevelName();
    }

    public static Path getCurrentWorldPath(MinecraftServer server) {
        return server.getSavePath(WorldSavePath.ROOT);
    }

    public static String getCurrentWorldName() {
        return currentWorldName;
    }

    public static Path getCurrentWorldPath() {
        return currentWorldPath;
    }

    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }

    public static boolean isInWorld() {
        return currentWorldName != null;
    }
}