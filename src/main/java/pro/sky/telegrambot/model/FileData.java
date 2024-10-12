package pro.sky.telegrambot.model;

import javax.persistence.*;

@Entity
@Table(name = "files")
public class FileData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private int fileId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Lob
    @Column(name = "file_data", nullable = false)
    private byte[] fileData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Конструкторы
    public FileData() {

    }

    public FileData(String fileName, byte[] fileData, User user) {
        this.fileName = fileName;
        this.fileData = fileData;
        this.user = user;
    }

    // Геттеры и сеттеры
    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
