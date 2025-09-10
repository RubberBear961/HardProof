package net.bieluuu.hardproof.mixin;


import net.bieluuu.hardproof.client.gui.HardProofScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SelectWorldScreen.class)
public abstract class HardProofSingleplayerMixin extends Screen {

    @Shadow
    private WorldListWidget levelList;
    @Unique
    private ButtonWidget hardproofButton;

    protected HardProofSingleplayerMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void addHardproofButton(CallbackInfo ci) {
        this.hardproofButton = this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("HardProof"), button -> {
                            this.levelList.getSelectedAsOptional().ifPresent(entry -> {
                                MinecraftClient client = MinecraftClient.getInstance();
                                client.setScreen(new HardProofScreen(client.currentScreen));
                            });
                        })
                        .dimensions(this.width / 2 - 154 - 72 - 5, this.height - 28, 72, 20)
                        .build()
        );
        this.hardproofButton.active = false;
    }

    @Inject(at = @At("RETURN"), method = "worldSelected")
    private void checkHardproof(LevelSummary levelSummary, CallbackInfo ci) {
        if (this.hardproofButton != null) {
            this.hardproofButton.active = levelSummary != null && levelSummary.isEditable();
        }
    }

}



