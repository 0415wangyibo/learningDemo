package com.wangyb.ftpdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/30 15:08
 * Modified By:
 * Description:
 */
@Service
@Slf4j
public class SendMailService {

    private final JavaMailSenderImpl mailSender;

    @Autowired
    public SendMailService(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 向单个或者多个邮箱发只含Text邮件
     *
     * @param subject  主题
     * @param text     内容
     * @param sender   发送者
     * @param receiver 接收者
     */
    public void sendTextMail(String subject, String text, String sender, String... receiver) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        //如果没有填写接收通知信息的邮箱，则不发送
        if (receiver == null || receiver.length < 1) {
            return;
        }
        // 可写入多个接收者
        simpleMailMessage.setTo(receiver);
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);
        mailSender.send(simpleMailMessage);
        log.info("通知邮件已发送");
    }

    /**
     * 向多个邮箱发送html格式邮件
     *
     * @param subject   主题
     * @param html      html
     * @param sender    发送者
     * @param receivers 接收者
     * @throws Exception 异常
     */
    public void sendHtmlMails(String subject, String html, String sender, String[] receivers) throws Exception {

        if (receivers == null || receivers.length < 1) {
            return;
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(receivers);
        mimeMessageHelper.setFrom(sender);
        mimeMessageHelper.setSubject(subject);

        // 启用html
        mimeMessageHelper.setText(html, true);
        // 发送邮件
        mailSender.send(mimeMessage);

        log.info("邮件已发送");
    }
}
