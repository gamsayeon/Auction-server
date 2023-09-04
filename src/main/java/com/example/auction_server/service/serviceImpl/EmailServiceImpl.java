package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.exception.CacheTTLOutException;
import com.example.auction_server.exception.EmailSendException;
import com.example.auction_server.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${expireUrl}")
    private static String url;
    private final JavaMailSender javaMailSender;

    private final Logger logger = LogManager.getLogger(EmailServiceImpl.class);
    private final RedisTemplate<String, String> redisTemplate;


    public EmailServiceImpl(JavaMailSender javaMailSender, RedisTemplate<String, String> redisTemplate) {
        this.javaMailSender = javaMailSender;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Cacheable(key = "#token", value = "userId")
    public String putCacheToken(String token, String userId) {
        return userId;
    }

    @Override
    public void sendToUser(String userId, String email) {
        String token = UUID.randomUUID().toString();
        this.putCacheToken(token, userId);
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = "<p>Please click the link below to verify your email:</p>" +
                    "<a href='" + url + token + "'>Click here to verify</a>";

            helper.setTo(email);
            helper.setSubject("Email Verification");
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            logger.warn("email 전송 실패");
            throw new EmailSendException("ERR_EMAIL_1");
        }
    }

    public String verifyEmail(String token) {
        String cachedUserId = this.getCachedToken(token);
        if (cachedUserId != null) {
            logger.info("redis value 값을 정상적으로 가지고 왔습니다.");
            return cachedUserId;
        } else {
            logger.warn("만료시간이 지나 다시시도해주세요");
            throw new CacheTTLOutException("ERR_EMAIL_2");
        }
    }

    public String getCachedToken(String token) {
        String key = "userId::" + token;
        String cachedValue = redisTemplate.opsForValue().get(key);
        return cachedValue;
    }

}
