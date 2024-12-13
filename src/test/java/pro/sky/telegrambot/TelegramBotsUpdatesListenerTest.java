package pro.sky.telegrambot;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.telegrambot.configuration.ConfigurationKingdom;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.FileData;
import pro.sky.telegrambot.model.MenuBot;
import pro.sky.telegrambot.model.User;
import pro.sky.telegrambot.repository.FileRepository;
import pro.sky.telegrambot.repository.UserRepository;
import pro.sky.telegrambot.service.FileService;
import pro.sky.telegrambot.service.UserService;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class TelegramBotsUpdatesListenerTest {

    @Test
    public void testWriteDownContactPhoneNumber() {
        // Создание мока для TelegramBotUpdatesListener
        TelegramBotUpdatesListener telegramBotUpdatesListener = mock(TelegramBotUpdatesListener.class);
        doCallRealMethod().when(telegramBotUpdatesListener).writeDownContactPhoneNumber(anyLong());
        telegramBotUpdatesListener.userStates = new HashMap<>();
        long chatId = 310232057L;
        //String text = "Test message";

        // Вызываем метод, который должен вызвать sendMessage
        telegramBotUpdatesListener.writeDownContactPhoneNumber(chatId);

        // Проверяем, был ли вызван sendMessage с ожидаемыми параметрами
        verify(telegramBotUpdatesListener, times(1)).sendMessage(eq(chatId), anyString());
    }

//    @Test
//    public void testHandleContactInput_ValidPhoneNumber() {
//        UserRepository userRepository = mock(UserRepository.class);
//        FileRepository fileRepository = mock(FileRepository.class);
//        ConfigurationKingdom kingdom = mock(ConfigurationKingdom.class);
//        UserService userService = mock(UserService.class);
//        MenuBot menuBot = mock(MenuBot.class);
//        FileService fileService = mock(FileService.class);
//        TelegramBotUpdatesListener telegramBotUpdatesListener = spy(new TelegramBotUpdatesListener(kingdom, userRepository, userService, fileRepository, menuBot, fileService));
//
//        User testUser = new User(123456789L, 1l, "testLogin", "Test User", null, "Test city");
//        //Настройка поведения мока
//        when(userRepository.findByChatId(123456789L)).thenReturn(testUser);
//        doNothing().when(telegramBotUpdatesListener).sendMessage(anyLong(), anyString());
//        //Тестируем метод
//        String validPhoneNumber = "+79998881122"; //корректный номер телефона
//        telegramBotUpdatesListener.handleContactInput(123456789L, validPhoneNumber);
//        verify(userRepository, times(1)).save(testUser);
//        assertEquals("+7-999-888-11-22", testUser.getPhone());//имеет место, когда есть метод форматирования
//        //проверяем, что файл был сохранен
//        String expectedFileName = "user_123456789_phone.txt";
//        verify(fileService, times(1)).saveFile(eq(expectedFileName), any(byte[].class), eq(testUser.getLogin()));
//        // проверяем, что сообщение было отправлено
//        verify(telegramBotUpdatesListener, times(1)).sendMessage(123456789L, "Номер телефона успешно сохранен! Нажмите кнопку /menu");
//    }
//
//    @Test
//    public void testHandleContactInput_InvalidPhoneNumber() {
//        UserRepository userRepository = mock(UserRepository.class);
//        FileRepository fileRepository = mock(FileRepository.class);
//        ConfigurationKingdom kingdom = mock(ConfigurationKingdom.class);
//        UserService userService = mock(UserService.class);
//        MenuBot menuBot = mock(MenuBot.class);
//        FileService fileService = mock(FileService.class);
//        TelegramBotUpdatesListener telegramBotUpdatesListener = spy(new TelegramBotUpdatesListener(kingdom, userRepository, userService, fileRepository, menuBot, fileService));
//        //создаем тестового пользователя
//        User testUser = new User(123456789L, 1l, "testLogin", "Test User", null, "Test city");
//        //Настройка поведения мока
//        when(userRepository.findByChatId(123456789L)).thenReturn(testUser);
//        doNothing().when(telegramBotUpdatesListener).sendMessage(anyLong(), anyString());
//        //Тестируем метод
//        String invalidPhoneNumber = "12345"; //корректный номер телефона
//        telegramBotUpdatesListener.handleContactInput(123456789L, invalidPhoneNumber);
//        verify(userRepository, never()).save(any(User.class));
//        //проверка сообщения об ошибке
//        verify(telegramBotUpdatesListener, times(1)).sendMessage(123456789L, "Неверный формат номера телефона. Пожалуйста, введите номер в формате: '79992221123' и повторно нажмите 'Добавить номер телефона для дальнейшей связи'");
//    }
//
//    @Test
//    public void testHandleContactInput_TooManyDigits() {
//        UserRepository userRepository = mock(UserRepository.class);
//        FileRepository fileRepository = mock(FileRepository.class);
//        ConfigurationKingdom kingdom = mock(ConfigurationKingdom.class);
//        UserService userService = mock(UserService.class);
//        MenuBot menuBot = mock(MenuBot.class);
//        FileService fileService = mock(FileService.class);
//        TelegramBotUpdatesListener telegramBotUpdatesListener = spy(new TelegramBotUpdatesListener(kingdom, userRepository, userService, fileRepository, menuBot, fileService));
//        //создаем тестового пользователя
//        User testUser = new User(123456789L, 1l, "testLogin", "Test User", null, "Test city");
//        //Настройка поведения мока
//        when(userRepository.findByChatId(123456789L)).thenReturn(testUser);
//        doNothing().when(telegramBotUpdatesListener).sendMessage(anyLong(), anyString());
//
//        String tooManyDigitsPhoneNumber = "7999888112233"; //напишем 13 цифр вместо 11
//        telegramBotUpdatesListener.handleContactInput(123456789L, tooManyDigitsPhoneNumber);
//        //проверяем, что пользователь не был сохранен
//        verify(userRepository, never()).save(any(User.class));
//        //проверка на сообщение об ошибке
//        verify(telegramBotUpdatesListener, times(1)).sendMessage(123456789L, "Неверный формат номера телефона. Пожалуйста, введите номер в формате: '79992221123' и повторно нажмите 'Добавить номер телефона для дальнейшей связи'");
//    }
//
//    @Test
//    public void testHandleContactInput_TooShortPhoneNumber() {
//        UserRepository userRepository = mock(UserRepository.class);
//        FileRepository fileRepository = mock(FileRepository.class);
//        ConfigurationKingdom kingdom = mock(ConfigurationKingdom.class);
//        UserService userService = mock(UserService.class);
//        MenuBot menuBot = mock(MenuBot.class);
//        FileService fileService = mock(FileService.class);
//        TelegramBotUpdatesListener telegramBotUpdatesListener = spy(new TelegramBotUpdatesListener(kingdom, userRepository, userService, fileRepository, menuBot, fileService));
//        //создаем тестового пользователя
//        User testUser = new User(123456789L, 1l, "testLogin", "Test User", null, "Test city");
//        //Настройка поведения мока
//        when(userRepository.findByChatId(123456789L)).thenReturn(testUser);
//        doNothing().when(telegramBotUpdatesListener).sendMessage(anyLong(), anyString());
//
//        String tooManyDigitsPhoneNumber = "799988811"; //напишем 13 цифр вместо 11
//        telegramBotUpdatesListener.handleContactInput(123456789L, tooManyDigitsPhoneNumber);
//        //проверяем, что пользователь не был сохранен
//        verify(userRepository, never()).save(any(User.class));
//        //проверка на сообщение об ошибке
//        verify(telegramBotUpdatesListener, times(1)).sendMessage(123456789L, "Неверный формат номера телефона. Пожалуйста, введите номер в формате: '79992221123' и повторно нажмите 'Добавить номер телефона для дальнейшей связи'");
//    }
//    @Test
//    public void testHandleContactInput_PhoneNumberWithNonNumericCharacters() {
//        UserRepository userRepository = mock(UserRepository.class);
//        FileRepository fileRepository = mock(FileRepository.class);
//        ConfigurationKingdom kingdom = mock(ConfigurationKingdom.class);
//        UserService userService = mock(UserService.class);
//        MenuBot menuBot = mock(MenuBot.class);
//        FileService fileService = mock(FileService.class);
//        TelegramBotUpdatesListener telegramBotUpdatesListener = spy(new TelegramBotUpdatesListener(kingdom, userRepository, userService, fileRepository, menuBot, fileService));
//        //создаем тестового пользователя
//        User testUser = new User(123456789L, 1l, "testLogin", "Test User", null, "Test city");
//        //Настройка поведения мока
//        when(userRepository.findByChatId(123456789L)).thenReturn(testUser);
//        doNothing().when(telegramBotUpdatesListener).sendMessage(anyLong(), anyString());
//
//        String tooManyDigitsPhoneNumber = "799988811sf"; //напишем 13 цифр вместо 11
//        telegramBotUpdatesListener.handleContactInput(123456789L, tooManyDigitsPhoneNumber);
//        //проверяем, что пользователь не был сохранен
//        verify(userRepository, never()).save(any(User.class));
//        //проверка на сообщение об ошибке
//        verify(telegramBotUpdatesListener, times(1)).sendMessage(123456789L, "Неверный формат номера телефона. Пожалуйста, введите номер в формате: '79992221123' и повторно нажмите 'Добавить номер телефона для дальнейшей связи'");
//    }
//    @Test
//    public void testHandleContactInput_EmptyOrNullPhoneNumber() {
//        UserRepository userRepository = mock(UserRepository.class);
//        FileRepository fileRepository = mock(FileRepository.class);
//        ConfigurationKingdom kingdom = mock(ConfigurationKingdom.class);
//        UserService userService = mock(UserService.class);
//        MenuBot menuBot = mock(MenuBot.class);
//        FileService fileService = mock(FileService.class);
//        TelegramBotUpdatesListener telegramBotUpdatesListener = spy(new TelegramBotUpdatesListener(kingdom, userRepository, userService, fileRepository, menuBot, fileService));
//        //создаем тестового пользователя
//        User testUser = new User(123456789L, 1l, "testLogin", "Test User", null, "Test city");
//        //Настройка поведения мока
//        when(userRepository.findByChatId(123456789L)).thenReturn(testUser);
//        doNothing().when(telegramBotUpdatesListener).sendMessage(anyLong(), anyString());
//
//        String tooManyDigitsPhoneNumber = ""; //напишем 13 цифр вместо 11
//        telegramBotUpdatesListener.handleContactInput(123456789L, tooManyDigitsPhoneNumber);
//        //проверяем, что пользователь не был сохранен
//        verify(userRepository, never()).save(any(User.class));
//        //проверка на сообщение об ошибке
//        verify(telegramBotUpdatesListener, times(1)).sendMessage(123456789L, "Неверный формат номера телефона. Пожалуйста, введите номер в формате: '79992221123' и повторно нажмите 'Добавить номер телефона для дальнейшей связи'");
//    }

//    @Test
//    public void testExportDataToFile_Success() throws TelegramApiException {
//        // Создание моков
//        UserRepository userRepository = mock(UserRepository.class);
//        FileRepository fileRepository = mock(FileRepository.class);
//        ConfigurationKingdom kingdom = mock(ConfigurationKingdom.class);
//        UserService userService = mock(UserService.class);
//        MenuBot menuBot = mock(MenuBot.class);
//        FileService fileService = mock(FileService.class);
//        TelegramBotUpdatesListener telegramBotUpdatesListener =
//                spy(new TelegramBotUpdatesListener(kingdom, userRepository, userService, fileRepository, menuBot, fileService));
//        // Данные для теста
//        long chatId = 123456789L;
//        String fileName = "test_file.txt";
//        byte[] fileData = "Test file content".getBytes();
//        // Настройка мока для метода execute
//        doNothing().when(telegramBotUpdatesListener).execute(any(SendDocument.class));
//        // Вызов метода, который тестируем
//        telegramBotUpdatesListener.sendFile(chatId, fileData, fileName);
//        // Проверка, что метод execute был вызван один раз с SendDocument, содержащим правильные параметры
//        ArgumentCaptor<SendDocument> captor = ArgumentCaptor.forClass(SendDocument.class);
//        verify(telegramBotUpdatesListener, times(1)).execute(captor.capture());
//        SendDocument sentDocument = captor.getValue();
//        assertNotNull(sentDocument);
//        assertEquals(String.valueOf(chatId), sentDocument.getChatId());
//        assertNotNull(sentDocument.getDocument());
//        assertEquals(fileName, sentDocument.getDocument().getMediaName());
//    }
    @Test
    public void testSendDocumentCreation() {
        long chatId = 123456789L;
        String fileName = "test_file.txt";
        byte[] fileData = "Test file content".getBytes();

        InputFile inputFile = new InputFile(new ByteArrayInputStream(fileData), fileName);
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(String.valueOf(chatId));
        sendDocument.setDocument(inputFile);

        assertEquals(String.valueOf(chatId), sendDocument.getChatId());
        assertEquals(fileName, sendDocument.getDocument().getMediaName());
    }
    }
//    @Test
//    public void testSendFile_ExceptionHandling() throws TelegramApiException {
//        UserRepository userRepository = mock(UserRepository.class);
//        FileRepository fileRepository = mock(FileRepository.class);
//        ConfigurationKingdom kingdom = mock(ConfigurationKingdom.class);
//        UserService userService = mock(UserService.class);
//        MenuBot menuBot = mock(MenuBot.class);
//        FileService fileService = mock(FileService.class);
//
//        // Создаем шпион на TelegramBotUpdatesListener
//        TelegramBotUpdatesListener telegramBotUpdatesListener = spy(new TelegramBotUpdatesListener(kingdom, userRepository, userService, fileRepository, menuBot, fileService));
//
//        long chatId = 123456789L;
//        String fileName = "test.txt";
//        byte[] fileData = "Test file content".getBytes();
//
//        // Настраиваем поведение метода execute с проверкой объекта SendDocument
//        doAnswer(invocation -> {
//            SendDocument sentDocument = invocation.getArgument(0);
//            assertNotNull(sentDocument); // Убедитесь, что объект не null
//            assertEquals(chatId, sentDocument.getChatId()); // Проверка chatId
//            assertEquals(fileName, sentDocument.getDocument().getFileName()); // Проверка имени файла
//            throw new TelegramApiException("Test exception"); // выбрасываем исключение
//        }).when(telegramBotUpdatesListener).execute(any(SendDocument.class));
//
//        // Вызываем метод sendFile, который будет тестироваться
//        telegramBotUpdatesListener.sendFile(chatId, fileData, fileName);
//
//        // Проверяем, что было вызвано сообщение об ошибке
//        verify(telegramBotUpdatesListener, times(1)).sendMessage(chatId, "Ошибка при отправке файла");
//    }
//    @Test
//    public void testSendFileCalledWithCorrectParameters() throws TelegramApiException {
//        UserRepository userRepository = mock(UserRepository.class);
//        FileRepository fileRepository = mock(FileRepository.class);
//        ConfigurationKingdom kingdom = mock(ConfigurationKingdom.class);
//        UserService userService = mock(UserService.class);
//        MenuBot menuBot = mock(MenuBot.class);
//        FileService fileService = mock(FileService.class);
//
//        // Создаем шпион на TelegramBotUpdatesListener
//        TelegramBotUpdatesListener telegramBotUpdatesListener = spy(new TelegramBotUpdatesListener(kingdom, userRepository, userService, fileRepository, menuBot, fileService));
//        // Arrange
//        long chatId = 123456789L;
//        byte[] fileData = "Test file content".getBytes();
//        String expectedFileName = "test.txt";
//
//        // Act
//        telegramBotUpdatesListener.sendFile(chatId, fileData, expectedFileName);
//
//        // Assert
//        verify(telegramBotUpdatesListener, times(1)).sendFile(eq(chatId), any(byte[].class), eq(expectedFileName));
//    }

