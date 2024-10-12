package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.User;
import pro.sky.telegrambot.repository.UserRepository;
import java.util.List;

@Service
public class Sheduler {

    private static final Logger log = LoggerFactory.getLogger(Sheduler.class);
    private final TelegramBot telegramBot;
    private final UserRepository userRepository;
    private static final long ADMIN_CHAT_ID = 310232057L;

    public Sheduler(TelegramBot telegramBot,  UserRepository userRepository) {
        this.telegramBot = telegramBot;
        this.userRepository = userRepository;
    }


    @Scheduled(cron = "0 5 20 * * *")
    public void  adminRun() {
        final String ADMIN_ID = String.valueOf(310232057);
        try {
            telegramBot.execute(new SendMessage(ADMIN_ID, "*Ежедневная информация*"));
        } catch (RuntimeException e) {
            log.error("ADMIN_ID does`t used");
        }
    }

}



