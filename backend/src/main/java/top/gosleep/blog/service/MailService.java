package top.gosleep.blog.service;

public interface MailService {
    void sendText(String email, String title, String text);

    void sendVerifyCode(String email);
}
