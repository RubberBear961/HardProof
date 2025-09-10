package net.bieluuu.hardproof.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class HardProofScreen extends Screen {
    private final Screen parent;

    // Zmienne dynamiczne
    private boolean cheated = false;
    private String usingBackups = "No (though some exist)";
    private int openedToLAN = 2;
    private String datapacks = "NONE";
    private int deaths = 0;
    private String unconventionalMods = "NONE";
    private boolean modInstalled = true; // nowa zmienna

    public HardProofScreen(Screen parent) {
        super(Text.of("HardProof Menu"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int startY = 50;
        int lineHeight = 24;
        int width = 220;
        int x = this.width / 2 - width / 2;

        // STATE z ikoną
        addStateButton(x, startY, width, "STATE:", cheated);

        // Reszta przycisków dynamiczna
        addInfoButton(x, startY + lineHeight, width, "Using Backups:", usingBackups);
        addInfoButton(x, startY + lineHeight * 2, width, "Opened to LAN:", "Yes -> " + openedToLAN + " times");
        addInfoButton(x, startY + lineHeight * 3, width, "Datapacks:", datapacks);
        addInfoButton(x, startY + lineHeight * 4, width, "Number of Deaths:", String.valueOf(deaths));
        addInfoButton(x, startY + lineHeight * 5, width, "Unconventional File Modifications:", unconventionalMods);

        // Nowy przycisk: Mod Installed
        addInfoButton(x, startY + lineHeight * 6, width, "Mod Installed after creating world:", modInstalled ? "Yes" : "No");

        // Przycisk powrotu
        this.addDrawableChild(ButtonWidget.builder(Text.of("Back"), button -> this.client.setScreen(parent))
                .dimensions(this.width / 2 - 50, this.height - 30, 100, 20).build());
    }

    private void addStateButton(int x, int y, int width, String label, boolean cheated) {
        String icon = cheated ? Formatting.RED + "❌" : Formatting.GREEN + "✔";
        String text = label + " " + (cheated ? "Cheated" : "Not Cheated") + " " + icon;

        ButtonWidget stateButton = ButtonWidget.builder(Text.of(text), b -> {}).dimensions(x, y, width, 20).build();
        stateButton.active = false;
        this.addDrawableChild(stateButton);
    }

    private void addInfoButton(int x, int y, int width, String label, String value) {
        ButtonWidget infoButton = ButtonWidget.builder(Text.of(label + " " + value), b -> {}).dimensions(x, y, width, 20).build();
        infoButton.active = false;
        this.addDrawableChild(infoButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, "HardProof Info", this.width / 2, 20, 0xFFFFFF);
    }

    // Metody do aktualizacji zmiennych z zewnątrz
    public void setCheated(boolean cheated) {
        this.cheated = cheated;
    }

    public void setUsingBackups(String usingBackups) {
        this.usingBackups = usingBackups;
    }

    public void setOpenedToLAN(int openedToLAN) {
        this.openedToLAN = openedToLAN;
    }

    public void setDatapacks(String datapacks) {
        this.datapacks = datapacks;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setUnconventionalMods(String unconventionalMods) {
        this.unconventionalMods = unconventionalMods;
    }

    public void setModInstalled(boolean modInstalled) {
        this.modInstalled = modInstalled;
    }
}
