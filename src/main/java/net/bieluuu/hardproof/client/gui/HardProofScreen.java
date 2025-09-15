package net.bieluuu.hardproof.client.gui;

import net.bieluuu.hardproof.CheatDataManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class HardProofScreen extends Screen {
    private final Screen parent;
    private final List<ButtonWidget> infoButtons = new ArrayList<>();
    private long lastCheckTime = 0;
    private String lastContent = "";

    // Zmienne dynamiczne
    private boolean cheated = false;
    private String usingBackups = "No";
    private String datapacks = "NONE";
    private String unconventionalMods = "NONE";
    private boolean modInstalled = true;
    private boolean wasOpenedToLAN = false;
    private String reason = "NONE";

    public HardProofScreen(Screen parent) {
        super(Text.of("HardProof Menu"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        infoButtons.clear();
        int startY = 50;
        int lineHeight = 24;
        int width = 250;
        int x = this.width / 2 - width / 2;

        // Tworzymy przyciski tylko raz
        infoButtons.add(createStateButton(x, startY, width));
        infoButtons.add(createInfoButton(x, startY + lineHeight, width, "Using Backups:", usingBackups));
        infoButtons.add(createInfoButton(x, startY + lineHeight * 2, width, "Opened to LAN:", wasOpenedToLAN ? "Yes" : "No"));
        infoButtons.add(createInfoButton(x, startY + lineHeight * 3, width, "Datapacks:", datapacks));
        infoButtons.add(createInfoButton(x, startY + lineHeight * 4, width, "Reason:", reason));
        infoButtons.add(createInfoButton(x, startY + lineHeight * 5, width, "File Modifications:", unconventionalMods));
        infoButtons.add(createInfoButton(x, startY + lineHeight * 6, width, "Mod Installed:", modInstalled ? "Yes" : "No"));

        // Dodaj wszystkie przyciski na raz
        infoButtons.forEach(this::addDrawableChild);
        infoButtons.forEach(button -> button.active = false);

        // Przycisk powrotu
        this.addDrawableChild(ButtonWidget.builder(Text.of("Back"), button -> this.client.setScreen(parent))
                .dimensions(this.width / 2 - 50, this.height - 30, 100, 20).build());

        // Pierwsze sprawdzenie od razu
        checkForCheats();
        updateButtons();
    }

    private ButtonWidget createStateButton(int x, int y, int width) {
        return ButtonWidget.builder(Text.of("STATE: Checking..."), b -> {})
                .dimensions(x, y, width, 20)
                .build();
    }

    private ButtonWidget createInfoButton(int x, int y, int width, String label, String value) {
        return ButtonWidget.builder(Text.of(label + " " + value), b -> {})
                .dimensions(x, y, width, 20)
                .build();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, "HardProof Info", this.width / 2, 20, 0xFFFFFF);

        // Sprawdzaj cheaty tylko raz na sekundę (zamiast 60 razy!)
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCheckTime > 1000) {
            checkForCheats();
            updateButtons(); // Aktualizuj tylko tekst przycisków
            lastCheckTime = currentTime;
        }
    }

    private void checkForCheats() {
        String currentPath = String.valueOf(HardProofScreenTempData.selectedWorldPathPcd);
        String content = CheatDataManager.odczytajPlikPcd(currentPath);

        if (content == null) {
            content = "";
        }

        // Sprawdzaj tylko rjeśli zawartość się zmieniła
        if (!content.equals(lastContent)) {
            lastContent = content;

            // Resetuj stan przed sprawdzeniem
            boolean previouslyCheated = cheated;
            cheated = false;
            wasOpenedToLAN = false;
            reason = "NONE";

            // Sprawdzaj poszczególne cheaty
            boolean creativeCheat = content.contains("9872r398neyf2697n0235467234c567b8c0342508673cb24576b0c325807b62358c708bt23c5523bct702357cbt480532tc78b078t52b3c5278t3cb");
            boolean opCheat = content.contains("08f76235huifafa9y87pfas7yn76afsdb7a68tfsa7fbs6o7afb69so4v53g5v43v543g4t3463gv5v3456v6435v346v436v346v643c3v64dadjshkhhs");
            boolean lanOpened = content.contains("43872694329768bg432f679gb436729vbcf342cv769934267cv43267vc93462c7v943c627v967234cv943c2679v342c67v94326c7v986473cv29843");
            boolean hardcoreCheat = content.contains("9b267362f4326bf42784bc76832bfc7823bnf48726bnf87bn32784f6293bf492b3f49287b3f64287963b4f87243798bf2b634f926f49269f4b23fff");

            // Ustawianie reason POPRAWNIE
            List<String> reasons = new ArrayList<>();

            if (creativeCheat) {
                cheated = true;
                reasons.add("Using creative");
            }
            if (opCheat) {
                cheated = true;
                reasons.add("Using OP privileges");
            }
            if (hardcoreCheat) {
                cheated = true;
                reasons.add("Player should be dead");
            }
            if (lanOpened) {
                wasOpenedToLAN = true;
            }

            // Łącz powody tylko jeśli są
            if (!reasons.isEmpty()) {
                reason = String.join(", ", reasons);
            } else {
                reason = "NONE";
            }
        }
    }

    private void updateButtons() {
        if (infoButtons.size() < 7) return;

        // Aktualizuj tylko tekst przycisków (nie twórz nowych!)
        String stateIcon = cheated ? Formatting.RED + "❌" : Formatting.GREEN + "✅";
        String stateText = "STATE: " + (cheated ? "Cheated " : "Not Cheated ") + stateIcon;
        infoButtons.get(0).setMessage(Text.of(stateText));

        infoButtons.get(1).setMessage(Text.of("Using Backups: " + usingBackups));

        String lanText = "Opened to LAN: " + (wasOpenedToLAN ? Formatting.YELLOW + "Yes" : "No");
        infoButtons.get(2).setMessage(Text.of(lanText));

        infoButtons.get(3).setMessage(Text.of("Datapacks: " + datapacks));

        String reasonText = "Reason: " + (cheated ? Formatting.RED + reason : "NONE");
        infoButtons.get(4).setMessage(Text.of(reasonText));

        infoButtons.get(5).setMessage(Text.of("File Modifications: " + unconventionalMods));
        infoButtons.get(6).setMessage(Text.of("Mod Installed: " + (modInstalled ? "Yes" : "No")));
    }

    // Metody do aktualizacji z zewnątrz
    public void setCheated(boolean cheated) {
        this.cheated = cheated;
    }

    public void setUsingBackups(String usingBackups) {
        this.usingBackups = usingBackups;
    }

    public void setOpenedToLAN(boolean wasOpenedToLAN) {
        this.wasOpenedToLAN = wasOpenedToLAN;
    }

    public void setDatapacks(String datapacks) {
        this.datapacks = datapacks;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setUnconventionalMods(String unconventionalMods) {
        this.unconventionalMods = unconventionalMods;
    }

    public void setModInstalled(boolean modInstalled) {
        this.modInstalled = modInstalled;
    }
}