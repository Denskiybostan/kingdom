package pro.sky.telegrambot;

import org.junit.jupiter.api.Test;
import pro.sky.telegrambot.model.FileData;
import pro.sky.telegrambot.model.User;
import pro.sky.telegrambot.repository.FileRepository;
import pro.sky.telegrambot.repository.UserRepository;
import pro.sky.telegrambot.service.UserService;

import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Test
    public void testSaveFile() {
        UserRepository userRepository = mock(UserRepository.class);
        FileRepository fileRepository = mock(FileRepository.class);
        UserService userService = new UserService(userRepository, fileRepository);

        User testUser = new User(123456789L, 1l, "testLogin", "Test User",  "+71234567890", "Test city");

        when(userRepository.findByLogin("testLogin")).thenReturn(testUser);
        String fileName = "testFile.txt";
        byte[] fileData = "Hello, World!".getBytes();
        userService.saveFile(fileName, fileData, "testLogin");

        verify(fileRepository, times(1)).save(any(FileData.class));
    }
}
