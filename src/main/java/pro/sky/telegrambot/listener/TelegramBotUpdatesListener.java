package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import pro.sky.telegrambot.configuration.ConfigurationKingdom;
import pro.sky.telegrambot.model.FileData;
import pro.sky.telegrambot.model.MenuBot;
import pro.sky.telegrambot.model.User;
import pro.sky.telegrambot.repository.FileRepository;
import pro.sky.telegrambot.repository.UserRepository;
import pro.sky.telegrambot.service.FileService;
import pro.sky.telegrambot.service.UserService;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener extends TelegramLongPollingBot {
    private Map<Long, String> userStates = new HashMap<>();
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\+7-9\\d{2}-\\d{3}-\\d{2}-\\d{2}");
//    private static final Pattern PHONE_PATTERN = Pattern.compile("\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}");
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private static final  List <Long> ADMIN_CHAT_ID = Arrays.asList(310232057L, 465693647L); // здесь добавляем нужный id

    @Autowired
    private TelegramBot telegramBot;
    private final ConfigurationKingdom kingdom;
    private UserService userService;
    private final UserRepository repository;
    private final FileRepository fileRepository;
    private final MenuBot menuBot;
    private final FileService fileService;

    public TelegramBotUpdatesListener(ConfigurationKingdom kingdom, UserRepository repository, UserService userService, FileRepository fileRepository, MenuBot menuBot, FileService fileService) {
        this.kingdom = kingdom;
        this.repository = repository;
        this.fileRepository = fileRepository;
        this.menuBot = menuBot;
        this.userService = userService;
        this.fileService = fileService;
    }

    @PostConstruct
    public void init() throws TelegramApiException {

        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String login = update.getMessage().getFrom().getUserName();
            var state = userStates.get(chatId);
            var user = userService.findByUser(chatId);

            if ("PhoneListener".equals(state)) {
                handleContactInput(chatId, text);
                userStates.remove(chatId);
            } else {
                switch (text) {
                    case "/start":
                        if (user == null) {
                            startCommandReceived(chatId, update.getMessage().getChat().getFirstName(), login);
                            break;
                        }
                    case "/menu":
                        menu(chatId);
                        break;
                    case "скачать":
                        handleFileDownloadCommand(chatId);
                        break;
                    case "Добавить номер телефона для дальнейшей связи":
                        writeDownContactPhoneNumber(chatId);
                        break;
                }
            }
        }
    }

    //Основная команда - /start
    private void startCommandReceived(long chatId, String name, String login) {
        var user = new User();
        user.setChatId(chatId);
        user.setName(name);
        user.setId(chatId);
        user.setId(user.getId());
        user.setLogin(login);
        repository.save(user);
        sendMessage(chatId, "Привет, " + name + ". Я бот, который поможет вам взаимодействовать с королевством" +
                "\n" + "Я могу рассказать вам о королевстве, его правилах и дальнейших действиях" +
                "\n" + "Жми скорее /menu");
    }


    //Метод, определяющий правильность номера телефона и позволяющий записать контактные данные в БД при корректном их написании
    private void handleContactInput(Long chatId, String text) {
        String digitsOnly = text.replaceAll("\\D", ""); // Извлекаем только цифры

        // Проверяем, что длина номера правильная (обычно 11 цифр, включая код страны)
        if (digitsOnly.length() == 11 && digitsOnly.startsWith("7")) {
            String formattedPhone = formatPhoneNumber(digitsOnly);

            var task = repository.findByChatId(chatId);
            task.setPhone(formattedPhone);
            repository.save(task);

            String fileName = "user_" + chatId + "_phone.txt";
            byte[] fileData = formattedPhone.getBytes();
            String userLogin = task.getLogin();
            fileService.saveFile(fileName, fileData, userLogin);

            sendMessage(chatId, "Номер телефона успешно сохранен! Нажмите кнопку /menu");
        } else {
            sendMessage(chatId, "Неверный формат номера телефона. Пожалуйста, введите номер в формате: +7-9**-***-**-**");
        }
    }

    private String formatPhoneNumber(String digitsOnly) {
        // Форматируем телефон: +7-XXX-XXX-XX-XX
        return "+7-"
                + digitsOnly.substring(1, 4) + "-"  // код оператора
                + digitsOnly.substring(4, 7) + "-"  // первые 3 цифры номера
                + digitsOnly.substring(7, 9) + "-"  // следующие 2 цифры
                + digitsOnly.substring(9);          // последние 2 цифры
    }

    private void writeDownContactPhoneNumber(long chatId) {
        String text = "Я могу записать Ваши контактные данные и в ближайшее время с Вами свяжется наш ассистент и проконсультируют Вас. " +
                "Введите номер телефона";
        sendMessage(chatId, text);
        userStates.put(chatId, "PhoneListener");
    }
    private void menu(long chatId) {
        String text = "выберите услугу";
        sendMessage2(chatId, text, menuBot.sendMainMenu(chatId));
    }

    //Метод, помогающий вывести сообщение пользователю
    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }
    }

    //Метод, помогающий вывести сообщение пользователю, а также кнопки меню
    private void sendMessage2(long chatId, String text, ReplyKeyboard replyMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(replyMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }
    }


    private void handleFileDownloadCommand(long chatId) {
        try {
            exportDataToDataFile(chatId);
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(chatId, "Ошибка при выгрузке файла");
        }
    }

    public void exportDataToDataFile(long chatId) {
        List<FileData> allFiles = fileRepository.findAll();
        System.out.println("Найденные файлы " + allFiles.size());
        if (allFiles.isEmpty()) {
            sendMessage(chatId, "Нет файлов для выгрузки");
            return;
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            for (FileData file : allFiles) {
                outputStream.write(("Имя файла: " + file.getFileName() + "\n").getBytes());
                System.out.println("Записываем файл " + file.getFileName());
                outputStream.write(file.getFileData());
                outputStream.write("\n\n".getBytes());
            }
            byte[] fullFileData = outputStream.toByteArray();
            sendFile(chatId, fullFileData, "database_export.txt");
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Ошибка при экспорте данных");
        }
    }

    private void sendFile(long chatId, byte[] fileData, String fileName) {
        if (fileData == null || fileData.length == 0) {
            System.out.println("File data is null or empty. Aborting file send.");
            sendMessage(chatId, "Файл пустой");
            return;
        }

        InputStream inputStream = new ByteArrayInputStream(fileData);
        InputFile inputFile = new InputFile(inputStream, fileName); // Создаем объект InputFile

        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(inputFile); // Устанавливаем файл

        try {
            execute(sendDocument); // Отправляем файл через API Telegram
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sendMessage(chatId, "Ошибка при отправке файла");
        }
    }
    @Override
    public String getBotUsername() {
        return kingdom.getName();
    }
    @Override
    public String getBotToken() {
        return kingdom.getToken();
    }

}
