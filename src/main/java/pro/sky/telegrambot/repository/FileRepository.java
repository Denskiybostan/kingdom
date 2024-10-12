package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.FileData;
import pro.sky.telegrambot.model.User;

import java.util.List;

public interface FileRepository  extends JpaRepository<FileData, Integer> {
    List<FileData> findByUser(User user);

    List<FileData> findAll();
}
