package net.bieluuu.hardproof.mixin;


import net.bieluuu.hardproof.client.gui.HardProofScreen;
import net.bieluuu.hardproof.client.gui.HardProofScreenTempData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelSummary;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(SelectWorldScreen.class)
public abstract class HardProofSingleplayerMixin extends Screen {

    @Shadow
    private WorldListWidget levelList;

    @Shadow
    public abstract void worldSelected(@Nullable LevelSummary levelSummary);

    @Unique
    private ButtonWidget hardproofButton;

    protected HardProofSingleplayerMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void addHardproofButton(CallbackInfo ci) {
        this.hardproofButton = this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("i"), button -> {
                            this.levelList.getSelectedAsOptional().ifPresent(entry -> {
                                MinecraftClient client = MinecraftClient.getInstance();
                                client.setScreen(new HardProofScreen(client.currentScreen));
                            });
                        })
                        .dimensions(this.width / 2 - 154 - 20 - 5, this.height - 28, 20, 20)
                        .build()
        );
        this.hardproofButton.active = false;
    }

    @Unique
    private Path selectedWorldPath;

    @Inject(at = @At("RETURN"), method = "worldSelected")
    private void checkHardproof(LevelSummary levelSummary, CallbackInfo ci) {
        if (this.hardproofButton != null) {
            this.hardproofButton.active = levelSummary != null && levelSummary.isEditable();
        }

        if (levelSummary != null) {
            // katalog z save'ami
            Path savesDir = MinecraftClient.getInstance().getLevelStorage().getSavesDirectory();

            // UWAGA: LevelSummary nie ma już getFile ani getLevelDirectoryName
            // UWAGA: LevelSummary nie ma już getFile ani getLevelDirectoryName
            // ale Yarn / Mojang mappings zostawiają zwykle "getName" jako ID folderu
            String folderName = levelSummary.getName(); // <- to powinien być ID folderu, a nie tylko displayName

            this.selectedWorldPath = savesDir.resolve(folderName);
            HardProofScreenTempData.selectedWorldPathPcd = selectedWorldPath;
            System.out.println("Wybrana ścieżka świata: " + this.selectedWorldPath);
        } else {
            this.selectedWorldPath = null;
        }
    }




}



