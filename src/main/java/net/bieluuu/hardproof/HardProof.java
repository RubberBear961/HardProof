package net.bieluuu.hardproof;

import net.bieluuu.hardproof.client.gui.HardProofScreenTempData;
import net.bieluuu.hardproof.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.bieluuu.hardproof.CheatDataManager.odczytajPlikPcd;
import static net.bieluuu.hardproof.CheatDataManager.stworzPlikPcd;

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
            /// player entered sequence
            String content = "";
            String path = String.valueOf(HardProof.getCurrentWorldPath());
            String odczytanaZawartosc = odczytajPlikPcd(path);
                if (odczytanaZawartosc == null) {
                    boolean utworzono = stworzPlikPcd(path, content);
                    if (utworzono) {
                        String odczytanaZawartosc2 = odczytajPlikPcd(path);
                        HardProofScreenTempData.currentPcdFileContent = odczytanaZawartosc2;
                    }
                else {
                    HardProofScreenTempData.currentPcdFileContent = odczytanaZawartosc;
                    }
            }
            // Zamiast while(true) użyj:
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                if (currentWorldPath != null) {
                    stworzPlikPcd(String.valueOf(currentWorldPath),
                            HardProofScreenTempData.currentPcdFileContent);
                }
            }, 1, 1, TimeUnit.MINUTES);

        });

        // Event gdy serwer (świat) się zatrzymuje
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            /// lefty the world sequence
            String path = String.valueOf(HardProof.getCurrentWorldPath());
            boolean created = stworzPlikPcd(path, HardProofScreenTempData.currentPcdFileContent);
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