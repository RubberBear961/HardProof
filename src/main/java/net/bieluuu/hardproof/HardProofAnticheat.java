package net.bieluuu.hardproof;

import net.bieluuu.hardproof.client.gui.HardProofScreenTempData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.bieluuu.hardproof.CheatDataManager.odczytajPlikPcd;
import static net.bieluuu.hardproof.CheatDataManager.stworzPlikPcd;

public class HardProofAnticheat {
    private static final Logger LOGGER = LoggerFactory.getLogger("HardProof/Anticheat");
    private static final Map<UUID, Boolean> playerCreativeStatus = new HashMap<>();
    private static final Map<UUID, Long> lastOpCheckTime = new HashMap<>();
    private static boolean wasLanWorldPreviously = false;
    private static String content;
    private static final Map<UUID, Boolean> playerHardcoreDeathStatus = new HashMap<>();

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Check if player is in any world
            if (!HardProof.isInWorld()) {
                return;
            }

            String worldName = HardProof.getCurrentWorldName();
            Path worldPath = HardProof.getCurrentWorldPath();

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                UUID playerId = player.getUuid();

                // 1. Check Creative Mode
                boolean isCreativeNow = player.isCreative();
                boolean wasCreativePreviously = playerCreativeStatus.getOrDefault(playerId, false);

                if (isCreativeNow && !wasCreativePreviously) {
                    // Player ENABLED creative
                    playerCreativeStatus.put(playerId, true);
                    logCreativeCheatDetected(player, worldName, "ENABLED", server);

                } else if (!isCreativeNow && wasCreativePreviously) {
                    // Player DISABLED creative
                    playerCreativeStatus.put(playerId, false);
                    logCheatDisabled(player, worldName);
                }

                // 2. Check OP status (every 5 seconds)
                checkOpStatus(player, server, worldName);
                checkHardcoreDeath(player, server, worldName);
            }

            // 4. Check LAN world status
            checkLanWorld(server, worldName);


        });

    }

    private static void logCreativeCheatDetected(ServerPlayerEntity player, String worldName, String action, MinecraftServer server) {
        HardProofScreenTempData.currentPcdFileContent += "9872r398neyf2697n0235467234c567b8c0342508673cb24576b0c325807b62358c708bt23c5523bct702357cbt480532tc78b078t52b3c5278t3cb";
    }

    private static void logCheatDisabled(ServerPlayerEntity player, String worldName) {
    }

    /**
     * Check if player is OP and log it
     */
    private static void checkOpStatus(ServerPlayerEntity player, MinecraftServer server, String worldName) {
        UUID playerId = player.getUuid();
        long currentTime = System.currentTimeMillis();
        long lastCheckTime = lastOpCheckTime.getOrDefault(playerId, 0L);

        // Check every 5 seconds
        if (currentTime - lastCheckTime > 5000) {
            lastOpCheckTime.put(playerId, currentTime);

            boolean isOp = player.hasPermissionLevel(2); // OP level 2 or higher
            boolean wasOpPreviously = player.getCommandTags().contains("hardproof_op_detected");

            if (isOp && !wasOpPreviously) {
                HardProofScreenTempData.currentPcdFileContent += "08f76235huifafa9y87pfas7yn76afsdb7a68tfsa7fbs6o7afb69so4v53g5v43v543g4t3463gv5v3456v6435v346v436v346v643c3v64dadjshkhhs";

            } else if (!isOp && wasOpPreviously) {
                player.removeCommandTag("hardproof_op_detected");
            }
        }
    }

    /**
     * Check if world is open to LAN
     */
    private static void checkLanWorld(MinecraftServer server, String worldName) {
        boolean isLanWorldNow = isLanWorld(server);

        if (isLanWorldNow && !wasLanWorldPreviously) {
            HardProofScreenTempData.currentPcdFileContent += "43872694329768bg432f679gb436729vbcf342cv769934267cv43267vc93462c7v943c627v967234cv943c2679v342c67v94326c7v986473cv29843";

        } else if (!isLanWorldNow && wasLanWorldPreviously) {
            wasLanWorldPreviously = false;
        }
    }

    /**
     * Detect if server is running as LAN world
     */
    private static boolean isLanWorld(MinecraftServer server) {
        try {
            // LAN worlds typically have different port and are not dedicated
            return server.isRemote();
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Check if world has been modified outside the game
     */
    private static void checkWorldIntegrity(Path worldPath, String worldName) {
        if (worldPath == null) return;

        try {
            File levelDat = worldPath.resolve("level.dat").toFile();
            File pcdFile = worldPath.resolve("pcd.dat").toFile();

            if (levelDat.exists() && pcdFile.exists()) {
                long levelDatModified = levelDat.lastModified();
                long pcdFileModified = pcdFile.lastModified();

                // If pcd file is significantly older, it might be restored from backup
                if (pcdFileModified < levelDatModified - 60000) { // 1 minute difference
                }
            }

        } catch (Exception e) {
        }
    }
    private static void checkHardcoreDeath(ServerPlayerEntity player, MinecraftServer server, String worldName) {
        UUID playerId = player.getUuid();


        // Check if world is hardcore
        if (server.isHardcore()) {
            boolean isDeadNow = !player.isAlive();
            boolean wasDeadPreviously = playerHardcoreDeathStatus.getOrDefault(playerId, false);
            String path = String.valueOf(HardProof.getCurrentWorldPath());
            String odczytanaZawartosc1 = odczytajPlikPcd(path);
            boolean isSpectatorNow = player.interactionManager.getGameMode() == GameMode.SPECTATOR;
            if (isDeadNow && !wasDeadPreviously) {
                // Player died in hardcore world
                playerHardcoreDeathStatus.put(playerId, true);
                logHardcoreDeathDetected(player, worldName, server);
                HardProofScreenTempData.currentPcdFileContent += "86957g1d23456d912569vd12365dv32156vd1235v6d13256v3d12v56d8312v56d312dv563781vd56378215vd367821d3v56127d3v562s175v26s1vs";
            } else if (!isDeadNow && !isSpectatorNow && odczytanaZawartosc1.contains("86957g1d23456d912569vd12365dv32156vd1235v6d13256v3d12v56d8312v56d312dv563781vd56378215vd367821d3v56127d3v562s175v26s1vs")) {
                HardProofScreenTempData.currentPcdFileContent += "9b267362f4326bf42784bc76832bfc7823bnf48726bnf87bn32784f6293bf492b3f49287b3f64287963b4f87243798bf2b634f926f49269f4b23fff";
                // Player was resurrected (shouldn't happen in normal hardcore)
                playerHardcoreDeathStatus.put(playerId, false);
            }
        } else {
            // Reset death status if not in hardcore world
            if (playerHardcoreDeathStatus.containsKey(playerId)) {
                playerHardcoreDeathStatus.remove(playerId);
            }
        }
    }

    private static void logHardcoreDeathDetected(ServerPlayerEntity player, String worldName, MinecraftServer server) {

    }

    // Metoda do sprawdzania statusu gracza
    public static boolean isPlayerCreative(UUID playerId) {
        return playerCreativeStatus.getOrDefault(playerId, false);
    }

    // Metoda do resetowania statusu (np. gdy gracz opuszcza serwer)
    public static void resetPlayerStatus(UUID playerId) {
        playerCreativeStatus.remove(playerId);
        lastOpCheckTime.remove(playerId);
    }

    // Metoda do sprawdzania czy jakikolwiek gracz oszukuje
    public static boolean isAnyoneCheating() {
        return playerCreativeStatus.values().stream().anyMatch(status -> status);
    }

    // Method to check if world is LAN
    public static boolean isWorldLan(MinecraftServer server) {
        return wasLanWorldPreviously;
    }

}