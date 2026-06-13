package top.gosleep.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import top.gosleep.blog.bean.entity.User;
import top.gosleep.blog.common.BusinessException;
import top.gosleep.blog.common.ResultCode;
import top.gosleep.blog.common.context.SessionContext;
import top.gosleep.blog.common.context.SessionKey;
import top.gosleep.blog.common.context.UserContext;
import top.gosleep.blog.mapper.UserMapper;
import top.gosleep.blog.service.MailService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageServiceImpl.class);
    private static final long SEND_DURATION = 30;

    @Value("${spring.mail.username}")
    private String mailUsername;
    @Value("${spring.mail.password}")
    private String mailPassword;

    private final JavaMailSender mailSender;
    private final UserMapper userMapper;

    public MailServiceImpl(JavaMailSender mailSender, UserMapper userMapper) {
        this.mailSender = mailSender;
        this.userMapper = userMapper;
    }

    @Override
    public void sendText(String email, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailUsername);
        message.setTo(email);
        message.setSubject(title);
        message.setText(text);
        mailSender.send(message);
    }

    @Override
    public void sendVerifyCode(String email) {
        if (UserContext.getUser() != null)
            throw new BusinessException(ResultCode.FORBIDDEN, "请先登出");

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        if (user != null)
            throw new BusinessException(ResultCode.FORBIDDEN, "邮箱已被占用");

        SessionContext context = SessionContext.current();
        LocalDateTime last = context.get(SessionKey.VERIFY_SENT_TIME),
                      now = LocalDateTime.now();
        if (last != null) {
            long pass = Duration.between(last, now).getSeconds();
            if (pass < SEND_DURATION)
                throw new BusinessException(ResultCode.FORBIDDEN, "还需" + (SEND_DURATION - pass) + "秒才能重新发送验证码");
        }
        String code = generateVerifyCode();
        context.set(SessionKey.VERIFY_EMAIL, email);
        context.set(SessionKey.VERIFY_SENT_TIME, now);
        context.set(SessionKey.VERIFY_CODE, code);
        try {
            sendText(email, "验证码", String.format("你的验证码是：\n%s\n请不要告诉任何人！", code));
        } catch (MailAuthenticationException e) {
            log.error(String.format("验证用邮箱:\nusername:%s\npassword:%s\nerror:%s",
                    mailUsername, mailPassword, e.getMessage()));
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "请检查邮箱是否存在");
        }

    }

    private String generateVerifyCode() {
        int raw = ThreadLocalRandom.current().nextInt(1 << 24);// 16 ^ 6
        return String.format("%06X", raw);
    }
}
