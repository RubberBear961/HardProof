package net.bieluuu.hardproof;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.DosFileAttributes;
import java.security.MessageDigest;
import java.util.Base64;

public class CheatDataManager {
    private static final String PLIK_JAWNY = "pcd.dat";
    private static final String PLIK_UKRYTY = ".pcd_hidden.dat";

    /**
     * Tworzy ukryty plik z checksumą i jawny plik w folderze świata
     */
    public static boolean stworzPlikPcd(String sciezkaSwiat, String zawartosc) {
        try {
            Path folderSwiat = Paths.get(sciezkaSwiat);

            // Utwórz folder świata jeśli nie istnieje
            if (!Files.exists(folderSwiat)) {
                Files.createDirectories(folderSwiat);
            }

            // 1. Utwórz JAWNY plik w folderze świata
            Path plikJawny = folderSwiat.resolve(PLIK_JAWNY);
            Files.write(plikJawny, zawartosc.getBytes());
            System.out.println("Utworzono jawny plik: " + plikJawny);

            // 2. Oblicz checksumę zawartości
            String checksuma = obliczChecksume(zawartosc);
            String zawartoscZChecksuma = zawartosc + "\nCHECKSUM:" + checksuma;

            // 3. Utwórz UKRYTY plik z checksumą
            Path plikUkryty = folderSwiat.resolve(PLIK_UKRYTY);
            Files.write(plikUkryty, zawartoscZChecksuma.getBytes());

            // 4. Ukryj plik (tylko Windows)
            ukryjPlik(plikUkryty);
            System.out.println("Utworzono ukryty plik z checksumą: " + plikUkryty);

            return true;

        } catch (IOException e) {
            System.err.println("Błąd podczas tworzenia plików: " + e.getMessage());
            return false;
        }
    }

    /**
     * Odczytuje i weryfikuje plik na podstawie ukrytej checksumy
     */
    public static String odczytajPlikPcd(String sciezkaSwiat) {
        try {
            Path folderSwiat = Paths.get(sciezkaSwiat);
            Path plikJawny = folderSwiat.resolve(PLIK_JAWNY);
            Path plikUkryty = folderSwiat.resolve(PLIK_UKRYTY);

            // Sprawdź czy oba pliki istnieją
            if (!Files.exists(plikJawny)) {
                System.err.println("Jawny plik nie istnieje: " + plikJawny);
                return null;
            }

            if (!Files.exists(plikUkryty)) {
                System.err.println("Ukryty plik nie istnieje: " + plikUkryty);
                return null;
            }

            // Odczytaj jawny plik
            String zawartoscJawna = new String(Files.readAllBytes(plikJawny));

            // Odczytaj ukryty plik
            String zawartoscUkryta = new String(Files.readAllBytes(plikUkryty));

            // Zweryfikuj checksumę
            if (!zweryfikujChecksume(zawartoscJawna, zawartoscUkryta)) {
                System.err.println("🚨 WYKRYTO MANIPULACJĘ! Checksumy się nie zgadzają!");
                return "BŁĄD_CHEKSUMY";
            }

            System.out.println("Plik zweryfikowany pomyślnie: " + plikJawny);
            return zawartoscJawna;

        } catch (IOException e) {
            System.err.println("Błąd podczas odczytu plików: " + e.getMessage());
            return null;
        }
    }

    /**
     * Aktualizuje oba pliki (jawny i ukryty)
     */
    public static boolean aktualizujPlikPcd(String sciezkaSwiat, String nowaZawartosc) {
        try {
            Path folderSwiat = Paths.get(sciezkaSwiat);

            // 1. Aktualizuj jawny plik
            Path plikJawny = folderSwiat.resolve(PLIK_JAWNY);
            Files.write(plikJawny, nowaZawartosc.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // 2. Oblicz nową checksumę
            String nowaChecksuma = obliczChecksume(nowaZawartosc);
            String nowaZawartoscUkryta = nowaZawartosc + "\nCHECKSUM:" + nowaChecksuma;

            // 3. Aktualizuj ukryty plik
            Path plikUkryty = folderSwiat.resolve(PLIK_UKRYTY);
            Files.write(plikUkryty, nowaZawartoscUkryta.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            ukryjPlik(plikUkryty);

            System.out.println("Zaktualizowano oba pliki: " + plikJawny);
            return true;

        } catch (IOException e) {
            System.err.println("Błąd podczas aktualizacji plików: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sprawdza integralność plików
     */
    public static boolean sprawdzIntegralnosc(String sciezkaSwiat) {
        try {
            Path folderSwiat = Paths.get(sciezkaSwiat);
            Path plikJawny = folderSwiat.resolve(PLIK_JAWNY);
            Path plikUkryty = folderSwiat.resolve(PLIK_UKRYTY);

            if (!Files.exists(plikJawny) || !Files.exists(plikUkryty)) {
                return false;
            }

            String zawartoscJawna = new String(Files.readAllBytes(plikJawny));
            String zawartoscUkryta = new String(Files.readAllBytes(plikUkryty));

            return zweryfikujChecksume(zawartoscJawna, zawartoscUkryta);

        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Oblicza checksumę SHA-256
     */
    private static String obliczChecksume(String dane) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(dane.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return "BŁĄD";
        }
    }

    /**
     * Weryfikuje checksumę
     */
    private static boolean zweryfikujChecksume(String zawartoscJawna, String zawartoscUkryta) {
        try {
            // Wyodrębnij checksumę z ukrytego pliku
            String[] lines = zawartoscUkryta.split("\n");
            String storedChecksum = null;

            for (int i = lines.length - 1; i >= 0; i--) {
                if (lines[i].startsWith("CHECKSUM:")) {
                    storedChecksum = lines[i].substring(9);
                    break;
                }
            }

            if (storedChecksum == null) {
                return false;
            }

            // Oblicz checksumę z jawnej zawartości
            String calculatedChecksum = obliczChecksume(zawartoscJawna);

            return storedChecksum.equals(calculatedChecksum);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ukrywa plik (działa tylko na Windows)
     */
    private static void ukryjPlik(Path plik) {
        try {
            // Dla Windows
            if (plik.getFileSystem().supportedFileAttributeViews().contains("dos")) {
                Files.setAttribute(plik, "dos:hidden", true);
            }
        } catch (Exception e) {
            // Ignoruj błędy ukrywania (może nie działać na Linux/Mac)
        }
    }

    /**
     * Sprawdza czy plik jest ukryty
     */
    private static boolean czyPlikUkryty(Path plik) {
        try {
            if (plik.getFileSystem().supportedFileAttributeViews().contains("dos")) {
                DosFileAttributes attrs = Files.readAttributes(plik, DosFileAttributes.class);
                return attrs.isHidden();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}