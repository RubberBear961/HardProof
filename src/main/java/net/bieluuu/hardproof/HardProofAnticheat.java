package net.bieluuu.hardproof;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HardProofAnticheat {
    private static final Logger LOGGER = LoggerFactory.getLogger("HardProof/Anticheat");
    private static final Map<UUID, Boolean> playerCreativeStatus = new HashMap<>();

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Sprawdź czy gracz jest w jakimś świecie
            if (!HardProof.isInWorld()) {
                return;
            }

            String worldName = HardProof.getCurrentWorldName();

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                UUID playerId = player.getUuid();
                boolean isCreativeNow = player.isCreative();
                boolean wasCreativePreviously = playerCreativeStatus.getOrDefault(playerId, false);

                if (isCreativeNow && !wasCreativePreviously) {
                    // Gracz WŁĄCZYŁ kreatywny
                    playerCreativeStatus.put(playerId, true);
                    logCheatDetected(player, worldName, "WŁĄCZYŁ");

                } else if (!isCreativeNow && wasCreativePreviously) {
                    // Gracz WYŁĄCZYŁ kreatywny
                    playerCreativeStatus.put(playerId, false);
                    logCheatDisabled(player, worldName);
                }
            }
        });

        LOGGER.info("Anticheat system initialized");
    }

    private static void logCheatDetected(ServerPlayerEntity player, String worldName, String action) {
        LOGGER.warn("🚨 CHEAT DETECTED! {} {} Creative! '{}'",
                player.getName().getString(), action, worldName);

        LOGGER.info("📋 More Info:");
        LOGGER.info("   👤 Player: {}", player.getName().getString());
        LOGGER.info("   🆔 UUID: {}", player.getUuid());
        LOGGER.info("   🌍 World: {}", worldName);
        LOGGER.info("   📍 Pos: {:.1f}, {:.1f}, {:.1f}",
                player.getX(), player.getY(), player.getZ());
        LOGGER.info("   🎮 Game Mode: {}", player.interactionManager.getGameMode());
    }

    private static void logCheatDisabled(ServerPlayerEntity player, String worldName) {
        LOGGER.info("✅ Player {} just turned into a cheater on world: '{}'",
                player.getName().getString(), worldName);
    }

    // Metoda do sprawdzania statusu gracza
    public static boolean isPlayerCreative(UUID playerId) {
        return playerCreativeStatus.getOrDefault(playerId, false);
    }

    // Metoda do resetowania statusu (np. gdy gracz opuszcza serwer)
    public static void resetPlayerStatus(UUID playerId) {
        playerCreativeStatus.remove(playerId);
    }

    // Metoda do sprawdzania czy jakikolwiek gracz oszukuje
    public static boolean isAnyoneCheating() {
        return playerCreativeStatus.values().stream().anyMatch(status -> status);
    }
}