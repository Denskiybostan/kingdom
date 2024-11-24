package pro.sky.telegrambot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.FileData;
import pro.sky.telegrambot.model.User;
import pro.sky.telegrambot.repository.FileRepository;
import pro.sky.telegrambot.repository.UserRepository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public UserService(UserRepository userRepository, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    public User findByUser(long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public User getUserByLogin(String login) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", login)
                .getSingleResult();
    }

    public void saveFile(String fileName, byte[] fileData, String userLogin) {
        User user = userRepository.findByLogin(userLogin);
        if (user == null) {
            throw new RuntimeException("Пользователь с логином " + userLogin + " не найден");
        }
        FileData file = new FileData(fileName, fileData, user);
        fileRepository.save(file);
    }

    public void saveUserSource(long chatId, String source) {
        User user = userRepository.findByChatId(chatId);
        if (user == null) {
            user = new User();
            user.setChatId(chatId);
        }
        user.setSource(source);
        userRepository.save(user);
    }
}
//    public void savePhoneNumberToDataBase(long chatId, String phone) {
//        System.out.println("Сохраняем номер в базу " + phone);
//        User user = userRepository.findByChatId(chatId);
//        if (user != null) {
//            System.out.println("Найден пользователь: " + user);
//            //если пользователь существует, обнвовляем номер телефона
//            user.setPhone(phone);
//            System.out.println("Сохранение номера телефона: " + phone );
//            userRepository.save(user); //сохраняем пользователя
//        } else {
//            System.out.println("Пользователь не найден");
//            user = new User();
//            user.setChatId(chatId);
//            user.setPhone(phone);
//            userRepository.save(user);
//            System.out.println("Создан новый пользователь с телефоном " + phone);
//        }
//    }

//    @Autowired
//    private final UserRepository userRepository;
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//
//    public UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public User findByUser(long chatId) {
//        return userRepository.findByChatId(chatId);
//    }
//
//    public User getUserByLogin(String login) {
//        return entityManager.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class).setParameter("login", login).getSingleResult();
//    }

////    @Transactional
//    public void addCurrency(long chatId, int amount) {
//        User user = userRepository.findByChatId(chatId);
//        if (user == null) {
//            throw new RuntimeException("Пользователь с chatId " + chatId + "не найден.");
//        }
//        user.setCurrencyBalance(user.getCurrencyBalance() + amount);
//        userRepository.save(user);
//    }
//
//    @Transactional
//    public void subtractCurrency(long chatId, int amount) {
//        User user = userRepository.findByChatId(chatId);
//        if (user == null) {
//            throw new RuntimeException("Пользователь с chatId " + chatId + " не найден.");
//        }
//        if (user.getCurrencyBalance() < amount) {
//            throw new RuntimeException("Недостаточно средств ");
//        }
//        user.setCurrencyBalance(user.getCurrencyBalance() - amount);
//        userRepository.save(user);
//    }
//
//    public int getCurrencyBalance(long chatId) {
//        User user = userRepository.findByChatId(chatId);
//        if (user == null) {
//            throw new RuntimeException("Пользователь с chatId " + chatId + " не найден");
//        }
//        return user.getCurrencyBalance();
//        }


