package net.bieluuu.hardproof.mixin;



import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldListWidget.WorldEntry.class)
public class HardProofSingleplayerMixin {

    @Shadow
    private net.minecraft.client.MinecraftClient client;

    @Inject(at = @At("RETURN"), method = "render")
    private void addHardProofButton(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress, CallbackInfo ci) {


        int buttonWidth = 20;
        int buttonHeight = 20;
        int buttonX = x + entryWidth + 10; // +10 px poza prawą krawędź świata
        int buttonY = y + (entryHeight - buttonHeight) / 2;

        // rysowanie prostego prostokątnego przycisku
        context.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, 0xFF555555);
        context.drawTextWithShadow(this.client.textRenderer, Text.of("!"), buttonX + 5, buttonY + 5, Colors.WHITE);

    }

}
