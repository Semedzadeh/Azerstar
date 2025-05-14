package com.azerstar.util;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * Bu sinif TextField-lərə yalnız rəqəm daxil edilməsinə icazə verir.
 * Əgər nöqtə əvəzinə vergül yazılarsa, onu avtomatik olaraq nöqtəyə çevirir.
 * Baza üçün double formatına uyğunlaşdırır.
 */
public class NumberInputFilter {

    // Yalnız tam ədədlər üçün
    public static void allowOnlyIntegers(TextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            if (!character.matches("\\d")) {
                event.consume(); // yalnız rəqəmlərə icazə ver
            }
        });
    }

    // Double ədədlər üçün, , -> . düzəlişi ilə
    public static void allowOnlyDoubles(TextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();

            if (character.equals(",")) {
                // , əvəzinə . qoy
                event.consume();
                textField.appendText(".");
            } else if (!character.matches("\\d") && !character.equals(".")) {
                event.consume(); // yalnız rəqəm və nöqtə qəbul et
            }
        });

        // birdən çox "." yazılmasına icazə vermə
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.chars().filter(ch -> ch == '.').count() > 1) {
                textField.setText(oldValue);
            }
        });
    }

    // Baza üçün qiyməti double-a çevirmək (vergül varsa nöqtəyə çevriləcək)
    public static double parseToDouble(String input) {
        if (input == null || input.isEmpty()) return 0.0;
        input = input.replace(",", ".");
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // Baza üçün qiyməti int-ə çevirmək
    public static int parseToInt(String input) {
        if (input == null || input.isEmpty()) return 0;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
