package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.telegrambot.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByChatId(long chatId);
    User findByLogin(String login);
    @Query(value = "select * from users where data >= DATE('now', '-2 days')", nativeQuery = true)
    List<User> getOwnersAfterTwoDaysReport();

}
