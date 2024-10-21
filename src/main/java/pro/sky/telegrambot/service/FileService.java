package pro.sky.telegrambot.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.FileData;
import pro.sky.telegrambot.model.User;
import pro.sky.telegrambot.repository.FileRepository;
import pro.sky.telegrambot.repository.UserRepository;

import java.util.List;


@Service
public class FileService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileRepository fileRepository;
    public void saveFile(String fileName, byte[]fileData, String userLogin) {
        User user = userRepository.findByLogin(userLogin);
        if (user == null) {
            throw new RuntimeException("Пользователь с логином " + userLogin + " не найден");
        }
        FileData file = new FileData(fileName, fileData, user);
        fileRepository.save(file);
    }
    public byte[] loadFile(String fileName) {
        return null;
    }


}
