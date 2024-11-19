package pro.sky.telegrambot.model;


import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MenuBot {
    private static final  List <Long> ADMIN_CHAT_ID = Arrays.asList(310232057L, 465693647L); // здесь добавляем нужный id
    public ReplyKeyboardMarkup sendMainMenu(long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Добавить номер телефона для дальнейшей связи");
        row1.add("Добавить город проживания");
        keyboardRows.add(row1);
        if (isAdmin(chatId)) {
            KeyboardRow adminRow = new KeyboardRow();
            adminRow.add("скачать");
            keyboardRows.add(adminRow);
        }

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
    private boolean isAdmin(long chatId) {
        return ADMIN_CHAT_ID.contains(chatId);
    }
}
