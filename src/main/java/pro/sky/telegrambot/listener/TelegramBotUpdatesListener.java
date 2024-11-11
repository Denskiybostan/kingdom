package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
    public Map<Long, String> userStates = new HashMap<>();
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
        sendMessage(chatId,   name + " , приветствуем на территории Мастерской Игрового Психосинтеза" +
                "\n" +
                "Мы альтернативная психологическая онлайн-школа с авторским методом Аглаи Ваньгиной «квантовые игры» ");
        menu(chatId);
    }


    //Метод, определяющий правильность номера телефона и позволяющий записать контактные данные в БД при корректном их написании
    public void handleContactInput(Long chatId, String text) {
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
            sendMessage(chatId, "Неверный формат номера телефона. Пожалуйста, введите номер в формате: '79992221123' и повторно нажмите 'Добавить номер телефона для дальнейшей связи'");
            System.out.println("Версия кода обновлена до 1.1.3 ");
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

    public void writeDownContactPhoneNumber(long chatId) {
        String text = "Я могу записать Ваши контактные данные и в ближайшее время с Вами свяжется наш ассистент и проконсультирует Вас. " +
                "Введите номер телефона, начиная с 7 без плюса, например" + "\n" + "79998881122";
        sendMessage(chatId, text);
        userStates.put(chatId, "PhoneListener");
    }
    private void menu(long chatId) {
        String text = " ";
        sendMessage2(chatId, text, menuBot.sendMainMenu(chatId));
    }

    //Метод, помогающий вывести сообщение пользователю
    public void sendMessage(long chatId, String text) {
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
        List<User> allUsers = repository.findAll(); //здесь имеется ввиду репозиторий юзеров
        System.out.println("Найденные файлы " + allUsers.size());
        if (allUsers.isEmpty()) {
            sendMessage(chatId, "Нет файлов для выгрузки");
            return;
        }
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");
            //Создание заголовков столбцов
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Login");
            headerRow.createCell(1).setCellValue("Phone");
            //Заполнение данных
            int rowNum = 1;
            for (User user : allUsers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getLogin());
                row.createCell(1).setCellValue(user.getPhone());
            }
            workbook.write(outputStream);
            byte[] excelData = outputStream.toByteArray();
            sendFile(chatId, excelData, "user_data_export.xlsx");
        } catch (IOException | TelegramApiException e) {
            e.printStackTrace();
            sendMessage(chatId, "Ошибка при экспорте данных");
        }
    }

    public void sendFile(long chatId, byte[] fileData, String fileName) throws TelegramApiException {
        // Проверка на пустой файл
        if (fileData == null || fileData.length == 0) {
            System.out.println("File data is null or empty. Aborting file send.");
            sendMessage(chatId, "Файл пустой");
            return;
        }

        // Создаем InputFile
        try (InputStream inputStream = new ByteArrayInputStream(fileData)) {
            InputFile inputFile = new InputFile(inputStream, fileName); // Создаем объект InputFile

            // Создаем объект SendDocument
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(String.valueOf(chatId));
            sendDocument.setDocument(inputFile); // Устанавливаем файл

            System.out.println("SendDocument created: " + sendDocument);

            // Проверка на null
            if (sendDocument.getDocument() == null) {
                System.err.println("SendDocument document is null.");
                sendMessage(chatId, "Ошибка: документ не найден.");
                return;
            }

            // Отправляем документ
            execute(sendDocument); // Отправляем файл через API Telegram

        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Ошибка при отправке файла: " + e.getMessage());
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
