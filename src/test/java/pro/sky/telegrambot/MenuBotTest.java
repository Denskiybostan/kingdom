package pro.sky.telegrambot;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import pro.sky.telegrambot.model.MenuBot;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MenuBotTest {
    @Test
    public void testAdminMenu() {
        MenuBot menuBot = new MenuBot();
        ReplyKeyboardMarkup adminMenu = menuBot.sendMainMenu(310232057L);

        assertTrue(adminMenu.getKeyboard().stream().anyMatch(row -> row.contains("скачать")));
        ReplyKeyboardMarkup userMenu = menuBot.sendMainMenu(123456789L);

        assertFalse(userMenu.getKeyboard().stream().anyMatch(row -> row.contains("скачать")));
    }
}
