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
    @Column(name = "city", nullable = true)
    private String city;


    public User() {
    }
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    public User getUser() {
        return user;
    }

    public User(long chatId, long id, String login, String name, String phone, String city) {
        this.chatId = chatId;
        this.id = id;
        this.login = login;
        this.name = name;
        this.phone = phone;
        this.city = city;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && chatId == user.chatId && Objects.equals(name, user.name) && Objects.equals(phone, user.phone) && Objects.equals(login, user.login) && Objects.equals(city, user.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, name, phone, login, city);
    }

    @Override
    public String toString() {
        return "User{" +
                "phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", login='" + login + '\'' +
                ", id=" + id +
                ", city='" + city + '\'' +
                ", chatId=" + chatId +
                '}';
    }
}
