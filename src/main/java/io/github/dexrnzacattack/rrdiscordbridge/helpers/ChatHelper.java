package io.github.dexrnzacattack.rrdiscordbridge.helpers;

import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ChatHelper {
    public static String allowedCharacters = readFontTxt();

    public static String readFontTxt() {
        StringBuilder read = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Bukkit.class.getResourceAsStream("/font.txt"), "UTF-8"))) {
            String line = "";

            while (line != null) {
                if (!line.startsWith("#")) {
                    read.append(line);
                }

                line = reader.readLine();
            }
        } catch (Exception e) {
            return null;
        }

        return read.toString();
    }

    public static boolean isAllowedCharacter(char character) {
        return character != '\167' && (allowedCharacters == null || allowedCharacters.indexOf(character) >= 0) && character >= ' ' && character != '\127';
    }

    public static String filterText(String str, boolean allowSpecial, char replacement) {
        StringBuilder stringbuilder = new StringBuilder();

        for (char c : str.toCharArray()) {
            if (isAllowedCharacter(c)) {
                stringbuilder.append(c);
            } else if (allowSpecial && (c == '\n' || c == '§')) {
                stringbuilder.append(c);
            } else {
                stringbuilder.append(replacement);
            }
        }

        return stringbuilder.toString();
    }
}
