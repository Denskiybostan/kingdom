package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.util.Objects;
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "chat_Id")
    private long chatId;
    @Column(name = "name")
    private String name;
    @Column(name = "phone")
    private String phone;
    @Column(name = "login", nullable = false, unique = true)
    private String login;


    public User() {
    }
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    public User getUser() {
        return user;
    }

    public User(long chatId, long id, String login, String name, String phone) {
        this.chatId = chatId;
        this.id = id;
        this.login = login;
        this.name = name;
        this.phone = phone;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && chatId == user.chatId && Objects.equals(name, user.name) && Objects.equals(phone, user.phone) && Objects.equals(login, user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, name, phone, login);
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}
